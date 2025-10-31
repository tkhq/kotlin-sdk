package com.turnkey.internal

import android.app.Activity
import androidx.browser.customtabs.CustomTabsIntent
import com.turnkey.encoding.decodeBase64Url
import com.turnkey.http.TurnkeyClient
import com.turnkey.models.CreateSubOrgApiKey
import com.turnkey.models.CreateSubOrgAuthenticator
import com.turnkey.models.CreateSubOrgParams
import com.turnkey.models.OAuthOverrideParams
import com.turnkey.models.OtpOverrireParams
import com.turnkey.models.OtpType
import com.turnkey.models.OverrideParams
import com.turnkey.models.PasskeyOverrideParams
import com.turnkey.models.StorageError
import com.turnkey.models.TurnkeyConfig
import com.turnkey.models.Wallet
import com.turnkey.types.ProxyTSignupBody
import com.turnkey.types.TGetWalletAccountsBody
import com.turnkey.types.V1ApiKeyCurve
import com.turnkey.types.V1ApiKeyParamsV2
import com.turnkey.types.V1AuthenticatorParamsV2
import com.turnkey.types.V1OauthProviderParams
import com.turnkey.types.V1Pagination
import com.turnkey.types.V1Wallet
import com.turnkey.types.V1WalletAccount
import com.turnkey.types.V1WalletParams
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import androidx.core.net.toUri
import com.turnkey.encoding.toBase64Url
import com.turnkey.models.ChallengePair
import com.turnkey.models.Defaults
import com.turnkey.types.V1AddressFormat
import com.turnkey.types.V1HashFunction
import com.turnkey.types.V1PayloadEncoding
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

object JwtDecoder {
    /**
     * Decode a JWT's payload (2nd segment) as type [T].
     *
     * - Validates the 3-part structure.
     * - Base64URL-decodes the payload.
     * - Parses JSON into [T] (unknown fields are ignored by default).
     *
     * @throws StorageError.InvalidJWT if structure or base64 is invalid
     * @throws StorageError.DecodingFailed if JSON parsing fails
     */
    @Throws(StorageError::class)
    inline fun <reified T> decode(
        jwt: String,
        json: Json = Json { ignoreUnknownKeys = true }
    ): T {
        val parts = jwt.split('.')
        if (parts.size != 3) throw StorageError.InvalidJWT

        val payloadB64Url = parts[1]
        val bytes = try {
            decodeBase64Url(payloadB64Url)
        } catch (_: Throwable) {
            throw StorageError.InvalidJWT
        }

        return try {
            json.decodeFromString<T>(bytes.toString(Charsets.UTF_8))
        } catch (e: Throwable) {
            throw StorageError.DecodingFailed(e)
        }
    }
}

object Helpers {
    suspend fun fetchAllWalletAccountsWithCursor(client: TurnkeyClient, organizationId: String): MutableList<V1WalletAccount> =
        coroutineScope {
            var hasMore: Boolean = true
            var cursor: String? = null
            val limit: Int = 100
            val accounts: MutableList<V1WalletAccount> = mutableListOf()

            while (hasMore) {
                val walletAccountsDeferred = async {
                    client.getWalletAccounts(TGetWalletAccountsBody(
                        organizationId,
                        includeWalletDetails = true,
                        paginationOptions = V1Pagination(
                            limit = limit.toString(),
                            after = cursor
                        )
                    ))
                }
                val walletAccounts = walletAccountsDeferred.await()
                accounts.addAll(walletAccounts.accounts)
                hasMore = walletAccounts.accounts.size == limit

                cursor = if (walletAccounts.accounts.isNotEmpty()) {
                    walletAccounts.accounts.last().walletId
                } else {
                    null
                }
            }
            return@coroutineScope accounts
        }

    fun mapAccountsToWallet(
        accounts: List<V1WalletAccount>,
        wallets: List<V1Wallet>
    ): List<Wallet> {
        val accountsByWallet = mutableMapOf<String, MutableList<V1WalletAccount>>()
        val nameByWallet = mutableMapOf<String, String>()

        // Seed known wallets (no accounts yet)
        for (w in wallets) {
            nameByWallet.putIfAbsent(w.walletId, w.walletName)
            accountsByWallet.putIfAbsent(w.walletId, mutableListOf())
        }

        for (a in accounts) {
            val id = a.walletId
            val name = a.walletDetails?.walletName ?: id
            nameByWallet.putIfAbsent(id, name)
            accountsByWallet.getOrPut(id) { mutableListOf() }.add(a)
        }

        // Build immutable Wallets
        return accountsByWallet.map { (id, accs) ->
            Wallet(
                id = id,
                name = nameByWallet[id] ?: id,
                accounts = accs.toList()
            )
        }
    }

    fun buildSignUpBody(createSubOrgParams: CreateSubOrgParams): ProxyTSignupBody {
        val now = System.currentTimeMillis()

        val authenticators: List<V1AuthenticatorParamsV2> =
            createSubOrgParams.authenticators
                ?.takeIf { it.isNotEmpty() }
                ?.map { a ->
                    V1AuthenticatorParamsV2(
                        authenticatorName = a.authenticatorName ?: "A Passkey",
                        challenge = a.challenge,
                        attestation = a.attestation
                    )
                }
                ?: emptyList()

        val apiKeys: List<V1ApiKeyParamsV2> =
            createSubOrgParams.apiKeys
                ?.filter { it.curveType != null }
                ?.map { k ->
                    V1ApiKeyParamsV2(
                        apiKeyName = k.apiKeyName ?: "api-key-$now",
                        publicKey = k.publicKey,
                        expirationSeconds = k.expirationSeconds ?: "900",
                        curveType = k.curveType!!,
                    )
                }
                ?: emptyList()

        val userName = createSubOrgParams.userName
            ?: createSubOrgParams.userEmail
            ?: "user-$now"

        val subOrgName = createSubOrgParams.subOrgName ?: "sub-org-$now"

        val customWallet: V1WalletParams? = createSubOrgParams.customWallet?.let {
            V1WalletParams(walletName = it.walletName, accounts = it.walletAccounts)
        }

        return ProxyTSignupBody(
            userName = userName,
            organizationName = subOrgName,
            userEmail = createSubOrgParams.userEmail,
            userTag = createSubOrgParams.userTag,
            authenticators = authenticators,
            userPhoneNumber = createSubOrgParams.userPhoneNumber,
            verificationToken = createSubOrgParams.verificationToken,
            apiKeys = apiKeys,
            wallet = customWallet,
            oauthProviders = createSubOrgParams.oauthProviders ?: emptyList(),
        )
    }

    fun getCreateSubOrgParams(
        createSubOrgParams: CreateSubOrgParams?, // caller-supplied overrides (highest precedence)
        config: TurnkeyConfig,                   // contains master config defaults
        overrideParams: OverrideParams           // determines which branch to apply
    ): CreateSubOrgParams {

        val cfgCreate = config.authConfig?.createSubOrgParams

        return when (overrideParams) {
            is OtpOverrireParams -> {
                if (overrideParams.otpType == OtpType.OTP_TYPE_EMAIL) {
                    val base = createSubOrgParams
                        ?: cfgCreate?.emailOtpAuth
                        ?: CreateSubOrgParams()
                    base.copy(
                        userEmail = overrideParams.contact,
                        verificationToken = overrideParams.verificationToken,
                    )
                } else {
                    val base = createSubOrgParams
                        ?: cfgCreate?.smsOtpAuth
                        ?: CreateSubOrgParams()
                    base.copy(
                        userPhoneNumber = overrideParams.contact,
                        verificationToken = overrideParams.verificationToken,
                    )
                }
            }

            is OAuthOverrideParams -> {
                val base = createSubOrgParams
                    ?: cfgCreate?.oAuth
                    ?: CreateSubOrgParams()
                base.copy(
                    oauthProviders = listOf(
                        V1OauthProviderParams(
                            providerName = overrideParams.providerName,
                            oidcToken = overrideParams.oidcToken,
                        )
                    )
                )
            }

            is PasskeyOverrideParams -> {
                val base = createSubOrgParams
                    ?: cfgCreate?.passkeyAuth
                    ?: CreateSubOrgParams()

                base.copy(
                    authenticators = listOf(
                        CreateSubOrgAuthenticator(
                            authenticatorName = overrideParams.passkeyName,
                            challenge = overrideParams.encodedChallenge,
                            attestation = overrideParams.attestation,
                        )
                    ),
                    apiKeys = listOf(
                        CreateSubOrgApiKey(
                            apiKeyName = "passkey-auth-${overrideParams.temporaryPublicKey}",
                            publicKey = requireNotNull(overrideParams.temporaryPublicKey) {
                                "temporaryPublicKey is required for PasskeyOverridedParams"
                            },
                            curveType = V1ApiKeyCurve.API_KEY_CURVE_P256,
                            // short expiration since it's a temporary key
                            expirationSeconds = "15",
                        )
                    )
                )
            }
        }
    }

    fun openCustomTab(activity: Activity, url: String) {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        intent.launchUrl(activity, url.toUri())
    }

    fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Generate PKCE (S256) verifier + code_challenge pair. */
    fun generateChallengePair(lengthBytes: Int = 32): ChallengePair {
        val verifier = randomVerifier(lengthBytes)
        val digest = sha256(verifier.toByteArray(Charsets.US_ASCII))
        val codeChallenge = digest.toBase64Url()
        return ChallengePair(verifier = verifier, codeChallenge = codeChallenge)
    }

    /** 32 random bytes → 43-char base64url string (within PKCE 43–128 char limits). */
    fun randomVerifier(lengthBytes: Int = 32): String {
        val bytes = ByteArray(lengthBytes)
        SECURE_RANDOM.nextBytes(bytes)
        return bytes.toBase64Url()
    }

    private fun sha256(data: ByteArray): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(data)

    private val SECURE_RANDOM: SecureRandom by lazy { SecureRandom() }

    fun defaultsFor(addressFormat: V1AddressFormat): Defaults = when (addressFormat) {

        // Un/Compressed pubkeys
        V1AddressFormat.ADDRESS_FORMAT_UNCOMPRESSED,
        V1AddressFormat.ADDRESS_FORMAT_COMPRESSED ->
            Defaults(
                encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
                hashFunction = V1HashFunction.HASH_FUNCTION_SHA256
            )

        // Ethereum
        V1AddressFormat.ADDRESS_FORMAT_ETHEREUM ->
            Defaults(
                encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
                hashFunction = V1HashFunction.HASH_FUNCTION_KECCAK256
            )

        // Hex encoding, hash not applicable
        V1AddressFormat.ADDRESS_FORMAT_SOLANA,
        V1AddressFormat.ADDRESS_FORMAT_SUI,
        V1AddressFormat.ADDRESS_FORMAT_APTOS,
        V1AddressFormat.ADDRESS_FORMAT_TON_V3R2,
        V1AddressFormat.ADDRESS_FORMAT_TON_V4R2,
        V1AddressFormat.ADDRESS_FORMAT_TON_V5R1,
        V1AddressFormat.ADDRESS_FORMAT_XLM ->
            Defaults(
                encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
                hashFunction = V1HashFunction.HASH_FUNCTION_NOT_APPLICABLE
            )

        // Cosmos-family (incl. Sei): text + sha256
        V1AddressFormat.ADDRESS_FORMAT_COSMOS,
        V1AddressFormat.ADDRESS_FORMAT_SEI ->
            Defaults(
                encoding = V1PayloadEncoding.PAYLOAD_ENCODING_TEXT_UTF8,
                hashFunction = V1HashFunction.HASH_FUNCTION_SHA256
            )

        // Tron, Bitcoin variants, Doge, XRP: hex + sha256
        V1AddressFormat.ADDRESS_FORMAT_TRON,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_MAINNET_P2PKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_MAINNET_P2SH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_MAINNET_P2WPKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_MAINNET_P2WSH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_MAINNET_P2TR,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_TESTNET_P2PKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_TESTNET_P2SH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_TESTNET_P2WPKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_TESTNET_P2WSH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_TESTNET_P2TR,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_SIGNET_P2PKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_SIGNET_P2SH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_SIGNET_P2WPKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_SIGNET_P2WSH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_SIGNET_P2TR,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_REGTEST_P2PKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_REGTEST_P2SH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_REGTEST_P2WPKH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_REGTEST_P2WSH,
        V1AddressFormat.ADDRESS_FORMAT_BITCOIN_REGTEST_P2TR,
        V1AddressFormat.ADDRESS_FORMAT_DOGE_MAINNET,
        V1AddressFormat.ADDRESS_FORMAT_DOGE_TESTNET,
        V1AddressFormat.ADDRESS_FORMAT_XRP ->
            Defaults(
                encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
                hashFunction = V1HashFunction.HASH_FUNCTION_SHA256
            )
    }

    /** "\u0019Ethereum Signed Message:\n" + decimal length + message bytes */
    fun ethereumPrefixed(message: ByteArray): ByteArray {
        val prefix = "\u0019Ethereum Signed Message:\n${message.size}"
        val prefixBytes = prefix.toByteArray(StandardCharsets.UTF_8)
        // concat prefixBytes + message
        return ByteArray(prefixBytes.size + message.size).also {
            System.arraycopy(prefixBytes, 0, it, 0, prefixBytes.size)
            System.arraycopy(message, 0, it, prefixBytes.size, message.size)
        }
    }

    /** Encode raw bytes either as 0x-prefixed lowercase hex, or as UTF-8 text. */
    fun encodeMessageBytes(bytes: ByteArray, encoding: V1PayloadEncoding): String {
        return if (encoding == V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL) {
            "0x" + bytes.joinToString("") { b -> "%02x".format(Locale.ROOT, b) }
        } else {
            String(bytes, StandardCharsets.UTF_8)
        }
    }
}