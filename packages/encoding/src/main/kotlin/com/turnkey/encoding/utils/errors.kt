package com.turnkey.encoding.utils

/**
 * Base exception class for all Turnkey encoding/decoding operations.
 * Wraps underlying errors with descriptive messages.
 */
sealed class TurnkeyEncodingError(
    message: String,
    cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class OddLengthString(val length: Int, override val cause: Throwable? = null) :
        TurnkeyEncodingError("Hex string must have even length, found $length", cause)
    
    data class InvalidHexCharacter(val char: Char, val index: Int, override val cause: Throwable? = null) :
        TurnkeyEncodingError("Invalid hex character '$char' at index $index", cause)
    
    data class InvalidBase64Url(val input: String, override val cause: Throwable? = null) :
        TurnkeyEncodingError("Invalid base64url string", cause)
    
    data class InvalidBase58Check(override val cause: Throwable) :
        TurnkeyEncodingError("Base58Check decoding failed - invalid checksum or format", cause)
    
    data class InvalidUTF8(override val cause: Throwable) :
        TurnkeyEncodingError("Invalid UTF-8 byte sequence", cause)

    data class OperationFailed(override val cause: Throwable) :
        TurnkeyEncodingError("Encoding operation failed", cause)
    
    companion object {
        /**
         * Wrapper that preserves TurnkeyEncodingError types but wraps other exceptions.
         * Use this in high-level API functions to handle all exceptions.
         */
        fun wrap(e: Throwable): TurnkeyEncodingError {
            return when (e) {
                is TurnkeyEncodingError -> e
                else -> OperationFailed(e)
            }
        }
    }
}