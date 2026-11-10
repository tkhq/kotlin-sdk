package com.turnkey.stamper

/**
 * Signs Turnkey API requests.
 *
 * [TurnkeyClient][com.turnkey.http.TurnkeyClient] calls [stamp] with the exact
 * request body before it sends each signed request, and attaches the returned
 * header. [Stamper] implements this for API keys and passkeys.
 *
 * Implement this interface to sign with a key that the SDK does not hold, for
 * example a key in the Android Keystore (StrongBox or TEE) that needs a
 * biometric prompt for each signature. Use [ApiKeyStamp] to build the header
 * value from a P-256 signature:
 *
 * ```kotlin
 * val stamper = RequestStamper { payload ->
 *     val signature = Signature.getInstance("SHA256withECDSA").run {
 *         initSign(keystorePrivateKey)
 *         update(payload.toByteArray())
 *         sign() // DER-encoded
 *     }
 *     ApiKeyStamp.header(publicKeyHex, signature)
 * }
 * ```
 */
fun interface RequestStamper {
    /**
     * Signs [payload] and returns the header to attach to the request, as
     * `(name, value)`. For example `"X-Stamp" to "<base64url stamp>"`.
     *
     * @param payload the request body, exactly as it is sent
     */
    suspend fun stamp(payload: String): Pair<String, String>
}
