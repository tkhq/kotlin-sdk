package com.turnkey.core.internal.utils

import com.turnkey.core.models.ClientSignaturePayload
import com.turnkey.core.models.errors.TurnkeyKotlinError
import com.turnkey.types.V1ApiKeyParamsV2
import com.turnkey.types.V1AuthenticatorParamsV2
import com.turnkey.types.V1LoginUsage
import com.turnkey.types.V1LoginUsageV2
import com.turnkey.types.V1OauthProviderParamsV2
import com.turnkey.types.V1RootUserParamsV5
import com.turnkey.types.V1SignupUsage
import com.turnkey.types.V1SignupUsageV2
import com.turnkey.types.V1SignupUsageV3
import com.turnkey.types.V1TokenUsage
import com.turnkey.types.V1UsageType
import com.turnkey.types.V1WalletParams
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
     * Uses strict login usage only when every final request value is known.
     */
    internal fun forLoginForRequest(
        verificationToken: String,
        organizationId: String?,
        invalidateExisting: Boolean,
        expirationSeconds: String?
    ): ClientSignaturePayload {
        val targetOrganizationId = organizationId?.takeIf { it.isNotBlank() }
        return if (targetOrganizationId != null && expirationSeconds != null) {
            forLoginV2(verificationToken, targetOrganizationId, invalidateExisting, expirationSeconds)
        } else {
            forLogin(verificationToken)
        }
    }

    /**
     * Creates a client signature payload that binds a login request's final semantics.
     *
     * This may only be used when the target organization and auth proxy session configuration
     * are known before signing.
     */
    internal fun forLoginV2(
        verificationToken: String,
        organizationId: String,
        invalidateExisting: Boolean,
        expirationSeconds: String?
    ): ClientSignaturePayload {
        try {
            if (organizationId.isBlank()) throw TurnkeyKotlinError.InvalidParameter("Organization ID is required for strict login usage")

            val decoded = Helpers.decodeVerificationToken(verificationToken)

            if (decoded.publicKey.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidParameter("Verification token is missing a public key")
            val verificationPublicKey = decoded.publicKey

            val usage = V1LoginUsageV2(
                organizationId = organizationId,
                publicKey = verificationPublicKey,
                invalidateExisting = invalidateExisting,
                expirationSeconds = expirationSeconds
            )
            val payload = V1TokenUsage(loginV2 = usage, tokenId = decoded.id, type = V1UsageType.USAGE_TYPE_LOGIN)

            val jsonString = Json.encodeToString(V1TokenUsage.serializer(), payload)

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
        oauthProviders: List<V1OauthProviderParamsV2>? = null
    ): ClientSignaturePayload {
        try {
            val decoded = Helpers.decodeVerificationToken(verificationToken)

            if (decoded.publicKey.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidParameter("Verification token is missing a public key")
            val verificationPublicKey = decoded.publicKey

            val usage = V1SignupUsageV2(
                apiKeys = apiKeys,
                authenticators = authenticators,
                email = email,
                phoneNumber = phoneNumber,
                oauthProviders = oauthProviders
            )

            val payload = V1TokenUsage(signupV2 = usage, tokenId = decoded.id, type = V1UsageType.USAGE_TYPE_SIGNUP)

            val jsonString: String = Json.encodeToString(V1TokenUsage.serializer(), payload)

            return ClientSignaturePayload(message = jsonString, clientSignaturePublicKey = verificationPublicKey)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToBuildClientSignature(t)
        }
    }

    /**
     * Creates a client signature payload that binds a sign-up request's final semantics.
     *
     * Required nested collections are passed through explicitly, including empty collections.
     */
    internal fun forSignUpV3(
        verificationToken: String,
        parentOrganizationId: String,
        subOrganizationName: String,
        rootUsers: List<V1RootUserParamsV5>,
        rootQuorumThreshold: Long,
        wallet: V1WalletParams? = null
    ): ClientSignaturePayload {
        try {
            if (parentOrganizationId.isBlank()) throw TurnkeyKotlinError.InvalidParameter("Parent organization ID is required for strict sign-up usage")

            val decoded = Helpers.decodeVerificationToken(verificationToken)

            if (decoded.publicKey.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidParameter("Verification token is missing a public key")
            val verificationPublicKey = decoded.publicKey

            val usage = V1SignupUsageV3(
                parentOrganizationId = parentOrganizationId,
                subOrganizationName = subOrganizationName,
                rootUsers = rootUsers,
                rootQuorumThreshold = rootQuorumThreshold,
                wallet = wallet
            )
            val payload = V1TokenUsage(signupV3 = usage, tokenId = decoded.id, type = V1UsageType.USAGE_TYPE_SIGNUP)

            val jsonString = Json.encodeToString(V1TokenUsage.serializer(), payload)

            return ClientSignaturePayload(message = jsonString, clientSignaturePublicKey = verificationPublicKey)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToBuildClientSignature(t)
        }
    }
}
