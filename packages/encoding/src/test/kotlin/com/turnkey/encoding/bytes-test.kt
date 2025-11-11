package com.turnkey.encoding

import kotlin.test.*
import com.turnkey.encoding.utils.TurnkeyDecodingException

class BytesTest {

    /* ---------- Hex ---------- */

    @Test
    fun hex_roundTrip_and_lowercase() {
        val original = byteArrayOf(
            0x00, 0x0F, 0x10, 0x2A, 0x7F, 0x80.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xFF.toByte()
        )
        val hex = original.toHexString()
        assertEquals("000f102a7f80abcdff", hex)  // lowercase + zero-padded
        val decoded = decodeHex(hex)
        assertContentEquals(original, decoded)
    }

    @Test
    fun decodeHex_is_caseInsensitive() {
        assertContentEquals(
            byteArrayOf(0xAB.toByte(), 0xCD.toByte()),
            decodeHex("AbCd")
        )
    }

    @Test
    fun decodeHex_oddLength_throws() {
        val ex = assertFailsWith<TurnkeyDecodingException> { decodeHex("abc") }
        assertTrue(ex is TurnkeyDecodingException.OddLengthString)
    }

    @Test
    fun decodeHex_invalidChar_throws_with_char_and_index() {
        val ex = assertFailsWith<TurnkeyDecodingException> { decodeHex("0x") }
        when (ex) {
            is TurnkeyDecodingException.InvalidHexCharacter -> {
                assertEquals('x', ex.char)
                assertEquals(1, ex.index)
            }
            else -> fail("Expected InvalidHexCharacter, got $ex")
        }
    }

    @Test
    fun hexToBytesOrNull_null_on_invalid_or_odd() {
        assertNull("zz".hexToBytesOrNull())
        assertNull("abc".hexToBytesOrNull())
    }

    @Test
    fun hexToBytesOrNull_valid_returns_bytes() {
        assertContentEquals(
            byteArrayOf(0xAB.toByte(), 0xCD.toByte()),
            "abcd".hexToBytesOrNull()
        )
    }

    /* ---------- Base64 URL-safe (RFC 4648, no padding) ---------- */

    @Test
    fun base64Url_encode_noPadding_knownVector() {
        val bytes = byteArrayOf(0xFB.toByte(), 0xFF.toByte())
        // std base64 "+/8=" → url-safe "-_8" (no padding)
        assertEquals("-_8", bytes.toBase64Url())
    }

    @Test
    fun base64Url_decode_accepts_padded_and_unpadded() {
        val expected = byteArrayOf(0xFB.toByte(), 0xFF.toByte())
        assertContentEquals(expected, decodeBase64Url("-_8"))
        assertContentEquals(expected, decodeBase64Url("-_8=")) // extra padding ok

        val nullable = "-_8".base64UrlToBytesOrNull()
        assertNotNull(nullable)
        assertContentEquals(expected, nullable)
    }

    @Test
    fun base64Url_roundTrip_arbitrary() {
        val original = "hello world".encodeToByteArray()
        val enc = original.toBase64Url()
        val dec = decodeBase64Url(enc)
        assertContentEquals(original, dec)
    }

    @Test
    fun base64Url_invalid_returns_null_or_throws() {
        assertNull("!!".base64UrlToBytesOrNull())
        assertFailsWith<IllegalArgumentException> { decodeBase64Url("!!") }
    }

    /* ---------- Random bytes ---------- */

    @Test
    fun randomBytes_length_and_nonconstant() {
        val a = randomBytes(32)
        val b = randomBytes(32)
        assertEquals(32, a.size)
        assertEquals(32, b.size)
        assertFalse(a.all { it == 0.toByte() })      // highly unlikely to be all zeros
        assertFalse(a.contentEquals(b))              // astronomically unlikely to be identical
    }
}
