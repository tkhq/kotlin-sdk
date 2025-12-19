package com.turnkey.crypto

import com.turnkey.encoding.base58CheckDecode
import com.turnkey.encoding.decodeHex
import com.turnkey.encoding.decodeUtf8Strict
import com.turnkey.encoding.base58CheckEncode
import com.turnkey.encoding.toHexString
import com.turnkey.crypto.internal.P256
import com.turnkey.crypto.internal.bigIntToFixed
import com.turnkey.crypto.internal.decodeBundleOuter
import com.turnkey.crypto.internal.decodeSignedInner
import com.turnkey.crypto.internal.deriveEd25519PublicKey
import com.turnkey.crypto.internal.ecPointCompressedToUncompressed
import com.turnkey.crypto.internal.hpkeDecrypt
import com.turnkey.crypto.internal.hpkeEncrypt
import com.turnkey.crypto.internal.verifyEnclaveSignature
import com.turnkey.crypto.models.KeyFormat
import com.turnkey.crypto.models.P256KeyPair
import com.turnkey.crypto.models.RawP256KeyPair
import com.turnkey.crypto.utils.CryptoError
import kotlinx.serialization.json.Json
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec
import kotlin.jvm.Throws

/**
 * Generates a new P-256 (secp256r1) key pair.
 *
 * @return RawP256KeyPair containing the public key in both uncompressed and compressed formats,
 *         along with the private key scalar in hex format.
 */
fun generateP256KeyPair(): RawP256KeyPair{
    val kpg = KeyPairGenerator.getInstance("EC")
    kpg.initialize(ECGenParameterSpec("secp256r1"))
    val keyPair = kpg.generateKeyPair()

    val priv = keyPair.private as ECPrivateKey
    val pub = keyPair.public as ECPublicKey

    val coordLen = (pub.params.curve.field.fieldSize + 7) / 8

    val d = bigIntToFixed(priv.s, coordLen)
    val x = bigIntToFixed(pub.w.affineX, coordLen)
    val y = bigIntToFixed(pub.w.affineY, coordLen)

    // Uncompressed: 0x04 || X || Y
    val uncompressed = ByteArray(1 + 2 * coordLen).apply {
        this[0] = 0x04
        System.arraycopy(x, 0, this, 1, coordLen)
        System.arraycopy(y, 0, this, 1 + coordLen, coordLen)
    }

    // Compressed: 0x02 (Y even) or 0x03 (Y odd) || X
    val prefix: Byte = if (pub.w.affineY.testBit(0)) 0x03 else 0x02
    val compressed = ByteArray(1 + coordLen).apply {
        this[0] = prefix
        System.arraycopy(x, 0, this, 1, coordLen)
    }

    return RawP256KeyPair(
        publicKeyUncompressed = uncompressed.toHexString(),
        publicKeyCompressed   = compressed.toHexString(),
        privateKey            = d.toHexString()
    )
}

/**
 * Decrypts a credential bundle encrypted with HPKE.
 *
 * @param encryptedBundle Base58Check-encoded bundle containing the encapsulated key and ciphertext.
 * @param ephemeralPrivateKey The ephemeral P-256 private key used for decryption.
 * @return P256KeyPair containing the decrypted credential as Java EC key pair.
 * @throws CryptoError if decryption fails or bundle format is invalid.
 */
@Throws(CryptoError::class)
fun decryptCredentialBundle(
    encryptedBundle: String,
    ephemeralPrivateKey: ECPrivateKey
): P256KeyPair = try {
    val decoded = base58CheckDecode(encryptedBundle)
    if (decoded.size <= 33) throw CryptoError.InvalidCompressedKeyLength(decoded.size)

    val compressedEncapped = decoded.copyOfRange(0, 33)
    val ciphertext = decoded.copyOfRange(33, decoded.size)

    // Decompress to X9.62 uncompressed for HPKE
    val encappedUncompressed = P256.decompress(compressedEncapped)

    // extract scalar value (s) and left-pad to 32 bytes
    val receiverPrivScalar = bigIntToFixed(ephemeralPrivateKey.s, 32)
    val plaintext = hpkeDecrypt(
        ciphertext = ciphertext,
        encappedUncompressed = encappedUncompressed,
        receiverPrivScalar = receiverPrivScalar,
    )

    // Interpret plaintext as raw P-256 signing private key, derive pub:
    if (plaintext.size != 32) throw CryptoError.InvalidPrivateLength(32, plaintext.size)

    // Build ECPrivateKey on secp256r1
    val ecSpec: ECParameterSpec = AlgorithmParameters.getInstance("EC")
        .apply { init(ECGenParameterSpec("secp256r1")) }
        .getParameterSpec(ECParameterSpec::class.java)

    val d = BigInteger(1, plaintext)
    val kf = KeyFactory.getInstance("EC")

    val privKey = kf.generatePrivate(ECPrivateKeySpec(d, ecSpec)) as ECPrivateKey

    val q = P256.publicFromScalar(plaintext).q.normalize()
    val pubSpec = ECPublicKeySpec(
        ECPoint(q.affineXCoord.toBigInteger(), q.affineYCoord.toBigInteger()),
        ecSpec
    )
    val pubKey = kf.generatePublic(pubSpec) as ECPublicKey

    P256KeyPair(publicKey = pubKey, privateKey = privKey)
} catch (e: Exception) {
    throw CryptoError.wrap(e)
}

/**
 * Decrypts an export bundle and returns either a hex string or mnemonic depending on configuration.
 *
 * @param exportBundle A signed and encrypted bundle from Turnkey's enclave.
 * @param organizationId The expected organization ID to verify against.
 * @param embeddedPrivateKey The raw embedded private key in hex format (32-byte P-256 scalar, big-endian).
 * @param dangerouslyOverrideSignerPublicKey Optional override of the signer public key (for dev/test).
 * @param keyFormat The output format for Solana or other keys.
 * @return The decrypted payload as either a mnemonic or hex string.
 * @throws CryptoError if any validation, decoding, or decryption fails.
 */
@Throws(CryptoError::class)
fun decryptExportBundle(
    exportBundle: String,
    organizationId: String,
    embeddedPrivateKey: String,
    dangerouslyOverrideSignerPublicKey: String? = null,
    keyFormat: KeyFormat? = null,
    returnMnemonic: Boolean = false,
): String = try {
    val keyBytes = try {
        decodeHex(embeddedPrivateKey)
    }
    catch (_: Exception) { throw CryptoError.InvalidHexString(embeddedPrivateKey) }

    // Basic sanity check: P-256 private key should be 32 bytes.
    if (keyBytes.size != 32) throw CryptoError.InvalidHexString(embeddedPrivateKey)

    val d = BigInteger(1, keyBytes)

    // Build ECPrivateKey on secp256r1
    val ecSpec: ECParameterSpec = AlgorithmParameters.getInstance("EC")
        .apply { init(ECGenParameterSpec("secp256r1")) }
        .getParameterSpec(ECParameterSpec::class.java)

    val ecPrivate: ECPrivateKey = try {
        val spec = ECPrivateKeySpec(d, ecSpec)
        KeyFactory.getInstance("EC").generatePrivate(spec) as ECPrivateKey
    } catch (e: Exception) {
        throw CryptoError.InvalidPrivateKey(e)
    }

    // Parse outer JSON
    val outer = decodeBundleOuter(exportBundle)

    val ok = verifyEnclaveSignature(
        outer.enclaveQuorumPublic,
        outer.dataSignature,
        outer.data,
        dangerouslyOverrideSignerPublicKey
    )
    if (!ok) throw CryptoError.SignatureVerificationFailed()

    val inner = decodeSignedInner(outer.data)

    if (inner.organizationId != organizationId) {
        throw CryptoError.OrgIdMismatch(organizationId, inner.organizationId)
    }

    val encappedHex = inner.encappedPublic ?: throw CryptoError.MissingEncappedPublic()
    val ciphertextHex = inner.ciphertext ?: throw CryptoError.MissingCiphertext()

    val ct = decodeHex(ciphertextHex)
    val ek = decodeHex(encappedHex)

    // extract scalar value (s) and left-pad to 32 bytes
    val receiverPrivScalar = bigIntToFixed(ecPrivate.s, 32)
    val plaintext = hpkeDecrypt(ct, ek, receiverPrivScalar)

    when {
        keyFormat == KeyFormat.solana && !returnMnemonic -> {
            if (plaintext.size != 32) throw CryptoError.InvalidPrivateLength(32, plaintext.size)

            val pubKey = deriveEd25519PublicKey(plaintext)
            if (pubKey.size != 32) throw CryptoError.InvalidPublicLength(32, pubKey.size)

            base58CheckEncode(plaintext + pubKey)
        }
        returnMnemonic -> {
            val mnemonic = decodeUtf8Strict(plaintext)
            mnemonic
        }
        else -> {
            plaintext.toHexString()
        }
    }
} catch (e: Exception) {
    throw CryptoError.wrap(e)
}

/**
 * Encrypts a mnemonic into a bundle using the import payload
 *
 *  @param mnemonic The plaintext mnemonic string to encrypt.
 *  @param importBundle The enclave-generated bundle to use for encryption.
 *  @param userId The expected user ID to verify against.
 *  @param organizationId The expected organization ID to verify against.
 *  @param dangerouslyOverrideSignerPublicKey Optional override of the signer public key (for dev/test).
 *  @return The encrypted bundle as a JSON string.
 *  @throws CryptoError if validation or encryption fails.
 */
@Throws(CryptoError::class)
fun encryptWalletToBundle(
    mnemonic: String,
    importBundle: String,
    userId: String,
    organizationId: String,
    dangerouslyOverrideSignerPublicKey: String?
): String = try {
    val outer = decodeBundleOuter(importBundle)

    val ok = verifyEnclaveSignature(
        outer.enclaveQuorumPublic,
        outer.dataSignature,
        outer.data,
        dangerouslyOverrideSignerPublicKey
    )
    if (!ok) throw CryptoError.SignatureVerificationFailed()

    val inner = decodeSignedInner(outer.data)

    if (inner.organizationId != organizationId) {
        throw CryptoError.OrgIdMismatch(organizationId, inner.organizationId)
    }
    if (inner.userId != userId) {
        throw CryptoError.UserIdMismatch(userId, inner.userId)
    }

    val targetHex = inner.targetPublic ?: throw CryptoError.MissingEncappedPublic()

    val plaintext = mnemonic.toByteArray(StandardCharsets.UTF_8)

    val bundleBytes = hpkeEncrypt(plaintext, targetHex)

    if (bundleBytes.size <= 33) throw CryptoError.InvalidCompressedKeyLength(plaintext.size)

    val compressed = bundleBytes.copyOfRange(0, 33)
    val cipher = bundleBytes.copyOfRange(33, bundleBytes.size)

    val uncompressedPub = ecPointCompressedToUncompressed(compressed)
    val json = mapOf("encappedPublic" to uncompressedPub.toHexString(), "ciphertext" to cipher.toHexString())
    Json.encodeToString(json)
} catch (e: Exception) {
    throw CryptoError.wrap(e)
}