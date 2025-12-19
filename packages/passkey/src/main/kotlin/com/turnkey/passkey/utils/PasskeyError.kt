package com.turnkey.passkey.utils

sealed class PasskeyError(
    message: String, cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class InvalidUserId (val found: String, override val cause: Throwable?) : PasskeyError("Invalid user ID format, found: $found", cause)
    data class MissingAttestationObject (override val cause: Throwable?) : PasskeyError("Missing attestation object in registration.", cause)
    data class DecodeFailed(override val cause: Throwable) : PasskeyError("Decode failed", cause)
    data class RegistrationFailed (override val cause: Throwable) : PasskeyError("Registration failed", cause)
    data class AssertionFailed (override val cause: Throwable) : PasskeyError("Assertion failed", cause)
    data class InvalidChallenge (val found: String, override val cause: Throwable?) : PasskeyError("Invalid challenge format, found: $found", cause)
    data class UnsupportedOperation (override val cause: Throwable?) : PasskeyError("Unsupported operation.", cause)
    data class CredentialConversionFailed (override val cause: Throwable?) : PasskeyError("Failed to convert credential data.", cause)
    data class HandleRegistrationResultFailed (override val cause: Throwable) : PasskeyError("Failed to handle passkey registration result", cause)
    data class HandleAssertionResultFailed (override val cause: Throwable) : PasskeyError("Failed to handle passkey assertion result", cause)
}