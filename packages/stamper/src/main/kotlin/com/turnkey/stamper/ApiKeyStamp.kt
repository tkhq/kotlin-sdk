package com.turnkey.stamper

import com.turnkey.encoding.toBase64Url
import com.turnkey.encoding.toHexString

/**
 * Builds the `X-Stamp` header for a request signed with a P-256 API key.
 *
 * Use this from a [RequestStamper] that signs outside the SDK. The signature
 * must be ECDSA P-256 over the SHA-256 digest of the request body, DER-encoded.
 * `Signature.getInstance("SHA256withECDSA")` produces this format.
 */
object ApiKeyStamp {
    /** The header name for API key stamps. */
    const val HEADER_NAME = "X-Stamp"

    /** The signature scheme for P-256 API keys. */
    const val SCHEME = "SIGNATURE_SCHEME_TK_API_P256"

    /**
     * Returns the header value: the stamp JSON, base64url-encoded without padding.
     *
     * @param publicKeyHex the compressed SEC1 public key, in hex
     * @param signatureDerHex the DER-encoded ECDSA signature, in hex
     */
    fun value(
        publicKeyHex: String,
        signatureDerHex: String,
    ): String {
        require(COMPRESSED_P256.matches(publicKeyHex)) { "publicKeyHex must be a compressed P-256 key: 66 hex characters starting with 02 or 03" }
        require(HEX.matches(signatureDerHex)) { "signatureDerHex must be hex" }
        val json =
            """{"publicKey":"${publicKeyHex.lowercase()}","scheme":"$SCHEME","signature":"${signatureDerHex.lowercase()}"}"""
        return json.toByteArray(Charsets.UTF_8).toBase64Url()
    }

    /**
     * Returns the header as `(name, value)`, ready to return from [RequestStamper.stamp].
     *
     * @param publicKeyHex the compressed SEC1 public key, in hex
     * @param signatureDer the DER-encoded ECDSA signature
     */
    fun header(
        publicKeyHex: String,
        signatureDer: ByteArray,
    ): Pair<String, String> = HEADER_NAME to value(publicKeyHex, signatureDer.toHexString())

    private val HEX = Regex("^[0-9a-fA-F]+$")

    private val COMPRESSED_P256 = Regex("^0[23][0-9a-fA-F]{64}$")
}
