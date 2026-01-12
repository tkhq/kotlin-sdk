package com.turnkey.stamper

import android.app.Activity
import android.content.Context
import com.turnkey.crypto.generateP256KeyPair
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

        fun configure(context: Context, rpId: String? = null) {
            contextProvider = { context.applicationContext }
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
                ?: throw IllegalStateException("Stamper not initialized. Call Stamper.configure(context)")
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
        fun fromPasskey(activity: Activity, rpId: String? = null, allowedCredentials: List<ByteArray>? = null): Stamper {
            val resolvedRpId = rpId ?: defaultRpId
            ?: throw IllegalArgumentException(
                "rpId is required. Either pass it explicitly or set a default with Stamper.configure(context, rpId)"
            )

            val passkeyStamper = PasskeyStamper(activity, allowedCredentials, resolvedRpId)

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

        /**
         * Creates a new P-256 key pair and stores it securely.
         *
         * This is a static utility for creating key pairs without a Stamper instance.
         *
         * @return the public key in compressed hexadecimal format
         * @throws IllegalStateException if Stamper.configure() was not called
         */
        fun createOnDeviceKeyPair(context: Context? = null): String {
            val context = context ?: contextProvider?.invoke()
                ?: throw IllegalStateException("No app context found. Pass in app context or call Stamper.configure(context)")
            val (_, pubKeyCompressed, privKey) = generateP256KeyPair()
            KeyPairStore.save(context, privKey, pubKeyCompressed)

            return pubKeyCompressed
        }

        /**
         * Deletes a key pair from secure storage by public key.
         *
         * This is a static utility for deleting key pairs without a Stamper instance.
         *
         * @param publicKey the public key identifying the key pair to delete
         * @throws IllegalStateException if Stamper.configure() was not called
         */
        fun deleteOnDeviceKeyPair(context: Context? = null, publicKey: String) {
            val context = context ?: contextProvider?.invoke()
                ?: throw IllegalStateException("No app context found. Pass in app context or call Stamper.configure(context)")
            KeyPairStore.delete(context, publicHex = publicKey)
        }
    }

    /**
     * Deletes a key pair from secure storage.
     *
     * If publicKey is null, deletes this Stamper's key pair (from apiPublicKey).
     *
     * @param publicKey optional public key; if null, uses this instance's apiPublicKey
     * @throws IllegalStateException if publicKey is null and this Stamper has no apiPublicKey
     */
    fun deleteOnDeviceKeyPair(context: Context? = null, publicKey: String? = null) {
        val context = context ?: contextProvider?.invoke()
            ?: throw IllegalStateException("No app context found. Pass in app context or call Stamper.configure(context)")
        val pk = publicKey ?: apiPublicKey 
            ?: throw IllegalStateException("No key pairs found to delete")
        KeyPairStore.delete(context, publicHex = pk)
    }

    /**
     * Generates a signed stamp for the given payload.
     *
     * @param payload body of the request to be signed
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
    fun sign(
        context: Context? = null,
        payload: String,
        format: SignatureFormat = SignatureFormat.der,
        publicKey: String? = null
    ): String {
        try {
            val resolvedPrivateKey = resolvePrivateKey(context = context, publicKey = publicKey)

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

    private fun resolvePrivateKey(context: Context? = null, publicKey: String? = null): String {
        val context = context ?: contextProvider?.invoke()
            ?: throw IllegalStateException("No app context found. Pass in app context or call Stamper.configure(context)")
        return (if (publicKey !== null) KeyPairStore.getPrivateHex(
            context,
            publicKey
        ) else apiPrivateKey) ?: throw IllegalStateException("No private key found to sign with")
    }
}