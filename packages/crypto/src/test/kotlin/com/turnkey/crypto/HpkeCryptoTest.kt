package com.turnkey.crypto

import com.turnkey.crypto.internal.bigIntToFixed
import com.turnkey.crypto.internal.ecPointCompressedToUncompressed
import com.turnkey.crypto.internal.hpkeDecrypt
import com.turnkey.crypto.internal.hpkeEncrypt
import com.turnkey.encoding.base58CheckEncode
import kotlin.test.*
import java.math.BigInteger
import java.security.*
import java.security.interfaces.*
import java.security.spec.*

class HpkeCryptoTest {

    // Build uncompressed (0x04 || X || Y) from a JCA ECPublicKey
    private fun uncompressedFrom(pub: ECPublicKey): ByteArray {
        val coordLen = (pub.params.curve.field.fieldSize + 7) / 8
        fun fixed(bi: BigInteger): ByteArray {
            val full = bi.toByteArray()
            return when {
                full.size == coordLen -> full
                full.size == coordLen + 1 && full[0] == 0.toByte() -> full.copyOfRange(1, full.size)
                full.size < coordLen -> ByteArray(coordLen - full.size) + full
                else -> error("value does not fit")
            }
        }
        val x = fixed(pub.w.affineX)
        val y = fixed(pub.w.affineY)
        return ByteArray(1 + 2 * coordLen).apply {
            this[0] = 0x04
            System.arraycopy(x, 0, this, 1, coordLen)
            System.arraycopy(y, 0, this, 1 + coordLen, coordLen)
        }
    }

    @Test
    fun hpke_encrypt_decrypt_round_trip() {
        // Recipient keypair (secp256r1)
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()
        val priv = kp.private as ECPrivateKey
        val pub = kp.public as ECPublicKey

        val recipientUncompressedHex = uncompressedFrom(pub).toHexString()

        val plaintext = "hello hpke".encodeToByteArray()

        // Encrypt → [33B compressed enc || ciphertext]
        val bundleBytes = hpkeEncrypt(
            plaintext = plaintext,
            recipientPubKeyHex = recipientUncompressedHex
        )

        assertTrue(bundleBytes.size > 33, "bundle must contain encapped(33) + ciphertext(>0)")

        val compressed = bundleBytes.copyOfRange(0, 33)
        val ciphertext = bundleBytes.copyOfRange(33, bundleBytes.size)

        // Decompress encapped for HPKE open
        val encappedUncompressed = ecPointCompressedToUncompressed(compressed)

        // extract scalar value (s) and left-pad to 32 bytes
        val receiverPrivScalar = bigIntToFixed(priv.s, 32)
        val decrypted = hpkeDecrypt(
            ciphertext = ciphertext,
            encappedUncompressed = encappedUncompressed,
            receiverPrivScalar = receiverPrivScalar,
        )

        assertContentEquals(plaintext, decrypted)
    }

    @Test
    fun decryptCredentialBundle_builds_valid_signing_keys() {
        // Receiver keypair (ephemeral)
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()
        val priv = kp.private as ECPrivateKey
        val pub = kp.public as ECPublicKey

        // Choose plaintext = 32-byte scalar (signing private key)
        val plaintext = SecureRandom().generateSeed(32)

        // Encrypt to receiver’s public key
        val recipientUncompressedHex = uncompressedFrom(pub).toHexString()
        val bundleBytes = hpkeEncrypt(
            plaintext = plaintext,
            recipientPubKeyHex = recipientUncompressedHex
        )
        val compressed = bundleBytes.copyOfRange(0, 33)
        val ciphertext = bundleBytes.copyOfRange(33, bundleBytes.size)

        // Build Base58Check payload: [33B compressed enc || ciphertext]
        val b58 = base58CheckEncode(compressed + ciphertext)

        // Call the API under test
        val result = decryptCredentialBundle(
            encryptedBundle = b58,
            ephemeralPrivateKey = priv
        )

        // Basic shape checks
        val ecPriv = result.privateKey
        val ecPub = result.publicKey

        // Recompute expected public from the decrypted scalar and compare XY
        val d = BigInteger(1, plaintext)
        val ecSpec: ECParameterSpec = AlgorithmParameters.getInstance("EC")
            .apply { init(ECGenParameterSpec("secp256r1")) }
            .getParameterSpec(ECParameterSpec::class.java)

        val kf = KeyFactory.getInstance("EC")
        val expectPriv = kf.generatePrivate(ECPrivateKeySpec(d, ecSpec)) as ECPrivateKey

        assertEquals(expectPriv.s, ecPriv.s, "private scalar must match decrypted plaintext")

        // Derive public from decrypted scalar using your P256 helper, compare with ecPub
        val q = P256.publicFromScalar(plaintext).q.normalize()
        val xExpected = q.affineXCoord.toBigInteger()
        val yExpected = q.affineYCoord.toBigInteger()

        assertEquals(xExpected, ecPub.w.affineX)
        assertEquals(yExpected, ecPub.w.affineY)
    }
}
