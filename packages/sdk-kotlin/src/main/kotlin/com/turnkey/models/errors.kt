package com.turnkey.models

sealed class StorageError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    data object InvalidJWT : StorageError("Invalid JWT format or payload") {
        private fun readResolve(): Any = InvalidJWT
    }
    class DecodingFailed(cause: Throwable) : StorageError("Failed to decode JWT payload", cause)
    class KeyEncodingFailed(val key: String, cause: Throwable) : StorageError("Encoding failed for key=$key", cause)
    class KeyDecodingFailed(val key: String, cause: Throwable) : StorageError("Decoding failed for key=$key", cause)
    class KeychainAddFailed(val status: Int) : StorageError("Add failed: $status")
    class KeychainDeleteFailed(val status: Int) : StorageError("Delete failed: $status")
    class KeychainFetchFailed(val status: Int) : StorageError("Fetch failed: $status")
    data object InvalidCiphertext : StorageError("Invalid ciphertext") {
        private fun readResolve(): Any = InvalidCiphertext
    }
    data object KeyNotFound : StorageError("Key not found") {
        private fun readResolve(): Any = KeyNotFound
    }
    class KeychainListKeysFailed(val status: Int) : StorageError("List failed: $status")
}

sealed class TurnkeyKotlinError(message: String, cause: Throwable? = null): Exception(message, cause) {
    data class InvalidRefreshTTL(val s: String): TurnkeyKotlinError(s)
    data class FailedToCreateSession(val e: Throwable): TurnkeyKotlinError("Failed to create session from jwt", e)
    data class KeyAlreadyExists(val sessionKey: String) : TurnkeyKotlinError("Session key $sessionKey already exists")
    data class KeyNotFound(val sessionKey: String) : TurnkeyKotlinError("Session key $sessionKey not found")
    data object ClientNotInitialized : TurnkeyKotlinError("The Turnkey Client has not been initialized") {
        private fun readResolve(): Any = ClientNotInitialized
    }

    data class FailedToSetSelectedSession(val e: Throwable): TurnkeyKotlinError("Failed to set selected session", e)
    data object InvalidSession: TurnkeyKotlinError("Invalid session") {
        private fun readResolve(): Any = InvalidSession
    }

    data class InvalidResponse(val s: String): TurnkeyKotlinError("Invalid response: $s")
    data class FailedToRefreshSession(val t: Throwable) : TurnkeyKotlinError("Error refreshing session", t)
    data class FailedToSignRawPayload(val t: Throwable) : TurnkeyKotlinError("Failed to sign using SignRawPayload", t)
    data class FailedToLoginWithOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to login with OAuth", t)
    data class FailedToSignUpWithOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to sign up with OAuth", t)
    data class FailedToLoginOrSignUpWithOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to login or sign up with OAuth", t)
    data class FailedToLoginWithPasskey(val t: Throwable) : TurnkeyKotlinError("Failed to login with passkey", t)
    data class FailedToSignUpWithPasskey(val t: Throwable) : TurnkeyKotlinError("Failed to sign up with passkey", t)
    data class FailedToLoginWithOtp(val t: Throwable) : TurnkeyKotlinError("Failed to login with OTP", t)
    data class FailedToSignUpWithOtp(val t: Throwable) : TurnkeyKotlinError("Failed to sign up with OTP", t)
    data class FailedToLoginOrSignUpWithOtp(val t: Throwable) : TurnkeyKotlinError("Failed to login or sign up with OTP", t)
    data class FailedToCreateWallet(val t: Throwable) : TurnkeyKotlinError("Failed to create wallet", t)
    data class FailedToHandleGoogleOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to handle google OAuth", t)
    data class FailedToHandleAppleOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to handle apple OAuth", t)
    data class FailedToHandleXOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to handle X OAuth", t)
    data class FailedToHandleDiscordOAuth(val t: Throwable) : TurnkeyKotlinError("Failed to handle discord OAuth", t)
    data class FailedToImportWallet(val t: Throwable) : TurnkeyKotlinError("Failed to import wallet", t)
    data class FailedToExportWallet(val t: Throwable) : TurnkeyKotlinError("Failed to export wallet", t)
    data class SignUpFailed(val s: String) : TurnkeyKotlinError("Sign up failed: $s")
    data object MissingRpId : TurnkeyKotlinError("Missing rpId, please pass an rpId through the function params or set it in the config") {
        private fun readResolve(): Any = MissingRpId
    }
    data object NoSessionsFound : TurnkeyKotlinError("No session found") {
        private fun readResolve(): Any = NoSessionsFound
    }
    data class MissingConfigParam(val s: String) : TurnkeyKotlinError("Missing config param: $s")
    data class FailedToSignMessage(val t: Throwable) : TurnkeyKotlinError("Failed to sign message", t)
    data class InvalidMessage (val s: String) : TurnkeyKotlinError("Invalid sign message payload: $s")
    data class FailedToPurgeSession(val t: Throwable) : TurnkeyKotlinError("Failed to purge session", t)
    data class OAuthStateMismatch(val s: String) : TurnkeyKotlinError("OAuth state mismatch: $s")
}