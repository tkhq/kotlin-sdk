package com.turnkey.utils

sealed class PasskeyError(msg: String) : Exception(msg) {
    data object InvalidUserId : PasskeyError("Invalid user ID format.")
    data object MissingAttestationObject : PasskeyError("Missing attestation object in registration.")
    data class DecodeFailed(val causeErr: Throwable) : PasskeyError("Decode failed: ${causeErr.message}")
    data class RegistrationFailed (val error: Throwable) : PasskeyError("Registration failed: ${error.message}")
    data class AssertionFailed (val error: Throwable) : PasskeyError("Assertion failed: ${error.message}")
    data object InvalidChallenge : PasskeyError("Invalid challenge format.")
    data object UnsupportedOperation : PasskeyError("Unsupported operation.")
    data object CredentialConversionFailed : PasskeyError("Failed to convert credential data.")
}