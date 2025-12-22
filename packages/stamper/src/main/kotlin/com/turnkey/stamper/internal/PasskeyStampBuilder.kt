package com.turnkey.stamper.internal

import com.turnkey.encoding.toBase64Url
import com.turnkey.passkey.PasskeyStamper
import com.turnkey.stamper.utils.TurnkeyStamperError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class PasskeyStampJson(
    val authenticatorData: String,
    val clientDataJson: String,
    val credentialId: String,
    val signature: String
)

internal object PasskeyStampBuilder {
    /**
     * Core builder: stamp WebAuthn using a generic assertion client (test-friendly).
     *
     * @param payloadSha256 32-byte SHA-256 digest
     * @param passkeyClient something that can assert(challengeBytes)
     * @return JSON string with base64url fields
     */
    @Throws(TurnkeyStamperError::class)
    suspend fun stamp(
        payloadSha256: ByteArray,
        passkeyClient: PasskeyStamper
    ): String {
        try {
            if (payloadSha256.size != 32) throw TurnkeyStamperError.InvalidChallenge(
                payloadSha256.size,
                32,
                null
            )

            val challengeHexAscii =
                payloadSha256.joinToString("") { "%02x".format(it) }.encodeToByteArray()

            val assertion = try {
                passkeyClient.assert(challengeHexAscii)
            } catch (e: Throwable) {
                throw TurnkeyStamperError.AssertionFailed(e)
            }

            val jsonObj = PasskeyStampJson(
                authenticatorData = assertion.authenticatorData.toBase64Url(),
                clientDataJson = assertion.clientDataBytes.toBase64Url(),
                credentialId = assertion.credentialId.toBase64Url(),
                signature = assertion.signature.toBase64Url()
            )

            return try {
                Json.encodeToString(jsonObj)
            } catch (e: Throwable) {
                throw TurnkeyStamperError.FailedToEncodeStamp(e)
            }
        } catch (t: Throwable) {
            throw TurnkeyStamperError.wrap(t)
        }
    }
}


