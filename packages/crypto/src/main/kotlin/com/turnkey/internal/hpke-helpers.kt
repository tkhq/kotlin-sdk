package com.turnkey.internal

import com.turnkey.utils.TurnkeyConstants.hpkeInfo
import com.turnkey.encoding.decodeHex
import com.turnkey.encoding.toHexString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bitcoinj.core.Base58
import org.bouncycastle.asn1.nist.NISTNamedCurves
import java.security.interfaces.ECPrivateKey
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.hpke.HPKE
import org.bouncycastle.crypto.hpke.HPKEContextWithEncapsulation
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import com.turnkey.crypto.P256KeyPair
import java.math.BigInteger
import java.security.MessageDigest
import com.turnkey.utils.CryptoError
import com.turnkey.utils.KeyFormat
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.nio.ByteBuffer
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec

@Serializable
data class BundleOuter(
    val enclaveQuorumPublic: String,
    val dataSignature: String,
    val data: String
)

@Serializable
data class SignedInner(
    val organizationId: String? = null,
    val userId: String? = null,
    val encappedPublic: String? = null,  // hex (uncompressed X9.62)
    val targetPublic: String? = null,    // hex (uncompressed X9.62)
    val ciphertext: String? = null       // hex
)

fun decodeBundleOuter(jsonStr: String): BundleOuter =
    try { json.decodeFromString<BundleOuter>(jsonStr) }
    catch (e: Throwable) { throw CryptoError.DecodingFailed(e) }

fun decodeSignedInner(hexStr: String): SignedInner {
    val bytes = try { decodeHex(hexStr) } catch (_: Exception) { throw CryptoError.InvalidHexString(hexStr) }
    return try { json.decodeFromString<SignedInner>(bytes.decodeToString()) }
    catch (e: Throwable) { throw CryptoError.DecodingFailed(e) }
}

object P256 {
    // NIST P-256 domain
    private val x9 = NISTNamedCurves.getByName("P-256")
    val domain = ECDomainParameters(x9.curve, x9.g, x9.n, x9.h)

    /** Compute Q = d*G and return ECPublicKeyParameters. */
    fun publicFromScalar(d: ByteArray): ECPublicKeyParameters {
        val q = domain.g.multiply(BigInteger(1, d)).normalize()
        return ECPublicKeyParameters(q, domain)
    }

    /** Decompress 33-byte compressed point → 65-byte uncompressed (X9.62). */
    fun decompress(compressed: ByteArray): ByteArray =
        domain.curve.decodePoint(compressed).getEncoded(false)

    /** Parse 65-byte uncompressed (0x04 || X || Y) into ECPublicKeyParameters. */
    fun publicFromUncompressed(uncompressed: ByteArray): ECPublicKeyParameters =
        ECPublicKeyParameters(domain.curve.decodePoint(uncompressed), domain)

    /** Make AsymmetricCipherKeyPair from private scalar. */
    fun keyPairFromScalar(d: ByteArray): AsymmetricCipherKeyPair {
        val priv = ECPrivateKeyParameters(BigInteger(1, d), domain)
        val pub = publicFromScalar(d)
        return AsymmetricCipherKeyPair(pub, priv)
    }

    /** Compress/uncompress toggles. */
    fun compress(uncompressed: ByteArray): ByteArray =
        domain.curve.decodePoint(uncompressed).getEncoded(true)
}

private fun sha256(b: ByteArray) = MessageDigest.getInstance("SHA-256").digest(b)

/** Decode Base58Check -> payload (checksum verified, checksum removed). */
fun base58CheckDecode(s: String): ByteArray = Base58.decodeChecked(s) // checksum removed

/** Encode payload -> Base58Check (adds 4-byte double-SHA256 checksum). */
fun base58CheckEncode(payload: ByteArray): String {
    val chk = sha256(sha256(payload)).copyOfRange(0, 4)
    return Base58.encode(payload + chk)
}

private fun hpkeSuite(): HPKE = HPKE(
    HPKE.mode_base,
    HPKE.kem_P256_SHA256,
    HPKE.kdf_HKDF_SHA256,
    HPKE.aead_AES_GCM256
)

/**
 * HPKE encrypt using recipient public key (hex X9.62 uncompressed).
 * Returns: concatenated [compressedEncapped(33) || ciphertext].
 */
@Throws(Exception::class)
fun hpkeEncrypt(
    plaintext: ByteArray,
    recipientPubKeyHex: String,
    info: ByteArray = hpkeInfo
): ByteArray {
    val recipientUncompressed = try { decodeHex(recipientPubKeyHex) }
    catch (_: IllegalArgumentException) { throw CryptoError.InvalidHexString(recipientPubKeyHex) }

    val pkR = P256.publicFromUncompressed(recipientUncompressed)
    val hpke = hpkeSuite()

    // setup sender → context + encapsulated key (uncompressed, 65 bytes)
    val ctxWithEnc: HPKEContextWithEncapsulation = hpke.setupBaseS(pkR, info)
    val encUncompressed: ByteArray = ctxWithEnc.encapsulation  // aka getEncapsulation() in some docs

    // AAD = enc || recipientPub
    val aad = encUncompressed + recipientUncompressed

    // seal once with that AAD
    val ct = ctxWithEnc.seal(aad, plaintext)

    // store compressed(enc) + ct
    val compressed = P256.compress(encUncompressed) // 33 bytes
    require(compressed.size == 33)
    return compressed + ct
}

/**
 * HPKE decrypt.
 * @param ciphertext  raw HPKE ciphertext
 * @param encappedUncompressed  65-byte X9.62 uncompressed ephemeral public key
 * @param receiverPrivScalar    32-byte P-256 private scalar
 * @param info                  suite info/context
 */
@Throws(Exception::class)
fun hpkeDecrypt(
    ciphertext: ByteArray,
    encappedUncompressed: ByteArray,
    receiverPrivScalar: ByteArray,
    info: ByteArray = hpkeInfo
): ByteArray {
    // Receiver keypair from scalar
    val skR = P256.keyPairFromScalar(receiverPrivScalar)
    val hpke = hpkeSuite()

    // AAD = enc || receiverPublic (uncompressed)
    val receiverPubUncompressed = (skR.public as ECPublicKeyParameters).q.getEncoded(false)
    val aad = encappedUncompressed + receiverPubUncompressed

    return hpke.open(
        /*enc=*/ encappedUncompressed,
        /*skR=*/ skR,
        /*info=*/ info,
        /*aad=*/ aad,
        /*ct=*/ ciphertext,
        /*psk=*/ null,
        /*pskId=*/ null,
        /*pkS=*/ null
    )
}

@Throws(CryptoError::class)
fun decryptCredentialBundle(
    encryptedBundle: String,
    ephemeralPrivateKey: ECPrivateKey
): P256KeyPair {
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

    return P256KeyPair(publicKey = pubKey, privateKey = privKey)
}

@Throws(CryptoError::class)
fun decryptExportBundle(
    exportBundle: String,
    organizationId: String,
    embeddedPrivateKey: ECPrivateKey,
    dangerouslyOverrideSignerPublicKey: String? = null,
    keyFormat: KeyFormat? = null,
    returnMnemonic: Boolean
): String {
    // Parse outer JSON
    val outer = try {
        decodeBundleOuter(exportBundle)
    } catch (e: CryptoError) {
        throw CryptoError.DecodingFailed(e)
    }

    val ok = verifyEnclaveSignature(
        outer.enclaveQuorumPublic,
        outer.dataSignature,
        outer.data,
        dangerouslyOverrideSignerPublicKey
    )
    if (!ok) throw CryptoError.SignatureVerificationFailed

    val inner = try {
        decodeSignedInner(outer.data)
    } catch (e: Throwable) {
        throw CryptoError.DecodingFailed(e)
    }

    if (inner.organizationId != organizationId) {
        throw CryptoError.OrgIdMismatch(organizationId, inner.organizationId)
    }

    val encappedHex = inner.encappedPublic ?: throw CryptoError.MissingEncappedPublic
    val ciphertextHex = inner.ciphertext ?: throw CryptoError.MissingCiphertext

    val ct = try { decodeHex(ciphertextHex) }
    catch (_: Throwable) { throw CryptoError.InvalidHexString(ciphertextHex) }
    val ek = try { decodeHex(encappedHex) }
    catch (_: Throwable) { throw CryptoError.InvalidHexString(encappedHex) }

    // extract scalar value (s) and left-pad to 32 bytes
    val receiverPrivScalar = bigIntToFixed(embeddedPrivateKey.s, 32)
    val plaintext = hpkeDecrypt(ct, ek, receiverPrivScalar)

    return when {
        keyFormat == KeyFormat.solana && !returnMnemonic -> {
            if (plaintext.size != 32) throw CryptoError.InvalidPrivateLength(32, plaintext.size)

            val pubKey = deriveEd25519PublicKey(plaintext)
            if (pubKey.size != 32) throw CryptoError.InvalidPublicLength(32, pubKey.size)

            base58CheckEncode(plaintext + pubKey)
        }
        returnMnemonic -> {
            val mnemonic = utf8Strict(plaintext)
            mnemonic
        }
        else -> {
            plaintext.toHexString()
        }
    }
}

@Throws(CryptoError::class)
fun encryptWalletToBundle(
    mnemonic: String,
    importBundle: String,
    userId: String,
    organizationId: String,
    dangerouslyOverrideSignerPublicKey: String?
): String {
    val outer = try {
        decodeBundleOuter(importBundle)
    } catch (e: CryptoError) {
        throw CryptoError.DecodingFailed(e)
    }

    val ok = verifyEnclaveSignature(
        outer.enclaveQuorumPublic,
        outer.dataSignature,
        outer.data,
        dangerouslyOverrideSignerPublicKey
    )
    if (!ok) throw CryptoError.SignatureVerificationFailed

    val inner = try {
        decodeSignedInner(outer.data)
    } catch (e: Throwable) {
        throw CryptoError.DecodingFailed(e)
    }

    if (inner.organizationId != organizationId) {
        throw CryptoError.OrgIdMismatch(organizationId, inner.organizationId)
    }
    if (inner.userId != userId) {
        throw CryptoError.UserIdMismatch(userId, inner.userId)
    }

    val targetHex = inner.targetPublic ?: throw CryptoError.MissingEncappedPublic

    val plaintext = mnemonic.toByteArray(StandardCharsets.UTF_8)

    val bundleBytes = hpkeEncrypt(plaintext, targetHex)

    if (bundleBytes.size <= 33) throw CryptoError.InvalidCompressedKeyLength(plaintext.size)

    val compressed = bundleBytes.copyOfRange(0, 33)
    val cipher = bundleBytes.copyOfRange(33, bundleBytes.size)

    val uncompressedPub = ecPointCompressedToUncompressed(compressed)
    val json = mapOf("encappedPublic" to uncompressedPub.toHexString(), "ciphertext" to cipher.toHexString())
    return Json.encodeToString(json)
}

@Throws(CryptoError::class)
private fun utf8Strict(bytes: ByteArray): String {
    val dec = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
    return try {
        dec.decode(ByteBuffer.wrap(bytes)).toString()
    } catch (e: CharacterCodingException) {
        throw CryptoError.InvalidUTF8
    }
}

@Throws(CryptoError::class)
fun deriveEd25519PublicKey(secretScalar: ByteArray): ByteArray {
    if (secretScalar.size != 32) throw CryptoError.InvalidPrivateLength(32, secretScalar.size)
    val priv = Ed25519PrivateKeyParameters(secretScalar, 0)
    return priv.generatePublicKey().encoded // 32 bytes
}

private val json = Json { ignoreUnknownKeys = true }

fun ecPointUncompressedToCompressed(uncompressed: ByteArray): ByteArray = P256.compress(uncompressed)
fun ecPointCompressedToUncompressed(compressed: ByteArray): ByteArray = P256.decompress(compressed)

fun bigIntToFixed(v: BigInteger, size: Int): ByteArray {
    val full = v.toByteArray() // may include a leading 0x00 sign byte
    return when {
        full.size == size -> full
        full.size == size + 1 && full[0] == 0.toByte() -> full.copyOfRange(1, full.size)
        full.size < size -> ByteArray(size - full.size) + full
        else -> throw IllegalStateException("Value does not fit in $size bytes")
    }
}