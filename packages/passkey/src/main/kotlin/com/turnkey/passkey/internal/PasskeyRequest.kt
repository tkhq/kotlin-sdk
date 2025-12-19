package com.turnkey.passkey.internal

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import com.turnkey.encoding.decodeBase64Url
import com.turnkey.passkey.PasskeyRegistrationResult
import com.turnkey.types.V1Attestation
import com.turnkey.types.V1AuthenticatorTransport
import com.turnkey.passkey.utils.PasskeyError
import com.turnkey.passkey.utils.buildCreatePublicKeyOptionsJson
import com.turnkey.passkey.utils.buildGetPublicKeyOptionsJson
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
        @SerialName("clientDataJSON") val clientDataJSONB64url: String,
        @SerialName("attestationObject") val attestationObjectB64url: String? = null,
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
        @SerialName("clientDataJSON") val clientDataJSONB64url: String,
        @SerialName("authenticatorData") val authenticatorDataB64url: String,
        @SerialName("signature") val signatureB64url: String,
        @SerialName("userHandle") val userHandleB64url: String? = null
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

    /** Parse **registration** result (Create) into an attestation form. */
    @Throws(PasskeyError::class)
    fun handleRegistrationResult(credential: CreatePublicKeyCredentialResponse): PasskeyRegistrationResult {
        try {
            val regJson = credential.registrationResponseJson
            val parsed = try {
                json.decodeFromString(PublicKeyCredentialCreateJson.serializer(), regJson)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            val resp = parsed.response
                ?: throw IllegalStateException("Failed to decode registration response, missing response")

            val attObjB64 =
                resp.attestationObjectB64url ?: throw IllegalStateException("Missing attestation object from registration response")

            // Extract challenge string from clientDataJSON
            val clientDataJsonUtf8 = try {
                val bytes = decodeBase64Url(resp.clientDataJSONB64url)
                String(bytes, Charsets.UTF_8)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }
            val challenge = try {
                json.decodeFromString(ClientData.serializer(), clientDataJsonUtf8).challenge
            } catch (e: Throwable) {
                val elem = json.parseToJsonElement(clientDataJsonUtf8)
                elem.jsonObject["challenge"]?.jsonPrimitive?.content
                    ?: throw PasskeyError.DecodeFailed(e)
            }

            val transports = resp.transports?.mapNotNull {
                when (it.lowercase()) {
                    "hybrid" -> V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_HYBRID
                    "internal" -> V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_INTERNAL
                    "usb" -> V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_USB
                    "nfc" -> V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_NFC
                    "ble" -> V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_BLE
                    else -> null
                }
            } ?: listOf(V1AuthenticatorTransport.AUTHENTICATOR_TRANSPORT_HYBRID)

            val credId = (parsed.id ?: parsed.rawId).orEmpty()
            val attestation = V1Attestation(
                credentialId = credId,
                clientDataJson = resp.clientDataJSONB64url,
                attestationObject = attObjB64,
                transports = transports
            )
            return PasskeyRegistrationResult(challenge = challenge, attestation = attestation)
        } catch (t: Throwable) {
            throw PasskeyError.HandleRegistrationResultFailed(t)
        }
    }

    /** Parse **assertion** result (Get) into the AssertionResult model. */
    @Throws(PasskeyError::class)
    fun handleAssertionResult(credential: PublicKeyCredential): AssertionResult {
        try {
            val authJson = credential.authenticationResponseJson

            val parsed = try {
                json.decodeFromString(PublicKeyCredentialAuthJson.serializer(), authJson)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            val resp = parsed.response
                ?: throw IllegalStateException("Failed to decode assertion result, missing response")

            // These fields are already base64url in CM JSON; decode to bytes here.
            val authenticatorData = try {
                decodeBase64Url(resp.authenticatorDataB64url)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            val signature = try {
                decodeBase64Url(resp.signatureB64url)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            val clientDataBytes = try {
                decodeBase64Url(resp.clientDataJSONB64url)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            val userHandle = resp.userHandleB64url?.let {
                try {
                    decodeBase64Url(it)
                } catch (_: Throwable) {
                    null
                }
            }

            // Prefer rawId (base64url) → bytes. Fallback to id if rawId missing.
            val credIdB64 = parsed.rawId ?: parsed.id
            ?: throw IllegalStateException("Missing credential id from assertion response")
            val credentialId = try {
                decodeBase64Url(credIdB64)
            } catch (e: Throwable) {
                throw PasskeyError.DecodeFailed(e)
            }

            return AssertionResult(
                authenticatorData = authenticatorData,
                clientDataBytes = clientDataBytes,
                credentialId = credentialId,
                signature = signature,
                userHandle = userHandle
            )
        } catch (t: Throwable) {
            throw PasskeyError.HandleAssertionResultFailed(t)
        }
    }
}
