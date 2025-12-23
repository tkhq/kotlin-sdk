package com.turnkey.core.models.errors

import com.turnkey.stamper.utils.TurnkeyStamperError

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

    data class LocalStoreListKeysFailed(override val cause: Throwable? = null) :
            TurnkeyStorageError("Failed to list keys from local storage", cause)

    data class KeyNotFound(override val cause: Throwable? = null): TurnkeyStorageError("Key not found", cause)
}