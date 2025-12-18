package com.turnkey.crypto

import com.turnkey.encoding.decodeHex
import kotlin.test.*
import org.bouncycastle.asn1.nist.NISTNamedCurves
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec

class GenerateP256KeyPairTest {

    @Test
    fun generates_valid_lengths_and_prefixes() {
        val kp = generateP256KeyPair()

        val d = decodeHex(kp.privateKey)
        val pubUn = decodeHex(kp.publicKeyUncompressed)
        val pubC  = decodeHex(kp.publicKeyCompressed)

        // lengths
        assertEquals(32, d.size, "private key must be 32 bytes")
        assertEquals(65, pubUn.size, "uncompressed pubkey must be 65 bytes")
        assertEquals(33, pubC.size, "compressed pubkey must be 33 bytes")

        // prefixes
        assertEquals(0x04.toByte(), pubUn[0], "uncompressed must start with 0x04")
        assertTrue(pubC[0] == 0x02.toByte() || pubC[0] == 0x03.toByte(), "compressed must start with 0x02 or 0x03")
    }

    @Test
    fun compressed_matches_uncompressed() {
        val kp = generateP256KeyPair()
        val pubUn = decodeHex(kp.publicKeyUncompressed)
        val pubC  = decodeHex(kp.publicKeyCompressed)

        val x9 = NISTNamedCurves.getByName("P-256")
        val curve = x9.curve

        val decompressed = curve.decodePoint(pubC).getEncoded(false)
        assertContentEquals(pubUn, decompressed, "decompressed(compressed) should equal uncompressed")
    }

    @Test
    fun generates_valid_hex_and_ec_keys_and_verifies_signature() {
        val raw = generateP256KeyPair()

        val d = decodeHex(raw.privateKey)
        val pubUn = decodeHex(raw.publicKeyUncompressed)
        val pubC  = decodeHex(raw.publicKeyCompressed)

        // Sizes & prefixes
        assertEquals(32, d.size)
        assertEquals(65, pubUn.size)
        assertEquals(33, pubC.size)
        assertEquals(0x04.toByte(), pubUn[0])
        assertTrue(pubC[0] == 0x02.toByte() || pubC[0] == 0x03.toByte())

        // Rebuild JCA EC keys from hex and sign/verify
        val ecSpec: ECParameterSpec = AlgorithmParameters.getInstance("EC")
            .apply { init(ECGenParameterSpec("secp256r1")) }
            .getParameterSpec(ECParameterSpec::class.java)

        // Private key
        val kf = KeyFactory.getInstance("EC")
        val priv = kf.generatePrivate(ECPrivateKeySpec(BigInteger(1, d), ecSpec))

        // Public key (uncompressed 0x04 || X || Y)
        val x = pubUn.copyOfRange(1, 33)
        val y = pubUn.copyOfRange(33, 65)
        val pubSpec = ECPublicKeySpec(
            ECPoint(
                BigInteger(1, x),
                BigInteger(1, y)
            ),
            ecSpec
        )
        val pub = kf.generatePublic(pubSpec) as ECPublicKey

        // Sign / verify
        val msg = "turnkey".toByteArray()
        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initSign(priv)
        sig.update(msg)
        val sigBytes = sig.sign()

        sig.initVerify(pub)
        sig.update(msg)
        assertTrue(sig.verify(sigBytes), "signature must verify with generated public key")
    }
}