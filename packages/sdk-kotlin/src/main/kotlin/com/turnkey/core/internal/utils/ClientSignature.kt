package com.turnkey.core.internal.utils

import com.turnkey.core.models.ClientSignaturePayload
import com.turnkey.core.models.errors.TurnkeyKotlinError
import com.turnkey.types.V1ApiKeyParamsV2
import com.turnkey.types.V1AuthenticatorParamsV2
import com.turnkey.types.V1LoginUsage
import com.turnkey.types.V1OauthProviderParams
import com.turnkey.types.V1SignupUsage
import com.turnkey.types.V1TokenUsage
import com.turnkey.types.V1UsageType
import kotlinx.serialization.json.Json

/**
 * Utils for building client signature payloads for our OTP auth flows
 *
 * Client signature ensures two things:
 * 1. Only the owner of the public key in the verification token's  claim can use the token
 * 2. the intent has not been tampered with and was directly approved by the key owner
 */
object ClientSignature {
    /**
     * Creates a client signature payload for login flows
     *
     * @param verificationToken the JWT verification token to decode
     * @param sessionPublicKey optional public key to use instead of the one in the token
     * @return ClientSignaturePayload - a tuple containing the JSON string to sign and the public key for client signature
     * @throws `TurnkeyKotlinError.FailedToBuildClientSignature`
     */
    fun forLogin(
        verificationToken: String,
        sessionPublicKey: String? = null
    ): ClientSignaturePayload {
        try {
            val decoded = Helpers.decodeVerificationToken(verificationToken)

            if (decoded.publicKey.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidParameter("Verification token is missing a public key")
            val verificationPublicKey = decoded.publicKey

            // if a sessionPublicKey is passed in, we use it instead
            val resolvedSessionPublicKey = sessionPublicKey ?: verificationPublicKey

            val usage = V1LoginUsage(resolvedSessionPublicKey)
            val payload = V1TokenUsage(login = usage, tokenId = decoded.id, type = V1UsageType.USAGE_TYPE_LOGIN)

            val jsonString: String = Json.encodeToString(V1TokenUsage.serializer(), payload)

            return ClientSignaturePayload(message = jsonString, clientSignaturePublicKey = verificationPublicKey)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToBuildClientSignature(t)
        }
    }

    /**
     * Creates a client signature payload for sign up
     *
     * @param verificationToken the jwt verification token to decode
     * @param email optional email address
     * @param phoneNumber optional phone number
     * @param apiKeys optional array of api keys
     * @param authenticators optional list of authenticators
     * @param oauthProviders optional list of OAuth providers
     * @return `ClientSignaturePayload` - a tuple containing the JSON string to sign and the public key for client signature
     * @throws `TurnkeyKotlinErrors.FailedToBuildClientSignature`
     */
    fun forSignUp(
        verificationToken: String,
        email: String? = null,
        phoneNumber: String? = null,
        apiKeys: List<V1ApiKeyParamsV2>? = null,
        authenticators: List<V1AuthenticatorParamsV2>? = null,
        oauthProviders: List<V1OauthProviderParams>? = null
    ): ClientSignaturePayload {
        try {
            val decoded = Helpers.decodeVerificationToken(verificationToken)

            if (decoded.publicKey.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidParameter("Verification token is missing a public key")
            val verificationPublicKey = decoded.publicKey

            val usage = V1SignupUsage(
                apiKeys = apiKeys,
                authenticators = authenticators,
                email = email,
                phoneNumber = phoneNumber,
                oauthProviders = oauthProviders
            )

            val payload = V1TokenUsage(signup = usage, tokenId = decoded.id, type = V1UsageType.USAGE_TYPE_SIGNUP)

            val jsonString: String = Json.encodeToString(V1TokenUsage.serializer(), payload)

            return ClientSignaturePayload(message = jsonString, clientSignaturePublicKey = verificationPublicKey)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToBuildClientSignature(t)
        }
    }
}