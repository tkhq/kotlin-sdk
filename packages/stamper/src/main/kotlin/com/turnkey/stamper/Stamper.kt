package com.turnkey.stamper

import android.app.Activity
import android.content.Context
import com.turnkey.stamper.internal.ApiKeyStamper
import com.turnkey.stamper.internal.PasskeyStampBuilder
import com.turnkey.passkey.PasskeyStamper
import com.turnkey.stamper.internal.storage.KeyPairStore
import com.turnkey.stamper.utils.SignatureFormat
import com.turnkey.stamper.utils.TurnkeyStamperError
import java.security.MessageDigest

class Stamper private constructor(
    private val apiPublicKey: String?,
    private val apiPrivateKey: String?,
    private val passkeyManager: PasskeyStamper?
) {
    companion object {
        private var contextProvider: (() -> Context)? = null
        private var defaultRpId: String? = null

        fun init(context: Context, rpId: String? = null) {
            contextProvider = { context.applicationContext}
            defaultRpId = rpId
        }

        /**
         * Creates a Stamper from a stored P-256 key pair.
         *
         * @param publicKey the public key (compressed hex) of the stored key pair
         * @return Stamper instance that signs using the stored private key
         * @throws IllegalStateException if Stamper.init() was not called
         */
        fun fromPublicKey(publicKey: String): Stamper {
            val context = contextProvider?.invoke()
                ?: throw IllegalStateException("Stamper not initialized. Call Stamper.init(context)")
            val privateKey = KeyPairStore.getPrivateHex(context, publicKey)
            return Stamper(publicKey, privateKey)
        }

        /**
         * Creates a Stamper that uses a passkey for signing.
         *
         * The user will be prompted to authenticate with biometrics when signing.
         *
         * @param activity current Android activity for displaying the passkey UI
         * @param rpId optional relying party ID (uses configured default if null)
         * @return Stamper instance that signs using the passkey
         * @throws IllegalArgumentException if rpId is null and no default was configured
         */
        fun fromPasskey(activity: Activity, rpId: String? = null): Stamper {
            val resolvedRpId = rpId ?: defaultRpId
            ?: throw IllegalArgumentException(
                "rpId is required. Either pass it explicitly or set a default with Stamper.init(context, rpId)"
            )

            val passkeyStamper = PasskeyStamper(activity, resolvedRpId)

            return Stamper(
                apiPublicKey = null,
                apiPrivateKey = null,
                passkeyManager = passkeyStamper
            )
        }

        /**
         * Creates a Stamper with explicit key material.
         *
         * Useful for testing or when managing keys manually.
         */
        operator fun invoke(apiPublicKey: String, apiPrivateKey: String): Stamper {
            return Stamper(apiPublicKey, apiPrivateKey, null)
        }

        /**
         * Creates a Stamper with an existing PasskeyStamper.
         *
         * For advanced use cases where you need full control over passkey configuration.
         */
        operator fun invoke(passkeyStamper: PasskeyStamper): Stamper {
            return Stamper(null, null, passkeyStamper)
        }
    }

    /**
     * Generates a signed stamp for the given payload.
     *
     * @param payload body of the request to be signed
     * @param publicKey optional public key to use for stamping; if provided, the associated
     *                  private key will be loaded from storage. If null, uses the default
     *                  key pair or passkey stamper.
     * @return Pair(headerName, headerValue)
     * @throws TurnkeyStamperError if not configured for any mode
     */
    suspend fun stamp(payload: String, publicKey: String? = null): Pair<String, String> {
        try {
            val resolvedPublicKey = publicKey ?: apiPublicKey
            val resolvedPrivateKey = resolvePrivateKey(publicKey)

            val payloadBytes = payload.toByteArray(Charsets.UTF_8)
            val digest = MessageDigest.getInstance("SHA-256").digest(payloadBytes)

            return when {
                resolvedPublicKey != null -> {
                    val value = ApiKeyStamper.stamp(
                        payloadSha256 = digest,
                        publicKeyHex = resolvedPublicKey,
                        privateKeyHex = resolvedPrivateKey
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

    /**
     * Signs a payload using either the default key pair or a specified public key.
     *
     * @param payload the data to sign
     * @param format the signature format (raw, hex, etc.)
     * @param publicKey optional public key to use for signing; if provided, the associated
     *                  private key will be loaded from storage. If null, uses the default
     *                  key pair or passkey stamper.
     * @return the signature string
     * @throws IllegalArgumentException if the specified public key doesn't exist in storage
     * @throws IllegalStateException if the stamper is not properly initialized
     */
    fun sign(payload: String, format: SignatureFormat = SignatureFormat.der, publicKey: String? = null): String {
        try {
            val resolvedPrivateKey = resolvePrivateKey(publicKey)

            val payloadBytes = payload.toByteArray(Charsets.UTF_8)
            val digest = MessageDigest.getInstance("SHA-256").digest(payloadBytes)

            return ApiKeyStamper.sign(
                digest,
                resolvedPrivateKey,
                format
            )
        } catch (t: Throwable) {
            throw TurnkeyStamperError.wrap(t)
        }
    }

    private fun resolvePrivateKey(publicKey: String? = null): String {
        val context = contextProvider?.invoke()
            ?: throw IllegalStateException("Stamper not initialized. Call Stamper.init(context) first")
        return (if (publicKey !== null) KeyPairStore.getPrivateHex(context, publicKey) else apiPrivateKey) ?: throw IllegalStateException("No private key found to sign with")
    }
}