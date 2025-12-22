package com.turnkey.core.models.errors

sealed class TurnkeyStorageError(message: String, cause: Throwable? = null) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class InvalidJWT(override val cause: Throwable? = null) :
        TurnkeyStorageError("Invalid JWT format or payload", cause)

    data class DecodingFailed(override val cause: Throwable) :
        TurnkeyStorageError("Failed to decode JWT payload", cause)

    data class KeyEncodingFailed(val key: String, override val cause: Throwable) :
        TurnkeyStorageError("Encoding failed for key=$key", cause)

    data class KeyDecodingFailed(val key: String, override val cause: Throwable) :
        TurnkeyStorageError("Decoding failed for key=$key", cause)

    data class KeychainAddFailed(val status: Int, override val cause: Throwable? = null) :
        TurnkeyStorageError("Add failed: $status", cause)

    data class KeychainDeleteFailed(val status: Int, override val cause: Throwable? = null) : TurnkeyStorageError("Delete failed: $status", cause)
    data class KeychainFetchFailed(val status: Int, override val cause: Throwable? = null) : TurnkeyStorageError("Fetch failed: $status", cause)
    data class InvalidCiphertext(override val cause: Throwable? = null) : TurnkeyStorageError("Invalid ciphertext", cause)

    data class KeyNotFound(override val cause: Throwable? = null): TurnkeyStorageError("Key not found", cause)

    data class KeychainListKeysFailed(val status: Int, override val cause: Throwable? = null) : TurnkeyStorageError("List failed: $status", cause)
}