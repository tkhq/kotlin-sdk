package com.turnkey.crypto.models

import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

/**
 * Raw P-256 key pair represented as hex strings.
 *
 * @property publicKeyUncompressed 65-byte uncompressed public key: 0x04 || X(32) || Y(32)
 * @property publicKeyCompressed 33-byte compressed public key: 0x02/0x03 || X(32)
 * @property privateKey 32-byte private key scalar
 */
data class RawP256KeyPair(
    val publicKeyUncompressed: String, // 0x04 || X(32) || Y(32) -> 65 bytes hex
    val publicKeyCompressed: String,   // 0x02/0x03 || X(32)     -> 33 bytes hex
    val privateKey: String             // raw scalar d (32 bytes hex)
)

/**
 * P-256 key pair using Java security interfaces.
 *
 * @property publicKey Java ECPublicKey instance
 * @property privateKey Java ECPrivateKey instance
 */
data class P256KeyPair(
    val publicKey: ECPublicKey,
    val privateKey: ECPrivateKey,
)