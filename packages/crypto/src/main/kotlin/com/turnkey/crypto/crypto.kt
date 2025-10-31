package com.turnkey.crypto

import com.turnkey.encoding.decodeHex
import com.turnkey.encoding.toHexString
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPrivateKeySpec
import com.turnkey.internal.bigIntToFixed
import com.turnkey.internal.decryptCredentialBundle
import com.turnkey.internal.decryptExportBundle
import com.turnkey.internal.encryptWalletToBundle
import com.turnkey.utils.KeyFormat
import kotlin.jvm.Throws
import com.turnkey.utils.CryptoError
import java.security.KeyFactory


data class RawP256KeyPair(
    val publicKeyUncompressed: String, // 0x04 || X(32) || Y(32) -> 65 bytes hex
    val publicKeyCompressed: String,   // 0x02/0x03 || X(32)     -> 33 bytes hex
    val privateKey: String             // raw scalar d (32 bytes hex)
)

data class P256KeyPair(
    val publicKey: ECPublicKey,
    val privateKey: ECPrivateKey,
)

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

@Throws(IllegalArgumentException::class)
fun decryptCredentialBundle(encryptedBundle: String, ephemeralPrivateKey: ECPrivateKey): P256KeyPair {
    return decryptCredentialBundle(encryptedBundle, ephemeralPrivateKey)
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
): String {
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

    return decryptExportBundle(
        exportBundle = exportBundle,
        organizationId = organizationId,
        embeddedPrivateKey = ecPrivate, // raw scalar d (big-endian)
        dangerouslyOverrideSignerPublicKey = dangerouslyOverrideSignerPublicKey,
        keyFormat = keyFormat,
        returnMnemonic = returnMnemonic,
    )
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
    dangerouslyOverrideSignerPublicKey: String? = null
): String {
    return encryptWalletToBundle(mnemonic, importBundle, userId, organizationId, dangerouslyOverrideSignerPublicKey)
}