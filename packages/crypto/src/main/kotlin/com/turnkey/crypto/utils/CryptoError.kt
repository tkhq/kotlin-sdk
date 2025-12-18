package com.turnkey.crypto.utils

/**
 * Base exception class for all Turnkey crypto operations.
 * Wraps underlying errors with descriptive messages, similar to TurnkeyReactNativeError.
 */
sealed class CryptoError(
    message: String,
    cause: Throwable? = null
) : Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
) {
    data class InvalidCompressedKeyLength(val found: Int, override val cause: Throwable? = null) : 
        CryptoError("Compressed key must be 33 bytes. found=$found", cause)
    
    data class InvalidPrivateLength(val expected: Int, val found: Int, override val cause: Throwable? = null) :
        CryptoError("Private key must be $expected bytes, found $found", cause)
    
    data class InvalidPublicLength(val expected: Int, val found: Int, override val cause: Throwable? = null) :
        CryptoError("Public key must be $expected bytes, found $found", cause)
    
    data class InvalidHexString(val s: String, override val cause: Throwable? = null) : 
        CryptoError("Invalid hex string: $s", cause)
    
    data class MissingEncappedPublic(override val cause: Throwable? = null): 
        CryptoError("Signed payload lacked \"encappedPublic\"", cause)
    
    data class MissingCiphertext(override val cause: Throwable? = null): 
        CryptoError("Signed payload lacked \"ciphertext\"", cause)
    
    data class OrgIdMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        CryptoError("organizationId mismatch. expected=$expected found=$found", cause)
    
    data class UserIdMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        CryptoError("userId mismatch. expected=$expected found=$found", cause)
    
    data class SignatureVerificationFailed(override val cause: Throwable? = null) : 
        CryptoError("Signature verification failed", cause)
    
    data class EncodingFailed(override val cause: Throwable) :
        CryptoError("JSON encoding failed", cause)
    
    data class DecodingFailed(override val cause: Throwable) : 
        CryptoError("Decoding failed", cause)
    
    data class SignerMismatch(val expected: String, val found: String?, override val cause: Throwable? = null) :
        CryptoError("Signer mismatch. expected=$expected found=$found", cause)
    
    data class InvalidPublicKey(override val cause: Throwable) : 
        CryptoError("Invalid public key", cause)
    
    data class InvalidPrivateKey(override val cause: Throwable) : 
        CryptoError("Invalid private key", cause)

    data class OperationFailed(override val cause: Throwable) : 
        CryptoError("Operation failed", cause)
    
    companion object {
        /**
         * wrapper that preserves CryptoError types but wraps other exceptions
         * we use this in high-level API functions to handle all exceptions
         */
        fun wrap(e: Throwable): CryptoError {
            return when (e) {
                is CryptoError -> e  // Already a CryptoError, pass through
                else -> OperationFailed(e)  // Wrap other exceptions
            }
        }
    }
}
