package com.turnkey.crypto.internal

import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.security.MessageDigest
import com.turnkey.crypto.utils.CryptoError
import com.turnkey.crypto.utils.TurnkeyConstants
import com.turnkey.encoding.decodeHex

/**
 * Verifies an ECDSA signature from the Turnkey enclave.
 *
 * @param enclaveQuorumPublic Hex-encoded uncompressed P-256 public key (65 bytes)
 * @param publicSignature Hex-encoded DER signature (ECDSA r,s)
 * @param signedData Hex-encoded payload that was signed
 * @param dangerouslyOverrideSignerPublicKey Optional override for testing (do not use in production)
 * @return true if signature is valid
 * @throws CryptoError if signature verification fails or signer mismatch
 */
@Throws(CryptoError::class)
internal fun verifyEnclaveSignature(
    enclaveQuorumPublic: String,   // hex, X9.62 uncompressed point (65 bytes for P-256)
    publicSignature: String,       // hex, DER-encoded ECDSA signature (r,s)
    signedData: String,            // hex payload that was signed
    dangerouslyOverrideSignerPublicKey: String? = null
): Boolean {
    val expectedKey = dangerouslyOverrideSignerPublicKey ?: TurnkeyConstants.PRODUCTION_SIGNER_PUBLIC_KEY
    if (enclaveQuorumPublic != expectedKey) {
        throw CryptoError.SignerMismatch(expected = expectedKey, found = enclaveQuorumPublic)
    }

    val pubKeyBytes = try { decodeHex(enclaveQuorumPublic) }
    catch (_: Throwable) { throw CryptoError.InvalidHexString(enclaveQuorumPublic) }

    val sigDer = try { decodeHex(publicSignature) }
    catch (_: Throwable) { throw CryptoError.InvalidHexString(publicSignature) }

    val payload = try { decodeHex(signedData) }
    catch (_: Throwable) { throw CryptoError.InvalidHexString(signedData) }

    val publicKey = try { p256PublicKeyFromX963(pubKeyBytes) }
    catch (e: Throwable) { throw CryptoError.InvalidPublicKey(e) }

    // use "NONEwithECDSA" and feed the digest bytes.
    val digest = MessageDigest.getInstance("SHA-256").digest(payload)

    val verifier = try {
        Signature.getInstance("NONEwithECDSA")   // expects DER (r,s); no hashing
    } catch (_: Exception) {
        // Fallback if provider lacks NONEwithECDSA: verify over the *message* instead.
        // IMPORTANT: This only matches if the signer used "SHA256withECDSA" on the message.
        Signature.getInstance("SHA256withECDSA").also {
            it.initVerify(publicKey)
            it.update(payload)
            return it.verify(sigDer)
        }
    }

    verifier.initVerify(publicKey)
    verifier.update(digest)
    return verifier.verify(sigDer)
}

/**
 * Build a P-256 (secp256r1) public key from X9.62 uncompressed bytes: 0x04 || X(32) || Y(32)
 */
private fun p256PublicKeyFromX963(x962: ByteArray): PublicKey {
    require(x962.size == 65 && x962[0] == 0x04.toByte()) {
        "Expected 65-byte uncompressed X9.62 point for P-256"
    }
    val x = BigInteger(1, x962.copyOfRange(1, 33))
    val y = BigInteger(1, x962.copyOfRange(33, 65))
    val point = ECPoint(x, y)

    val params = AlgorithmParameters.getInstance("EC").apply {
        init(ECGenParameterSpec("secp256r1"))
    }.getParameterSpec(ECParameterSpec::class.java)

    val spec = ECPublicKeySpec(point, params)
    return KeyFactory.getInstance("EC").generatePublic(spec)
}