package com.turnkey.stamper

import com.turnkey.stamper.internal.ApiKeyStamper
import com.turnkey.stamper.internal.PasskeyStampBuilder
import com.turnkey.passkey.PasskeyStamper
import com.turnkey.stamper.utils.SignatureFormat
import com.turnkey.stamper.utils.TurnkeyStamperError
import java.security.MessageDigest

class Stamper private constructor(
    private val apiPublicKey: String?,
    private val apiPrivateKey: String?,
    private val passkeyManager: PasskeyStamper?
) {
    /** Empty: not configured for any mode. */
    constructor() : this(null, null, null)

    /** API-key mode. */
    constructor(apiPublicKey: String, apiPrivateKey: String) : this(
        apiPublicKey = apiPublicKey,
        apiPrivateKey = apiPrivateKey,
        passkeyManager = null
    )

    /** Passkey mode */
    constructor(passkeyManager: PasskeyStamper) : this(
        apiPublicKey = null,
        apiPrivateKey = null,
        passkeyManager = passkeyManager
    )

    /**
     * Generates a signed stamp for the given payload.
     *
     * @return Pair(headerName, headerValue)
     * @throws TurnkeyStamperError if not configured for any mode
     */
    suspend fun stamp(payload: String): Pair<String, String> {
        try {
            val payloadBytes = payload.toByteArray(Charsets.UTF_8)
            val digest = MessageDigest.getInstance("SHA-256").digest(payloadBytes)

            return when {
                apiPublicKey != null && apiPrivateKey != null -> {
                    val value = ApiKeyStamper.stamp(
                        payloadSha256 = digest,
                        publicKeyHex = apiPublicKey,
                        privateKeyHex = apiPrivateKey
                    )
                    "X-Stamp" to value
                }

                passkeyManager != null -> {
                    val value = PasskeyStampBuilder.stamp(
                        payloadSha256 = digest,
                        passkeyClient = passkeyManager
                    )
                    "X-Stamp-Webauthn" to value
                }

                else -> throw IllegalStateException("Unable to stamp request: no credentials configured")
            }
        } catch (t: Throwable) {
            throw TurnkeyStamperError.wrap(t)
        }
    }

    fun sign(payload: String, format: SignatureFormat = SignatureFormat.der): String {
        try {
            val payloadBytes = payload.toByteArray(Charsets.UTF_8)
            val digest = MessageDigest.getInstance("SHA-256").digest(payloadBytes)

            return if (apiPrivateKey != null) ApiKeyStamper.sign(
                digest,
                apiPrivateKey,
                format
            ) else throw IllegalStateException("No private key found to sign with")
        } catch (t: Throwable) {
            throw TurnkeyStamperError.wrap(t)
        }
    }
}