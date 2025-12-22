package com.turnkey.crypto.utils

/**
 * Base exception class for all Turnkey crypto operations.
 * Wraps underlying errors with descriptive messages, similar to TurnkeyReactNativeError.
 */
sealed class TurnkeyCryptoError(
    message: String,
    cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class InvalidCompressedKeyLength(val found: Int, override val cause: Throwable? = null) : 
        TurnkeyCryptoError("Compressed key must be 33 bytes. found=$found", cause)
    
    data class InvalidPrivateLength(val expected: Int, val found: Int, override val cause: Throwable? = null) :
        TurnkeyCryptoError("Private key must be $expected bytes, found $found", cause)
    
    data class InvalidPublicLength(val expected: Int, val found: Int, override val cause: Throwable? = null) :
        TurnkeyCryptoError("Public key must be $expected bytes, found $found", cause)
    
    data class InvalidHexString(val s: String, override val cause: Throwable? = null) : 
        TurnkeyCryptoError("Invalid hex string: $s", cause)
    
    data class MissingEncappedPublic(override val cause: Throwable? = null): 
        TurnkeyCryptoError("Signed payload lacked \"encappedPublic\"", cause)
    
    data class MissingCiphertext(override val cause: Throwable? = null): 
        TurnkeyCryptoError("Signed payload lacked \"ciphertext\"", cause)
    
    data class OrgIdMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        TurnkeyCryptoError("organizationId mismatch. expected=$expected found=$found", cause)
    
    data class UserIdMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        TurnkeyCryptoError("userId mismatch. expected=$expected found=$found", cause)
    
    data class SignatureVerificationFailed(override val cause: Throwable? = null) : 
        TurnkeyCryptoError("Signature verification failed", cause)
    
    data class EncodingFailed(override val cause: Throwable) :
        TurnkeyCryptoError("JSON encoding failed", cause)
    
    data class DecodingFailed(override val cause: Throwable) : 
        TurnkeyCryptoError("Decoding failed", cause)
    
    data class SignerMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        TurnkeyCryptoError("Signer mismatch. expected=$expected found=$found", cause)
    
    data class InvalidPublicKey(override val cause: Throwable) : 
        TurnkeyCryptoError("Invalid public key", cause)
    
    data class InvalidPrivateKey(override val cause: Throwable) : 
        TurnkeyCryptoError("Invalid private key", cause)

    data class OperationFailed(override val cause: Throwable) : 
        TurnkeyCryptoError("Operation failed", cause)
    
    companion object {
        /**
         * wrapper that preserves TurnkeyCryptoError types but wraps other exceptions
         * we use this in high-level API functions to handle all exceptions
         */
        fun wrap(e: Throwable): TurnkeyCryptoError {
            return when (e) {
                is TurnkeyCryptoError -> e  // Already a TurnkeyCryptoError, pass through
                else -> OperationFailed(e)  // Wrap other exceptions
            }
        }
    }
}
