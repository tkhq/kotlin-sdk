package com.turnkey.stamper

import com.turnkey.crypto.P256
import com.turnkey.encoding.decodeBase64Url
import com.turnkey.encoding.decodeHex
import com.turnkey.encoding.toHexString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RequestStamperTest {
    private fun compressedHex(pub: ECPublicKey): String {
        val x = pub.w.affineX.toByteArray().let { if (it.size > 32) it.copyOfRange(it.size - 32, it.size) else ByteArray(32 - it.size) + it }
        val prefix: Byte = if (pub.w.affineY.testBit(0)) 0x03 else 0x02
        return (byteArrayOf(prefix) + x).toHexString()
    }

    @Test
    fun external_signer_produces_a_verifiable_api_key_stamp() =
        runTest {
            // Stands in for a key the SDK cannot read, such as an Android Keystore key.
            val keyPair =
                KeyPairGenerator.getInstance("EC").run {
                    initialize(ECGenParameterSpec("secp256r1"))
                    generateKeyPair()
                }
            val publicKeyHex = compressedHex(keyPair.public as ECPublicKey)
            val stamper: RequestStamper =
                RequestStamper { payload ->
                    val signature =
                        Signature.getInstance("SHA256withECDSA").run {
                            initSign(keyPair.private)
                            update(payload.toByteArray())
                            sign()
                        }
                    ApiKeyStamp.header(publicKeyHex, signature)
                }

            val payload = """{"organizationId":"org","type":"ACTIVITY_TYPE_APPROVE_ACTIVITY"}"""
            val (name, value) = stamper.stamp(payload)

            assertEquals("X-Stamp", name)
            val stamp = Json.parseToJsonElement(String(decodeBase64Url(value))).jsonObject
            assertEquals(publicKeyHex, stamp.getValue("publicKey").jsonPrimitive.content)
            assertEquals("SIGNATURE_SCHEME_TK_API_P256", stamp.getValue("scheme").jsonPrimitive.content)
            val verified =
                Signature.getInstance("SHA256withECDSA").run {
                    initVerify(keyPair.public)
                    update(payload.toByteArray())
                    verify(decodeHex(stamp.getValue("signature").jsonPrimitive.content))
                }
            assertTrue(verified)
        }

    @Test
    fun value_matches_the_built_in_api_key_stamper() =
        runTest {
            // The built-in stamper signs deterministically (RFC 6979), so the
            // signature from its stamp can be re-encoded and compared.
            val privateKeyHex = "c9afa9d845ba75166b5c215767b1d6934e50c3db36e89b127b8a622b120f6721"
            val publicKeyHex =
                P256.domain.g
                    .multiply(BigInteger(privateKeyHex, 16))
                    .normalize()
                    .getEncoded(true)
                    .toHexString()
            val (name, value) = Stamper(publicKeyHex, privateKeyHex).stamp("""{"ping":"pong"}""")
            val signature =
                Json.parseToJsonElement(String(decodeBase64Url(value))).jsonObject.getValue("signature").jsonPrimitive.content

            assertEquals(ApiKeyStamp.HEADER_NAME, name)
            assertEquals(ApiKeyStamp.value(publicKeyHex, signature), value)
        }

    @Test
    fun value_rejects_non_hex_input() {
        assertFailsWith<IllegalArgumentException> { ApiKeyStamp.value("not hex", "3045") }
        assertFailsWith<IllegalArgumentException> { ApiKeyStamp.value("02ab", "") }
    }
}
