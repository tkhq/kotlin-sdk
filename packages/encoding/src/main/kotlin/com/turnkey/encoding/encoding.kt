package com.turnkey.encoding

import com.turnkey.encoding.utils.TurnkeyDecodingException
import java.security.SecureRandom
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
} catch (_: IllegalArgumentException) {
    null
}

/** Decode even-length hex string to bytes; throws TurnkeyDecodingException on error. */
@Throws(TurnkeyDecodingException::class)
fun decodeHex(hex: String): ByteArray {
    if (hex.length % 2 != 0) throw TurnkeyDecodingException.OddLengthString
    val out = ByteArray(hex.length / 2)
    var i = 0
    var j = 0
    while (i < hex.length) {
        val hi = hex[i++].hexNibbleOrThrow(i - 1)
        val lo = hex[i++].hexNibbleOrThrow(i - 1)
        out[j++] = ((hi shl 4) or lo).toByte()
    }
    return out
}

private fun Char.hexNibbleOrThrow(index: Int): Int {
    val d = Character.digit(this, 16)
    if (d == -1) throw TurnkeyDecodingException.InvalidHexCharacter(this, index)
    return d
}

/* ---------- Base64 URL (RFC 4648, no padding) ---------- */

/** Base64url (URL-safe) string without padding */
fun ByteArray.toBase64Url(): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString(this)

/** Decode a base64url string (with or without padding); returns null on error. */
fun String.base64UrlToBytesOrNull(): ByteArray? = try {
    decodeBase64Url(this)
} catch (_: IllegalArgumentException) {
    null
}

/** Decode a base64url string (with or without padding); throws IllegalArgumentException on invalid input. */
@Throws(IllegalArgumentException::class)
fun decodeBase64Url(b64url: String): ByteArray {
    // java.util.Base64 URL decoder expects proper padding; add it if missing
    val padNeeded = (4 - (b64url.length % 4)) % 4
    val padded = if (padNeeded == 0) b64url else b64url + "=".repeat(padNeeded)
    return Base64.getUrlDecoder().decode(padded)
}

/* ---------- Secure random ---------- */

private val SECURE_RANDOM = SecureRandom()

/** Cryptographically secure random byte array. */
fun randomBytes(count: Int): ByteArray = ByteArray(count).also { SECURE_RANDOM.nextBytes(it) }