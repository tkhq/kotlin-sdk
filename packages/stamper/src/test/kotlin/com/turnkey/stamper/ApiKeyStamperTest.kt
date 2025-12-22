package com.turnkey.stamper

import com.turnkey.encoding.decodeBase64Url
import com.turnkey.encoding.toHexString
import com.turnkey.stamper.internal.ApiKeyStamper
import com.turnkey.stamper.utils.errors.TurnkeyStamperError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.test.runTest
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.nist.NISTNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.test.*

private fun bigIntToFixed(bi: BigInteger, len: Int): ByteArray {
    val full = bi.toByteArray()
    return when {
        full.size == len -> full
        full.size == len + 1 && full[0] == 0.toByte() -> full.copyOfRange(1, full.size)
        full.size < len -> ByteArray(len - full.size) + full
        else -> error("integer too large")
    }
}

private fun compressedHex(pub: ECPublicKey): String {
    val coordLen = (pub.params.curve.field.fieldSize + 7) / 8
    val x = bigIntToFixed(pub.w.affineX, coordLen)
    val prefix: Byte = if (pub.w.affineY.testBit(0)) 0x03 else 0x02
    return (byteArrayOf(prefix) + x).toHexString()
}

class ApiKeyStamperTest {
    @Test
    fun apiKey_stamp_and_verify_signature() = runTest {
        // Generate a P-256 keypair
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()
        val priv = kp.private as ECPrivateKey
        val pub = kp.public as ECPublicKey

        val coordLen = (pub.params.curve.field.fieldSize + 7) / 8
        val dHex = bigIntToFixed(priv.s, 32).toHexString()
        val pubCompressedHex = compressedHex(pub).lowercase()

        // Payload & digest
        val payload = """{"ping":"pong"}"""
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray())

        // Build stamp (base64url-encoded JSON)
        val stampB64 = ApiKeyStamper.stamp(digest, pubCompressedHex, dHex)

        // Decode JSON
        val jsonStr = String(decodeBase64Url(stampB64))
        val root = Json.parseToJsonElement(jsonStr).jsonObject

        assertEquals(pubCompressedHex, root["publicKey"]!!.jsonPrimitive.content)
        assertEquals("SIGNATURE_SCHEME_TK_API_P256", root["scheme"]!!.jsonPrimitive.content)

        val sigHex = root["signature"]!!.jsonPrimitive.content
        val sigBytes = sigHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

        // Verify ECDSA (DER r,s) on the raw digest
        val x9 = NISTNamedCurves.getByName("P-256")
        val domain = ECDomainParameters(x9.curve, x9.g, x9.n, x9.h)
        val q = x9.curve.createPoint(pub.w.affineX, pub.w.affineY)
        val pubParams = ECPublicKeyParameters(q, domain)

        val seq = ASN1Sequence.getInstance(sigBytes)
        val r = (seq.getObjectAt(0) as ASN1Integer).value
        val s = (seq.getObjectAt(1) as ASN1Integer).value

        val verifier = ECDSASigner()
        verifier.init(false, pubParams)
        assertTrue(verifier.verifySignature(digest, r, s), "signature must verify")
    }

    @Test
    fun apiKey_mismatched_public_key_fails() = runTest {
        // real key
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()
        val priv = kp.private as ECPrivateKey
        val dHex = bigIntToFixed(priv.s, 32).toHexString()

        // intentionally wrong pubkey
        val wrongPubHex = "02" + "00".repeat(32)

        val digest = MessageDigest.getInstance("SHA-256").digest("x".encodeToByteArray())

        val ex = assertFails { ApiKeyStamper.stamp(digest, wrongPubHex, dHex) }
        assertTrue(ex is TurnkeyStamperError.FailedToStamp)
    }

    @Test
    fun apiKey_bad_private_hex_fails() = runTest {
        val digest = ByteArray(32) { 7 } // any digest
        val pubHex = "02" + "11".repeat(32)
        val badPriv = "abcd" // wrong length

        val ex = assertFails { ApiKeyStamper.stamp(digest, pubHex, badPriv) }
        print(ex)
        assertTrue(ex is TurnkeyStamperError.FailedToStamp)
    }

    @Test
    fun stamper_api_mode_returns_header_and_value() = runTest {
        // Build a valid API-key stamper
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(ECGenParameterSpec("secp256r1"))
        val kp = kpg.generateKeyPair()
        val priv = kp.private as ECPrivateKey
        val pub = kp.public as ECPublicKey
        val dHex = bigIntToFixed(priv.s, 32).toHexString()
        val pubHex = compressedHex(pub)

        val stamper = Stamper(apiPublicKey = pubHex, apiPrivateKey = dHex)
        val (header, value) = stamper.stamp("""{"hello":"world"}""")
        assertEquals("X-Stamp", header)
        // quick structural check: base64url decodes to JSON with required fields
        val root = Json.parseToJsonElement(String(decodeBase64Url(value))).jsonObject
        assertNotNull(root["publicKey"])
        assertNotNull(root["signature"])
    }

    @Test
    fun stamper_unknown_mode_throws() = runTest {
        val s = Stamper()
        val ex = assertFails { s.stamp("anything") }
        print(ex)
        assertTrue(ex is TurnkeyStamperError.OperationFailed)
    }
}
