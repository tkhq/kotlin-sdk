package com.turnkey.utils

sealed class CryptoError(msg: String) : IllegalArgumentException(msg) {
    data class InvalidCompressedKeyLength(val found: Int) : CryptoError("Compressed key must be 33 bytes. found=$found")
    data class InvalidPrivateLength(val expected: Int, val found: Int) :
        CryptoError("Private key must be $expected bytes, found $found")
    data class InvalidPublicLength(val expected: Int, val found: Int) :
        CryptoError("Public key must be $expected bytes, found $found")
    data class InvalidHexString(val s: String) : CryptoError("Invalid hex string: $s")
    data object InvalidUTF8 : CryptoError("Invalid UTF-8")
    data object MissingEncappedPublic: CryptoError("Signed payload lacked “encappedPublic”.")
    data object MissingCiphertext: CryptoError("Signed payload lacked “ciphertext”.")
    data class OrgIdMismatch(val expected: String, val found: String?) :
        CryptoError("organizationId mismatch. expected=$expected found=$found")
    data class UserIdMismatch(val expected: String, val found: String?) :
        CryptoError("userId mismatch. expected=$expected found=$found")
    data object SignatureVerificationFailed : CryptoError("Signature verification failed")
    data class EncodingFailed(val causeErr: Throwable) :
        CryptoError("JSON decoding failed: ${causeErr.message}")
    data class SignerMismatch(val expected: String, val found: String?) :
        CryptoError("Signer mismatch. expected=$expected found=$found")
    data class InvalidPublicKey(val causeErr: Throwable) : CryptoError("Invalid public key: ${causeErr.message}")
    data class InvalidPrivateKey(val causeErr: Throwable) : CryptoError("Invalid private key: ${causeErr.message}")
    data class DecodingFailed(val causeErr: Throwable) : CryptoError("Decoding failed: ${causeErr.message}")
}