package com.turnkey.crypto.internal

import com.turnkey.crypto.utils.CryptoError
import com.turnkey.crypto.utils.TurnkeyConstants.hpkeInfo
import com.turnkey.encoding.decodeHex
import com.turnkey.encoding.toHexString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bouncycastle.asn1.nist.NISTNamedCurves
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.hpke.HPKE
import org.bouncycastle.crypto.hpke.HPKEContextWithEncapsulation
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import java.math.BigInteger

@Serializable
internal data class BundleOuter(
    val enclaveQuorumPublic: String,
    val dataSignature: String,
    val data: String
)

@Serializable
internal data class SignedInner(
    val organizationId: String? = null,
    val userId: String? = null,
    val encappedPublic: String? = null,  // hex (uncompressed X9.62)
    val targetPublic: String? = null,    // hex (uncompressed X9.62)
    val ciphertext: String? = null       // hex
)

private val json = Json { ignoreUnknownKeys = true }

/** Decodes outer bundle from JSON string. */
internal fun decodeBundleOuter(jsonStr: String): BundleOuter =
    json.decodeFromString<BundleOuter>(jsonStr)

/** Decodes inner signed payload from hex-encoded JSON. */
internal fun decodeSignedInner(hexStr: String): SignedInner {
    val bytes = decodeHex(hexStr)
    return json.decodeFromString<SignedInner>(bytes.decodeToString())
}

internal object P256 {
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

    /** Compress 65-byte uncompressed point → 33-byte compressed (X9.62). */
    fun compress(uncompressed: ByteArray): ByteArray =
        domain.curve.decodePoint(uncompressed).getEncoded(true)

    /** Parse 65-byte uncompressed (0x04 || X || Y) into ECPublicKeyParameters. */
    fun publicFromUncompressed(uncompressed: ByteArray): ECPublicKeyParameters =
        ECPublicKeyParameters(domain.curve.decodePoint(uncompressed), domain)

    /** Make AsymmetricCipherKeyPair from private scalar. */
    fun keyPairFromScalar(d: ByteArray): AsymmetricCipherKeyPair {
        val priv = ECPrivateKeyParameters(BigInteger(1, d), domain)
        val pub = publicFromScalar(d)
        return AsymmetricCipherKeyPair(pub, priv)
    }
}

/** Converts 65-byte uncompressed point to 33-byte compressed format. */
internal fun ecPointUncompressedToCompressed(uncompressed: ByteArray): ByteArray = 
    P256.compress(uncompressed)

/** Converts 33-byte compressed point to 65-byte uncompressed format. */
internal fun ecPointCompressedToUncompressed(compressed: ByteArray): ByteArray = 
    P256.decompress(compressed)

/** Creates HPKE suite with P-256, HKDF-SHA256, and AES-GCM-256. */
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
internal fun hpkeEncrypt(
    plaintext: ByteArray,
    recipientPubKeyHex: String,
    info: ByteArray = hpkeInfo
): ByteArray {
    val recipientUncompressed = decodeHex(recipientPubKeyHex)
    val pkR = P256.publicFromUncompressed(recipientUncompressed)
    val hpke = hpkeSuite()

    // Setup sender → context + encapsulated key (uncompressed, 65 bytes)
    val ctxWithEnc: HPKEContextWithEncapsulation = hpke.setupBaseS(pkR, info)
    val encUncompressed: ByteArray = ctxWithEnc.encapsulation

    // AAD = enc || recipientPub
    val aad = encUncompressed + recipientUncompressed

    // Seal with AAD
    val ct = ctxWithEnc.seal(aad, plaintext)

    // Return compressed(enc) + ciphertext
    val compressed = P256.compress(encUncompressed)
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
internal fun hpkeDecrypt(
    ciphertext: ByteArray,
    encappedUncompressed: ByteArray,
    receiverPrivScalar: ByteArray,
    info: ByteArray = hpkeInfo
): ByteArray {
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

/**
 * Derives Ed25519 public key from a 32-byte secret scalar.
 *
 * @param secretScalar 32-byte secret scalar for Ed25519 key
 * @return 32-byte Ed25519 public key
 * @throws CryptoError.InvalidPrivateLength if scalar is not 32 bytes
 */
@Throws(CryptoError::class)
internal fun deriveEd25519PublicKey(secretScalar: ByteArray): ByteArray {
    if (secretScalar.size != 32) {
        throw CryptoError.InvalidPrivateLength(32, secretScalar.size)
    }
    val privateKey = Ed25519PrivateKeyParameters(secretScalar, 0)
    return privateKey.generatePublicKey().encoded
}

/**
 * Converts BigInteger to fixed-size byte array, handling sign byte and padding.
 *
 * @param value BigInteger to convert
 * @param size Target byte array size
 * @return Fixed-size byte array representation
 * @throws IllegalStateException if value doesn't fit in specified size
 */
internal fun bigIntToFixed(value: BigInteger, size: Int): ByteArray {
    val full = value.toByteArray() // May include a leading 0x00 sign byte
    return when {
        full.size == size -> full
        full.size == size + 1 && full[0] == 0.toByte() -> full.copyOfRange(1, full.size)
        full.size < size -> ByteArray(size - full.size) + full
        else -> throw IllegalStateException("Value does not fit in $size bytes")
    }
}
