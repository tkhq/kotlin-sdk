package com.turnkey.stamper.utils

sealed class TurnkeyStamperError(
    message: String, cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class FailedToStamp(override val cause: Throwable) :
        TurnkeyStamperError("Failed to stamp payload", cause)

    data class FailedToSign(override val cause: Throwable) :
        TurnkeyStamperError("Failed to sign payload", cause)

    data class InvalidChallenge(val found: Int, val expected: Int, override val cause: Throwable? = null) :
        TurnkeyStamperError(
            "Failed to encode the challenge as UTF-8 data, found payload size: $found, expected: $expected",
            cause
        )

    data class AssertionFailed(override val cause: Throwable) :
        TurnkeyStamperError("Passkey assertion failed", cause)

    data class FailedToEncodeStamp(override val cause: Throwable) :
        TurnkeyStamperError("Failed to encode WebAuthn stamp as JSON", cause)

    data class InvalidJsonString(val found: String, override val cause: Throwable) :
        TurnkeyStamperError(
            "Unable to convert the stamp JSON data to a UTF-8 string, found: $found.",
            cause
        )

    data class InvalidPrivateKey(override val cause: Throwable? = null) :
        TurnkeyStamperError(
            "Invalid private key format. Make sure it's a valid hex string.",
            cause
        )

    data class InvalidPrivateKeyBytes(val found: Int, val expected: Int, override val cause: Throwable? = null) :
        TurnkeyStamperError(
            "Invalid private key bytes size, found: $found, expected: $expected",
            cause
        )

    data class InvalidPublicKey(val found: String, override val cause: Throwable? = null) :
        TurnkeyStamperError("Invalid public key format, found: $found.", cause)

    data class MismatchedPublicKey(
        val expected: String,
        val found: String,
        override val cause: Throwable? = null
    ) : TurnkeyStamperError(
        "Invalid public key format. expected=$expected. found=$found",
        cause
    )

    data class InvalidHexCharacter(override val cause: Throwable? = null) :
        TurnkeyStamperError(
            "The provided hex string contains invalid characters.",
            cause
        )

    data class FailedToSerializePayloadToJson(override val cause: Throwable) :
        TurnkeyStamperError("Failed to serialize payload to JSON", cause)

    data class InvalidDigestLength(val found: Int, override val cause: Throwable? = null) :
        TurnkeyStamperError("Payload must be 32-byte SHA-256 digest, found: $found", cause)

    data class OperationFailed(override val cause: Throwable) :
        TurnkeyStamperError("Operation failed", cause)

    companion object {
        /**
         * wrapper that preserves TurnkeyStamperError types but wraps other exceptions
         * we use this in high-level API functions to handle all exceptions
         */
        fun wrap(t: Throwable): TurnkeyStamperError {
            return when (t) {
                is TurnkeyStamperError -> t  // Already a TurnkeyStamperError, pass through
                else -> OperationFailed(t)  // Wrap other exceptions
            }
        }
    }
}