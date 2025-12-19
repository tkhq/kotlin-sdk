package com.turnkey.encoding

import com.turnkey.encoding.internal.SECURE_RANDOM
import com.turnkey.encoding.internal.hexNibbleOrThrow
import com.turnkey.encoding.utils.TurnkeyEncodingError
import java.util.Base64

/** Lowercase hex string for this byte array. */
fun ByteArray.toHexString(): String {
    val hexChars = "0123456789abcdef".toCharArray()
    val out = CharArray(size * 2)
    var i = 0
    for (b in this) {
        val v = b.toInt() and 0xFF
        out[i++] = hexChars[v ushr 4]
        out[i++] = hexChars[v and 0x0F]
    }
    return String(out)
}

/** Decode even-length hex string to bytes; returns null on error. */
fun String.hexToBytesOrNull(): ByteArray? = try {
    decodeHex(this)
} catch (_: TurnkeyEncodingError) {
    null
}

/** Decode even-length hex string to bytes; throws TurnkeyEncodingError on error. */
@Throws(TurnkeyEncodingError::class)
fun decodeHex(hex: String): ByteArray = try {
    if (hex.length % 2 != 0) throw TurnkeyEncodingError.OddLengthString(hex.length)
    val out = ByteArray(hex.length / 2)
    var i = 0
    var j = 0
    while (i < hex.length) {
        val hi = hex[i++].hexNibbleOrThrow(i - 1)
        val lo = hex[i++].hexNibbleOrThrow(i - 1)
        out[j++] = ((hi shl 4) or lo).toByte()
    }
    out
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}

/** Base64url (URL-safe) string without padding. */
fun ByteArray.toBase64Url(): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString(this)

/** Decode a base64url string (with or without padding); returns null on error. */
fun String.base64UrlToBytesOrNull(): ByteArray? = try {
    decodeBase64Url(this)
} catch (_: TurnkeyEncodingError) {
    null
}

/** Decode a base64url string (with or without padding); throws TurnkeyEncodingError on invalid input. */
@Throws(TurnkeyEncodingError::class)
fun decodeBase64Url(b64url: String): ByteArray = try {
    // java.util.Base64 URL decoder expects proper padding; add it if missing
    val padNeeded = (4 - (b64url.length % 4)) % 4
    val padded = if (padNeeded == 0) b64url else b64url + "=".repeat(padNeeded)
    Base64.getUrlDecoder().decode(padded)
} catch (e: IllegalArgumentException) {
    throw TurnkeyEncodingError.InvalidBase64Url(b64url, e)
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}

/** Cryptographically secure random byte array. */
fun randomBytes(count: Int): ByteArray = ByteArray(count).also { SECURE_RANDOM.nextBytes(it) }

/**
 * Decodes a Base58Check-encoded string to its payload bytes.
 * Verifies the 4-byte checksum and removes it from the result.
 *
 * @param s Base58Check-encoded string
 * @return Decoded payload bytes (without checksum)
 * @throws TurnkeyEncodingError.InvalidBase58Check if checksum verification fails
 */
@Throws(TurnkeyEncodingError::class)
fun base58CheckDecode(s: String): ByteArray = try {
    org.bitcoinj.core.Base58.decodeChecked(s)
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}

/**
 * Encodes payload bytes to Base58Check format.
 * Adds a 4-byte double-SHA256 checksum before encoding.
 *
 * @param payload Bytes to encode
 * @return Base58Check-encoded string
 */
fun base58CheckEncode(payload: ByteArray): String = try {
    val checksum = sha256(sha256(payload)).copyOfRange(0, 4)
    org.bitcoinj.core.Base58.encode(payload + checksum)
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}

/** Compute SHA-256 hash of the input bytes. */
fun sha256(bytes: ByteArray): ByteArray = try {
    java.security.MessageDigest.getInstance("SHA-256").digest(bytes)
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}

/**
 * Strictly decodes bytes as UTF-8, throwing on invalid sequences.
 * Unlike standard UTF-8 decoding, this rejects malformed input and unmappable characters.
 *
 * @param bytes Input byte array to decode
 * @return Decoded UTF-8 string
 * @throws TurnkeyEncodingError.InvalidUTF8 if bytes contain invalid UTF-8 sequences
 */
@Throws(TurnkeyEncodingError::class)
fun decodeUtf8Strict(bytes: ByteArray): String = try {
    val decoder = java.nio.charset.StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
        .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT)
    decoder.decode(java.nio.ByteBuffer.wrap(bytes)).toString()
} catch (e: java.nio.charset.CharacterCodingException) {
    throw TurnkeyEncodingError.InvalidUTF8(e)
} catch (e: Exception) {
    throw TurnkeyEncodingError.wrap(e)
}