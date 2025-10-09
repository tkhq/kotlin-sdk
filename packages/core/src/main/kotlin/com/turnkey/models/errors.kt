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

    data class InvalidResponse(val s: String): TurnkeyKotlinError(s)
    data class FailedToRefreshSession(val t: Throwable) : TurnkeyKotlinError("Error refreshing session", t)
}