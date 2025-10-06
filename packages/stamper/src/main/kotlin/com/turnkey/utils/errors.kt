package com.turnkey.utils

sealed class ApiKeyStampError(msg: String) : IllegalArgumentException(msg) {
    data object InvalidPrivateKey : ApiKeyStampError("Invalid private key format. Make sure it's a valid hex string.")
    data object InvalidPublicKey : ApiKeyStampError("Invalid public key format.")
    data class MismatchedPublicKey(val expected: String, val found: String) : ApiKeyStampError("Invalid public key format. expected=$expected. found=$found")
    data object InvalidHexCharacter : ApiKeyStampError("The provided hex string contains invalid characters.")
    data object SignatureFailed : ApiKeyStampError("Failed to generate signature using the private key.")
    data class FailedToSerializePayloadToJson (val error: Throwable) : ApiKeyStampError("Failed to serialize payload to JSON: ${error.message}")
    data class InvalidDigestLength(val found: Int) :
        ApiKeyStampError("Payload must be 32-byte SHA-256 digest (found $found)")
}

sealed class PasskeyStampError(msg: String) : IllegalArgumentException(msg) {
    data object InvalidChallenge : PasskeyStampError("Failed to encode the challenge as UTF-8 data.")
    data class AssertionFailed(val error: Throwable) : PasskeyStampError("Passkey assertion failed: ${error.message}")
    data class FailedToEncodeStamp(val error: Throwable) : PasskeyStampError("Failed to encode WebAuthn stamp as JSON: ${error.message}")
    data object InvalidJsonString : PasskeyStampError("Unable to convert the stamp JSON data to a UTF-8 string.")
}

sealed class StampError(msg: String) : IllegalArgumentException(msg) {
    data object MissingCredentials : StampError("Missing credentials. Please ensure API or passkey credentials are configured.")
    data object AssertionFailed : StampError("Failed to complete passkey assertion.")
    data class ApiKeyStampError(val error: Throwable) : StampError(error.message ?: "Unknown api key stamper error")
    data class PasskeyStampError(val error: Throwable) : StampError(error.message ?: "Unknown passkey stamper error")
    data class UnknownError(val s: String) : StampError("An unknown error occurred: $s")
    data object PasskeyManagerNotSet : StampError("The passkey manager has not been initialized.")
    data object InvalidPayload : StampError("Invalid payload format for stamping.")
}