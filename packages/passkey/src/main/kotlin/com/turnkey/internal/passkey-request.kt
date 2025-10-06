package com.turnkey.internal

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import com.turnkey.encoding.decodeBase64Url
import com.turnkey.passkey.PasskeyRegistrationResult
import com.turnkey.passkey.AssertionResult
import com.turnkey.passkey.Attestation
import com.turnkey.utils.PasskeyError
import com.turnkey.passkey.Transport
import com.turnkey.utils.buildCreatePublicKeyOptionsJson
import com.turnkey.utils.buildGetPublicKeyOptionsJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** WebAuthn clientData (subset) */
@Serializable
private data class ClientData (
    val challenge: String,
    val origin: String,
    val type: String,
)

/** Registration response from CM (subset of WebAuthn). */
@Serializable
private data class PublicKeyCredentialCreateJson(
    val id: String? = null,
    val type: String? = null,
    val rawId: String? = null,
    val response: Response? = null
) {
    @Serializable
    data class Response(
        @SerialName("clientDataJSON") val clientDataJSON_b64url: String,
        @SerialName("attestationObject") val attestationObject_b64url: String? = null,
        val transports: List<String>? = null
    )
}


/** Assertion (authentication) response shape (subset) */
@Serializable
private data class PublicKeyCredentialAuthJson(
    val id: String? = null,
    val type: String? = null,
    val rawId: String? = null,
    val response: Response? = null
) {
    @Serializable
    data class Response(
        @SerialName("clientDataJSON") val clientDataJSON_b64url: String,
        @SerialName("authenticatorData") val authenticatorData_b64url: String,
        val signature_b64url: String,
        val userHandle_b64url: String? = null
    )
}

/**
 * - rpId: relying party identifier (domain)
 * - activity: used by Credential Manager UI
 */
class PasskeyRequestBuilder(
    private val rpId: String,
    private val activity: Activity,
    private val credentialManager: CredentialManager = CredentialManager.create(activity),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {

    /**
     * Build a WebAuthn **registration** (create) request for Credential Manager.
     *
     * - userId: raw bytes
     * - userName: human-readable
     * - challenge: 32-byte server challenge
     * - excludeCredentials: credential IDs to exclude (raw bytes)
     */
    fun makeRegistrationRequest(
        userId: ByteArray,
        userName: String,
        challenge: ByteArray,
        excludeCredentials: List<ByteArray>,
    ): CreatePublicKeyCredentialRequest {
        val publicKeyJson = buildCreatePublicKeyOptionsJson(
            rpId = rpId,
            userId = userId,
            userName = userName,
            challenge = challenge,
            excludeCredentialIds = excludeCredentials
        )
        return CreatePublicKeyCredentialRequest(
            requestJson = publicKeyJson,
            preferImmediatelyAvailableCredentials = false
        )
    }

    /**
     * Build a WebAuthn **assertion** (get) request for Credential Manager.
     *
     * - challenge: server challenge
     * - allowedCredentials: optional allow list (raw ID bytes)
     */
    fun makeAssertionRequest(
        challenge: ByteArray,
        allowedCredentials: List<ByteArray>?,
    ): GetCredentialRequest {
        val publicKeyJson = buildGetPublicKeyOptionsJson(
            rpId = rpId,
            challenge = challenge,
            allowCredentialIds = allowedCredentials.orEmpty()
        )
        val option = GetPublicKeyCredentialOption(publicKeyJson)
        return GetCredentialRequest(listOf(option))
    }

    /** Parse **registration** result (Create) into your attestation form. */
    @Throws(PasskeyError::class)
    fun handleRegistrationResult(credential: CreatePublicKeyCredentialResponse): PasskeyRegistrationResult {
        val regJson = credential.registrationResponseJson
        val parsed = try { json.decodeFromString(PublicKeyCredentialCreateJson.serializer(), regJson) }
        catch (e: Throwable) { throw PasskeyError.DecodeFailed(e) }

        val resp = parsed.response ?: throw PasskeyError.DecodeFailed(IllegalStateException("Missing response"))

        val attObjB64 = resp.attestationObject_b64url ?: throw PasskeyError.MissingAttestationObject

        // Extract challenge string from clientDataJSON
        val clientDataJsonUtf8 = try {
            val bytes = com.turnkey.encoding.decodeBase64Url(resp.clientDataJSON_b64url)
            String(bytes, Charsets.UTF_8)
        } catch (e: Throwable) {
            throw PasskeyError.DecodeFailed(e)
        }
        val challenge = try {
            json.decodeFromString(ClientData.serializer(), clientDataJsonUtf8).challenge
        } catch (e: Throwable) {
            // If clientDataJSON is already a JSON string literal (rare), try a lax parse:
            val elem = json.parseToJsonElement(clientDataJsonUtf8)
            elem.jsonObject["challenge"]?.jsonPrimitive?.content
                ?: throw PasskeyError.DecodeFailed(e)
        }

        val transports = resp.transports?.mapNotNull {
            when (it.lowercase()) {
                "hybrid" -> Transport.hybrid
                "internal" -> Transport.internalTransport
                "usb" -> Transport.usb
                "nfc" -> Transport.nfc
                "ble" -> Transport.ble
                else -> null
            }
        } ?: listOf(Transport.hybrid)

        val credId = (parsed.id ?: parsed.rawId).orEmpty()
        val attestation = Attestation(
            credentialId = credId, // Android’s id/rawId are already base64url
            clientDataJson = resp.clientDataJSON_b64url,
            attestationObject = attObjB64,
            transports = transports
        )
        return PasskeyRegistrationResult(challenge = challenge, attestation = attestation)
    }

    /** Parse **assertion** result (Get) into the AssertionResult model. */
    @Throws(PasskeyError::class)
    fun handleAssertionResult(credential: PublicKeyCredential): AssertionResult {
        val authJson = credential.authenticationResponseJson

        val parsed = try { json.decodeFromString(PublicKeyCredentialAuthJson.serializer(), authJson) }
        catch (e: Throwable) { throw PasskeyError.DecodeFailed(e) }

        val resp = parsed.response ?: throw PasskeyError.DecodeFailed(IllegalStateException("Missing response"))

        val authenticatorData = try { decodeBase64Url(resp.authenticatorData_b64url) }
        catch (e: Throwable) { throw PasskeyError.DecodeFailed(e) }

        val signature = try { decodeBase64Url(resp.signature_b64url) }
        catch (e: Throwable) { throw PasskeyError.DecodeFailed(e) }

        val userHandle = resp.userHandle_b64url?.let {
            try { decodeBase64Url(it) } catch (_: Throwable) { null }
        }

        val clientDataJsonUtf8 = try {
            val bytes = decodeBase64Url(resp.clientDataJSON_b64url)
            String(bytes, Charsets.UTF_8)
        } catch (_: Throwable) {
            // Some OEMs may already return plain text; keep as-is
            resp.clientDataJSON_b64url
        }

        return AssertionResult(
            authenticatorData = authenticatorData,
            clientDataJson = clientDataJsonUtf8,
            credentialId = (parsed.id ?: parsed.rawId).orEmpty(),
            signature = signature,
            userHandle = userHandle
        )
    }
}
