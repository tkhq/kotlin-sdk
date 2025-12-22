package com.turnkey.core.models.errors

sealed class TurnkeyKotlinError(message: String, cause: Throwable? = null) :
    Exception(
        if (cause != null) "$message - error: ${cause.message}" else message,
        cause
    ) {
    data class InvalidRefreshTTL(
        override val message: String,
        override val cause: Throwable? = null
    ) : TurnkeyKotlinError(message, cause)

    data class FailedToCreateSession(override val cause: Throwable? = null) :
        TurnkeyKotlinError("Failed to create session from jwt", cause)

    data class KeyAlreadyExists(val sessionKey: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Session key $sessionKey already exists", cause)

    data class KeyNotFound(val sessionKey: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Session key $sessionKey not found", cause)

    data class ClientNotInitialized(override val cause: Throwable? = null) : TurnkeyKotlinError(
        """
            Turnkey client not ready. 
            
            Did you:
            1. Call TurnkeyContext.init(app, config) in Application.onCreate()?
            2. Wait for initialization with TurnkeyContext.awaitReady()?
            
            Example:
                lifecycleScope.launch {
                    TurnkeyContext.awaitReady()
                    // Now safe to use TurnkeyContext.client
                }
            """.trimIndent()
    )

    data class FailedToSetSelectedSession(val e: Throwable) :
        TurnkeyKotlinError("Failed to set selected session", e)

    data class InvalidSession(override val cause: Throwable? = null) :
        TurnkeyKotlinError("Invalid session", cause)

    data class InvalidResponse(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Invalid response: $s", cause)

    data class InvalidParameter(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Invalid parameter(s): $s", cause)

    data class FailedToRefreshSession(override val cause: Throwable) :
        TurnkeyKotlinError("Error refreshing session", cause)

    data class FailedToSignRawPayload(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to sign using SignRawPayload", cause)

    data class FailedToLoginWithOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to login with OAuth", cause)

    data class FailedToSignUpWithOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to sign up with OAuth", cause)

    data class FailedToLoginOrSignUpWithOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to login or sign up with OAuth", cause)

    data class FailedToLoginWithPasskey(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to login with passkey", cause)

    data class FailedToSignUpWithPasskey(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to sign up with passkey", cause)

    data class FailedToLoginWithOtp(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to login with OTP", cause)

    data class FailedToSignUpWithOtp(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to sign up with OTP", cause)

    data class FailedToLoginOrSignUpWithOtp(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to login or sign up with OTP", cause)

    data class FailedToCreateWallet(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to create wallet", cause)

    data class FailedToHandleGoogleOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to handle google OAuth", cause)

    data class FailedToHandleAppleOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to handle apple OAuth", cause)

    data class FailedToHandleXOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to handle X OAuth", cause)

    data class FailedToHandleDiscordOAuth(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to handle discord OAuth", cause)

    data class FailedToImportWallet(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to import wallet", cause)

    data class FailedToExportWallet(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to export wallet", cause)

    data class SignUpFailed(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Sign up failed: $s", cause)

    data class MissingRpId(override val cause: Throwable? = null) :
        TurnkeyKotlinError(
            "Missing rpId, please pass an rpId through the function params or set it in the config",
            cause
        )

    data class NoSessionsFound(override val cause: Throwable? = null) :
        TurnkeyKotlinError("No session found", cause)

    data class MissingConfigParam(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Missing config param: $s", cause)

    data class FailedToSignMessage(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to sign message", cause)

    data class InvalidMessage(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("Invalid sign message payload: $s", cause)

    data class FailedToPurgeSession(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to purge session", cause)

    data class OAuthStateMismatch(val s: String, override val cause: Throwable? = null) :
        TurnkeyKotlinError("OAuth state mismatch: $s", cause)

    data class FailedToBuildClientSignature(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to build client signature payload", cause)

    data class FailedToRescheduleSessionExpiries(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to reschedule session expiry", cause)

    data class FailedToClearSession(override val cause: Throwable? = null) :
        TurnkeyKotlinError("Failed to clear session", cause)

    data class FailedToPersistSession(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to persist session", cause)

    data class FailedToRefreshWallets(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to refresh wallets", cause)

    data class FailedToRefreshUsers(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to refresh users", cause)

    data class FailedToVerifyOtp(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to verify otp", cause)

    data class FailedToInitOtp(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to init otp", cause)

    data class FailedToDeleteUnusedKeyPairs(override val cause: Throwable) :
        TurnkeyKotlinError("Failed to delete unused key pairs", cause)

    data class FailedToDeleteKeyPair(val k: String, override val cause: Throwable) :
            TurnkeyKotlinError("Failed to delete key pair with public key $k", cause)

    data class FailedToCreateKeyPair(override val cause: Throwable) :
            TurnkeyKotlinError("Failed to create key pair", cause)

    data class FailedToClearAllSessions(override val cause: Throwable) :
            TurnkeyKotlinError("Failed to clear all sessions", cause)

    data class FailedToUpdateSession(override val cause: Throwable) :
            TurnkeyKotlinError("Failed to update session", cause)
    data class FailedToScheduleExpiryTimer(val s: String, override val cause: Throwable) :
            TurnkeyKotlinError("Failed to schedule expiry timer for session key $s", cause)

    data class FailedToGetAuthProxyConfig(override val cause: Throwable) :
            TurnkeyKotlinError("Failed to get auth proxy config", cause)
}