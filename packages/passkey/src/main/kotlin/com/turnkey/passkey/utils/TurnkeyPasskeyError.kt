package com.turnkey.passkey.utils

sealed class TurnkeyPasskeyError(
    message: String, cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class InvalidUserId(val found: String, override val cause: Throwable?) :
        TurnkeyPasskeyError("Invalid user ID format, found: $found", cause)

    data class MissingAttestationObject(override val cause: Throwable?) :
        TurnkeyPasskeyError("Missing attestation object in registration.", cause)

    data class DecodeFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Decode failed", cause)

    data class RegistrationFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Registration failed", cause)

    data class AssertionFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Assertion failed", cause)

    data class InvalidChallenge(val found: String, override val cause: Throwable?) :
        TurnkeyPasskeyError("Invalid challenge format, found: $found", cause)

    data class UnsupportedOperation(override val cause: Throwable?) :
        TurnkeyPasskeyError("Unsupported operation.", cause)

    data class CredentialConversionFailed(override val cause: Throwable?) :
        TurnkeyPasskeyError("Failed to convert credential data.", cause)

    data class HandleRegistrationResultFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Failed to handle passkey registration result", cause)

    data class HandleAssertionResultFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Failed to handle passkey assertion result", cause)

    data class OperationFailed(override val cause: Throwable) :
        TurnkeyPasskeyError("Operation failed", cause)

    companion object {
        fun wrap(t: Throwable): TurnkeyPasskeyError {
            return when (t) {
                is TurnkeyPasskeyError -> t
                else -> OperationFailed(t)
            }
        }
    }
}