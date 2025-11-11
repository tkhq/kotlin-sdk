package com.turnkey.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.turnkey.crypto.decryptExportBundle
import com.turnkey.http.TurnkeyClient
import com.turnkey.internal.Helpers
import com.turnkey.internal.JwtDecoder
import com.turnkey.internal.storage.keys.KeyPairStore
import com.turnkey.internal.storage.keys.PendingKeysStore
import com.turnkey.internal.storage.sessions.AutoRefreshStore
import com.turnkey.internal.storage.sessions.JwtSessionStore
import com.turnkey.internal.storage.sessions.SelectedSessionStore
import com.turnkey.internal.storage.sessions.SessionRegistryStore
import com.turnkey.models.AuthState
import com.turnkey.models.Storage
import com.turnkey.models.StorageError
import com.turnkey.models.TurnkeyConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import com.turnkey.models.*
import com.turnkey.crypto.generateP256KeyPair
import com.turnkey.encoding.hexToBytesOrNull
import com.turnkey.internal.encryptWalletToBundle
import com.turnkey.passkey.PasskeyStamper
import com.turnkey.passkey.PasskeyUser
import com.turnkey.passkey.createPasskey
import com.turnkey.stamper.Stamper
import com.turnkey.types.ProxyTGetAccountBody
import com.turnkey.types.ProxyTGetWalletKitConfigBody
import com.turnkey.types.ProxyTGetWalletKitConfigResponse
import com.turnkey.types.ProxyTInitOtpBody
import com.turnkey.types.ProxyTOAuth2AuthenticateBody
import com.turnkey.types.ProxyTOAuthLoginBody
import com.turnkey.types.ProxyTOtpLoginBody
import com.turnkey.types.ProxyTVerifyOtpBody
import com.turnkey.types.TCreateWalletBody
import com.turnkey.types.TExportWalletBody
import com.turnkey.types.TGetUserBody
import com.turnkey.types.TGetWalletAccountsBody
import com.turnkey.types.TGetWalletsBody
import com.turnkey.types.TImportWalletBody
import com.turnkey.types.TInitImportWalletBody
import com.turnkey.types.TSignRawPayloadBody
import com.turnkey.types.TStampLoginBody
import com.turnkey.types.V1AddressFormat
import com.turnkey.types.V1CreateWalletResult
import com.turnkey.types.V1ExportWalletResult
import com.turnkey.types.V1HashFunction
import com.turnkey.types.V1ImportWalletResult
import com.turnkey.types.V1Oauth2Provider
import com.turnkey.types.V1PayloadEncoding
import com.turnkey.types.V1SignRawPayloadResult
import com.turnkey.types.V1SignRawPayloadsResult
import com.turnkey.types.V1User
import com.turnkey.types.V1WalletAccountParams
import com.turnkey.utils.KeyFormat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okio.Utf8
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.HexFormat
import java.util.UUID

object TurnkeyContext {
    @Volatile
    private var initialized = false
    private val initMutex = Mutex()
    lateinit var appContext: Context
    private lateinit var config: TurnkeyConfig
    private lateinit var masterConfig: TurnkeyConfig

    private val io = Dispatchers.IO
    private val bg = Dispatchers.Default

    private val _authState = MutableStateFlow(AuthState.loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @Volatile
    private var _client: TurnkeyClient? = null
    private val clientReady = CompletableDeferred<Unit>()
    val client: TurnkeyClient
        get() = _client ?: error("Turnkey not initialized. Call Turnkey.init(...) first.")

    private val _selectedSessionKey = MutableStateFlow<String?>(null)
    val selectedSessionKey: StateFlow<String?> = _selectedSessionKey.asStateFlow()

    private val _session = MutableStateFlow<Session?>(null)
    val session: StateFlow<Session?> = _session.asStateFlow()
    private val _user = MutableStateFlow<V1User?>(null)
    val user: StateFlow<V1User?> = _user.asStateFlow()

    private val _wallets = MutableStateFlow<List<Wallet>?>(null)
    val wallets: StateFlow<List<Wallet>?> = _wallets.asStateFlow()

    //    Internal state
    private val expiryJobs = ConcurrentHashMap<String, Job>()
    private val timerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val http = OkHttpClient()

    fun registerForegroundObserver(onEnterForeground: () -> Unit) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                onEnterForeground()
            }
        })
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    fun init(app: Application, config: TurnkeyConfig) {
        scope.launch {
            initSuspend(app, config)
        }
    }

    suspend fun awaitReady() {
        clientReady.await()
    }

    private suspend fun awaitClient(): TurnkeyClient {
        _client?.let { return it }
        clientReady.await()
        return checkNotNull(_client) { "Client not available after initialization." }
    }

    suspend fun initSuspend(app: Application, cfg: TurnkeyConfig) {
        initMutex.withLock {
            if (initialized) return
            appContext = app.applicationContext
            config = cfg
            initialized = true
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                scope.launch(io) {
                    PendingKeysStore.purge(appContext)
                    SessionRegistryStore.purgeExpiredSessions(appContext)
                }
            }
        })

        withContext(io) {
            SessionRegistryStore.purgeExpiredSessions(appContext)
            PendingKeysStore.purge(appContext)
        }

        try {
            supervisorScope {
                val proxyDeferred = async(io) {
                    if (config.authProxyConfigId.isNullOrEmpty()) null
                    else getAuthProxyConfig() // <- must be suspend + run on IO internally
                }

                val client = withContext(bg) { createTurnkeyClient(config) }

                _client = client
                rescheduleAllSessionExpiries(appContext)
                restoreSelectedSession(appContext)

                clientReady.complete(Unit)

                // Resolve final config (proxy wins; failure is non-fatal)
                val proxyConfig = runCatching { proxyDeferred.await() }.getOrNull()
                masterConfig = config.resolveWithProxy(proxyConfig)
            }
        } catch (t: Throwable) {
            if (!clientReady.isCompleted) clientReady.completeExceptionally(t)
            throw t
        }
    }

    private fun TurnkeyConfig.resolveWithProxy(
        authProxyConfig: ProxyTGetWalletKitConfigResponse?
    ): TurnkeyConfig {
        fun resolveMethod(local: Boolean?, key: String): Boolean? {
            if (local != null) return local
            val proxy = authProxyConfig ?: return null
            return key in proxy.enabledProviders
        }

        fun resolveClientId(local: String?, key: String): String? {
            if (!local.isNullOrEmpty()) return local
            return authProxyConfig?.oauthClientIds?.get(key)
        }

        fun resolveRedirect(local: String?): String? {
            if (!local.isNullOrEmpty()) return local
            return authProxyConfig?.oauthRedirectUrl
        }

        val usingAuthProxy = !this.authProxyConfigId.isNullOrEmpty()
        if (usingAuthProxy) {
            this.authConfig?.sessionExpirationSeconds?.let {
                Log.w(
                    "TurnkeyConfig",
                    "`sessionExpirationSeconds` set directly will be ignored when using an auth proxy. Configure this in the Turnkey dashboard."
                )
            }
            this.authConfig?.otpAlphanumeric?.let {
                Log.w(
                    "TurnkeyConfig",
                    "`otpAlphanumeric` set directly will be ignored when using an auth proxy. Configure this in the Turnkey dashboard."
                )
            }
            this.authConfig?.otpLength?.let {
                Log.w(
                    "TurnkeyConfig",
                    "`otpLength` set directly will be ignored when using an auth proxy. Configure this in the Turnkey dashboard."
                )
            }
        }

        // ---- resolved methods (nullable booleans) ----
        val baseMethods = this.authConfig?.methods
        val resolvedMethods = AuthMethods(
            emailOtpAuthEnabled = resolveMethod(baseMethods?.emailOtpAuthEnabled, "email"),
            smsOtpAuthEnabled = resolveMethod(baseMethods?.smsOtpAuthEnabled, "sms"),
            passkeyAuthEnabled = resolveMethod(baseMethods?.passkeyAuthEnabled, "passkey"),
            walletAuthEnabled = resolveMethod(baseMethods?.walletAuthEnabled, "wallet"),
            googleOauthEnabled = resolveMethod(baseMethods?.googleOauthEnabled, "google"),
            appleOauthEnabled = resolveMethod(baseMethods?.appleOauthEnabled, "apple"),
            xOauthEnabled = resolveMethod(baseMethods?.xOauthEnabled, "x"),
            discordOauthEnabled = resolveMethod(baseMethods?.discordOauthEnabled, "discord"),
            facebookOauthEnabled = resolveMethod(baseMethods?.facebookOauthEnabled, "facebook"),
        )

        // ---- resolved OAuth (strings) ----
        val baseOAuth = this.authConfig?.oAuthConfig
        val resolvedOAuth = OAuthConfig(
            oauthRedirectUri = resolveRedirect(baseOAuth?.oauthRedirectUri),
            googleClientId = resolveClientId(baseOAuth?.googleClientId, "google"),
            appleClientId = resolveClientId(baseOAuth?.appleClientId, "apple"),
            facebookClientId = resolveClientId(baseOAuth?.facebookClientId, "facebook"),
            xClientId = resolveClientId(baseOAuth?.xClientId, "x"),
            discordClientId = resolveClientId(baseOAuth?.discordClientId, "discord"),
        )

        // ---- proxy-only values (proxy wins; if using proxy and missing, null them) ----
        val sessionExpirationSeconds =
            authProxyConfig?.sessionExpirationSeconds
                ?: (if (usingAuthProxy) null else this.authConfig?.sessionExpirationSeconds)

        val otpAlphanumeric =
            authProxyConfig?.otpAlphanumeric
                ?: (if (usingAuthProxy) null else this.authConfig?.otpAlphanumeric)

        val otpLength =
            authProxyConfig?.otpLength
                ?: (if (usingAuthProxy) null else this.authConfig?.otpLength)

        // Preserve your createSubOrgParams as-is
        val resolvedAuth = AuthConfig(
            methods = resolvedMethods,
            oAuthConfig = resolvedOAuth,
            sessionExpirationSeconds = sessionExpirationSeconds,
            otpAlphanumeric = otpAlphanumeric,
            otpLength = otpLength,
            createSubOrgParams = this.authConfig?.createSubOrgParams
        )

        // If TurnkeyConfig is a data class, prefer copy()
        return this.copy(authConfig = resolvedAuth)
    }

    private suspend fun getAuthProxyConfig(): ProxyTGetWalletKitConfigResponse? {
        if (config.authProxyConfigId.isNullOrEmpty() || !config.autoFetchWalletKitConfig) return null

        val client = awaitClient()
        return withContext(io) {
            client.proxyGetWalletKitConfig(
                input = ProxyTGetWalletKitConfigBody()
            )
        }
    }


    /**
     * Attempt to restore a previously selected session.
     * @return true if a valid selected session exists; false otherwise (and clears selection).
     */
    suspend fun restoreSelectedSession(context: Context): Boolean = withContext(Dispatchers.IO) {

        fun resetToUnauthenticated(clearSelection: Boolean) {
            if (clearSelection) SelectedSessionStore.delete(context)
            _selectedSessionKey.value = null
            _authState.value = AuthState.unauthenticated
            _client = createTurnkeyClient(config) // stamper = null
        }

        runCatching {
            val sessionKey = SelectedSessionStore.load(context)
                ?: return@runCatching false.also { resetToUnauthenticated(clearSelection = true) }

            val dto = JwtSessionStore.load(context, sessionKey)
                ?: return@runCatching false.also { resetToUnauthenticated(clearSelection = true) }

            // success path
            scheduleExpiryTimer(sessionKey, dto.expiry)
            _selectedSessionKey.value = sessionKey
            _authState.value = AuthState.authenticated

            // this can throw → handled by outer runCatching
            setSelectedSession(sessionKey)

            true
        }.getOrElse {
            // any error → fall back once
            resetToUnauthenticated(clearSelection = true)
            false
        }
    }

    fun createTurnkeyClient(cfg: TurnkeyConfig, stamper: Stamper? = null): TurnkeyClient {
        return TurnkeyClient(
            apiBaseUrl = cfg.apiBaseUrl,
            authProxyUrl = cfg.authProxyBaseUrl,
            authProxyConfigId = cfg.authProxyConfigId,
            stamper = stamper,
            http = http
        )
    }

    /**
     * Recreate expiry timers for all stored sessions (e.g., after process restart).
     */
    suspend fun rescheduleAllSessionExpiries(context: Context) = withContext(Dispatchers.IO) {
        try {
            SessionRegistryStore.all(context).forEach { key ->
                JwtSessionStore.load(context, key)?.let { dto ->
                    scheduleExpiryTimer(key, dto.expiry)
                }
            }
        } catch (_: Throwable) {
            // Silent by design
        }
    }

    /**
     * Schedule a one-shot timer that either:
     *  - refreshes the session automatically if AutoRefreshStore has a duration; or
     *  - clears the session if not.
     *
     * @param bufferSeconds fire a little earlier than exp (default 5s).
     */
    suspend fun scheduleExpiryTimer(
        sessionKey: String,
        expTimestampSeconds: Double,
        bufferSeconds: Double = 5.0
    ) {
        // Cancel any previous timer for this key
        expiryJobs.remove(sessionKey)?.cancel()

        val nowSec = System.currentTimeMillis() / 1000.0
        val timeLeft = expTimestampSeconds - nowSec

        // Already expired or within buffer → clear now
        if (timeLeft <= bufferSeconds) {
            clearSession(sessionKey)
            return
        }

        val delayMillis = ((timeLeft - bufferSeconds) * 1000.0).toLong().coerceAtLeast(0L)

        val job = timerScope.launch {
            delay(delayMillis)

            // Re-check at handler time
            val leftNow = expTimestampSeconds - (System.currentTimeMillis() / 1000.0)
            if (leftNow <= 0.0) {
                clearSession(sessionKey)
                return@launch
            }

            val dur = AutoRefreshStore.durationSeconds(appContext, sessionKey)
            if (dur != null) {
                try {
                    val refreshDeferred = async { refreshSession(dur, sessionKey) }
                    refreshDeferred.await()
                } catch (_: Throwable) {
                    clearSession(sessionKey)
                }
            } else {
                clearSession(sessionKey)
            }
        }

        expiryJobs[sessionKey] = job
    }

    /**
     * Persist all storage artifacts for a session and schedule its expiry.
     *
     * @param refreshedSessionTTLSeconds if present, also set auto-refresh duration.
     */
    @Throws(StorageError::class)
    suspend fun persistSession(
        dto: Session,
        sessionKey: String,
        refreshedSessionTTLSeconds: String? = null
    ) = withContext(Dispatchers.IO) {
        JwtSessionStore.save(appContext, sessionKey, dto)
        SessionRegistryStore.add(appContext, sessionKey)

        // Ensure key material is present
        val priv = KeyPairStore.getPrivateHex(appContext, dto.publicKey)
        if (priv.isEmpty()) throw StorageError.KeyNotFound
        PendingKeysStore.remove(appContext, dto.publicKey)

        if (refreshedSessionTTLSeconds != null) {
            AutoRefreshStore.set(appContext, sessionKey, refreshedSessionTTLSeconds)
        }

        scheduleExpiryTimer(sessionKey, dto.expiry)
    }

    /**
     * Removes only stored artifacts for a session (does not reset in-memory UI state).
     *
     * @param keepAutoRefresh keep auto-refresh duration (true) or remove it (false)
     */
    suspend fun purgeStoredSession(
        context: Context,
        sessionKey: String,
        keepAutoRefresh: Boolean
    ) = withContext(Dispatchers.IO) {
        try {
            expiryJobs.remove(sessionKey)?.cancel()

            JwtSessionStore.load(context, sessionKey)?.let { dto ->
                runCatching { KeyPairStore.delete(context, dto.publicKey) }
            }

            JwtSessionStore.delete(context, sessionKey)
            runCatching { SessionRegistryStore.remove(context, sessionKey) }

            if (!keepAutoRefresh) {
                runCatching { AutoRefreshStore.remove(context, sessionKey) }
            }
        } catch (t: Throwable) {
            TurnkeyKotlinError.FailedToPurgeSession(t)
        }
    }

    /**
     * Update an existing session with a fresh JWT.
     * Preserves auto-refresh duration if one was configured.
     */
    @Throws(StorageError::class)
    suspend fun updateSession(
        context: Context,
        jwt: String,
        sessionKey: String = SessionStorage.DEFAULT_SESSION_KEY
    ) = withContext(Dispatchers.IO) {
        // Ensure a session already exists under this key
        val exists = JwtSessionStore.load(context, sessionKey) != null
        if (!exists) throw StorageError.KeyNotFound

        // Remove old artifacts but keep auto-refresh
        purgeStoredSession(context, sessionKey, keepAutoRefresh = true)

        val dto = JwtDecoder.decode(jwt, Session::class as Json) as Session
        val nextDuration = AutoRefreshStore.durationSeconds(context, sessionKey)
        persistSession(dto, sessionKey, nextDuration)
    }

    suspend fun refreshUser() = coroutineScope {
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession
        val userId = session.value?.userId ?: throw TurnkeyKotlinError.InvalidSession
        val res = client.getUser(TGetUserBody(organizationId, userId))
        _user.value = res.user
    }

    suspend fun refreshWallets() = coroutineScope {
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession
        val wallets = client.getWallets(TGetWalletsBody(organizationId))
        val walletAccounts = Helpers.fetchAllWalletAccountsWithCursor(client, organizationId)

        _wallets.value = Helpers.mapAccountsToWallet(walletAccounts, wallets.wallets)
    }

    suspend fun clearSession(sessionKey: String? = null) {
        val key = sessionKey ?: selectedSessionKey.value ?: return

        try {
            purgeStoredSession(appContext, sessionKey = key, keepAutoRefresh = false)
        } catch (_: Throwable) {
        }

        // If we cleared the selected session, reset in-memory state
        if (selectedSessionKey.value == key) {
            _authState.value = AuthState.unauthenticated
            _selectedSessionKey.value = null
            _client = createTurnkeyClient(masterConfig)
            _user.value = null
            _session.value = null
            _wallets.value = null
            SelectedSessionStore.delete(appContext)
        }
    }

    suspend fun clearAllSessions() {
        val keys = SessionRegistryStore.all(appContext)
        for (k in keys) {
            clearSession(k)
        }
    }

    fun createKeyPair(): String {
        val (_, pubKeyCompressed, privKey) = generateP256KeyPair()
        KeyPairStore.save(appContext, privKey, pubKeyCompressed)
        PendingKeysStore.add(appContext, pubKeyCompressed)
        return pubKeyCompressed
    }

    fun deleteKeyPair(publicKey: String) {
        KeyPairStore.delete(appContext, publicKey)
    }

    /** Deletes keypairs in secure storage that are not referenced by any session. Returns count deleted. */
    fun deleteUnusedKeyPairs() {
        val storedKeys: List<String> = KeyPairStore.listKeys(appContext)
        if (storedKeys.isEmpty()) return

        // Build a set of all public keys currently used by sessions
        val sessionKeys: List<String> = SessionRegistryStore.all(appContext)
        val usedKeys: Set<String> = sessionKeys
            .mapNotNull { sKey -> JwtSessionStore.load(appContext, sKey)?.publicKey }
            .toSet()

        for (pk in storedKeys) {
            if (pk !in usedKeys) {
                deleteKeyPair(pk)
            }
        }
    }

    suspend fun createSession(
        jwt: String,
        sessionKey: String? = null,
        refreshedSessionTTLSeconds: String? = null
    ) {
        try {
            // eventually we should verify that the jwt was signed by Turnkey
            // but for now we just assume it is

            refreshedSessionTTLSeconds?.toIntOrNull()?.let { ttl ->
                if (ttl < 30) throw TurnkeyKotlinError.InvalidRefreshTTL("Minimum allowed TTL is 30 seconds.")
            }

            // Ensure no existing session under same key
            val sessionKey = sessionKey ?: SessionStorage.DEFAULT_SESSION_KEY
            val existingKey = JwtSessionStore.load(appContext, sessionKey)
            if (existingKey != null) {
                throw TurnkeyKotlinError.KeyAlreadyExists(sessionKey)
            }

            val dto = JwtDecoder.decode<SessionJwt>(jwt)
            val expirySeconds = ((dto.expiry * 1000.0 - System.currentTimeMillis()) / 1000).toLong()
            print(expirySeconds)
            val s = Session(
                organizationId = dto.organizationId,
                userId = dto.userId,
                expiry = dto.expiry,
                publicKey = dto.publicKey,
                sessionType = dto.sessionType,
                expirationSeconds = expirySeconds.toString(),
                token = jwt
            )

            persistSession(
                dto = s,
                sessionKey = sessionKey,
                refreshedSessionTTLSeconds = refreshedSessionTTLSeconds
            )

            // If no selection yet, make this the selected session
            if (selectedSessionKey.value == null) {
                setSelectedSession(sessionKey)
            }

            withContext(Dispatchers.Main) {
                _authState.value = AuthState.authenticated
            }
        } catch (error: Throwable) {
            throw TurnkeyKotlinError.FailedToCreateSession(error)
        }
    }

    suspend fun setSelectedSession(sessionKey: String): TurnkeyClient {
        try {
            val dto = JwtSessionStore.load(appContext, sessionKey)
                ?: throw TurnkeyKotlinError.KeyNotFound(sessionKey)

            val privHex = KeyPairStore.getPrivateHex(appContext, dto.publicKey)
            val cli = createTurnkeyClient(
                config,
                Stamper(apiPublicKey = dto.publicKey, apiPrivateKey = privHex)
            )

            withContext(Dispatchers.Main) {
                SelectedSessionStore.save(appContext, sessionKey)
                _selectedSessionKey.value = sessionKey
                _client = cli
                _session.value = dto
            }

            if (config.autoRefreshManagedStates) {
                refreshUser()
                refreshWallets()
            }

            return cli
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSetSelectedSession(t)
        }
    }

    @Throws(TurnkeyKotlinError::class)
    suspend fun refreshSession(
        expirationSeconds: String = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false
    ) {
        val targetKey = sessionKey ?: selectedSessionKey.value ?: SessionStorage.DEFAULT_SESSION_KEY

        // Use current client if this is the selected session; else build one from stored material
        val (clientToUse, orgId) = if (targetKey == selectedSessionKey.value) {
            val curClient = _client ?: throw TurnkeyKotlinError.InvalidSession
            val curSession = session.value ?: throw TurnkeyKotlinError.InvalidSession
            curClient to curSession.organizationId
        } else {
            val dto =
                JwtSessionStore.load(appContext, targetKey) ?: throw TurnkeyKotlinError.KeyNotFound(
                    targetKey
                )
            val privHex = KeyPairStore.getPrivateHex(appContext, dto.publicKey)
            val cli = createTurnkeyClient(
                config,
                Stamper(apiPublicKey = dto.publicKey, apiPrivateKey = privHex)
            )
            cli to dto.organizationId
        }

        // Generate a fresh ephemeral key (public is used to log in)
        val newPublicKey = createKeyPair()

        try {
            // Call the “stamp login” endpoint (shape may vary with your generator)
            // If your client expects an input DTO, construct it here instead.
            val resp = clientToUse.stampLogin(
                TStampLoginBody(
                    organizationId = orgId,
                    publicKey = newPublicKey,
                    expirationSeconds = expirationSeconds,
                    invalidateExisting = invalidateExisting
                )
            )

            val jwt = resp
                .activity
                .result
                .stampLoginResult
                ?.session
                ?: throw TurnkeyKotlinError.InvalidResponse("Session not found in stampLogin response.")

            // Swap the stored session contents (preserve auto-refresh duration)
            updateSession(appContext, jwt = jwt, sessionKey = targetKey)

            // If this was the selected session, replace the client in memory
            if (targetKey == selectedSessionKey.value) {
                val updated = JwtSessionStore.load(appContext, targetKey)!!
                val privHex = KeyPairStore.getPrivateHex(appContext, updated.publicKey)

                val newClient = createTurnkeyClient(
                    config,
                    Stamper(apiPublicKey = updated.publicKey, apiPrivateKey = privHex)
                )

                withContext(Dispatchers.Main) {
                    _client = newClient
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRefreshSession(t)
        }
    }

    suspend fun loginWithOAuth(
        oidcToken: String,
        publicKey: String,
        invalidateExisting: Boolean? = false,
        sessionKey: String? = null,
        organizationId: String? = null,
    ): LoginWithOAuthResult {
        try {
            val res = client.proxyOAuthLogin(
                ProxyTOAuthLoginBody(
                    oidcToken = oidcToken,
                    publicKey = publicKey,
                    invalidateExisting = invalidateExisting,
                    organizationId = organizationId
                )
            )
            val sessionJwt = res.session
            createSession(sessionJwt, sessionKey)
            return LoginWithOAuthResult(sessionJwt = sessionJwt)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginWithOAuth(t)
        }
    }

    suspend fun signUpWithOAuth(
        oidcToken: String,
        publicKey: String,
        providerName: String,
        createSubOrgParams: CreateSubOrgParams? = null,
        sessionKey: String? = null
    ): SignUpWithOAuthResult {
        val overrideParams = OAuthOverrideParams(
            providerName,
            oidcToken
        )
        val updatedCreateSubOrgParams = Helpers.getCreateSubOrgParams(
            createSubOrgParams,
            masterConfig,
            overrideParams
        )
        val signUpBody = Helpers.buildSignUpBody(
            updatedCreateSubOrgParams
        )

        try {
            val res = client.proxySignup(signUpBody)
            val orgId = res.organizationId
            if (orgId.isEmpty()) {
                throw TurnkeyKotlinError.SignUpFailed("No organizationId returned")
            }

            val loginRes = loginWithOAuth(
                oidcToken = oidcToken,
                publicKey = publicKey,
                sessionKey = sessionKey
            )

            return SignUpWithOAuthResult(sessionJwt = loginRes.sessionJwt)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignUpWithOAuth(t)
        }
    }

    suspend fun loginOrSignUpWithOAuth(
        oidcToken: String,
        publicKey: String,
        providerName: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
    ): LoginOrSignUpWithOAuthResult {
        try {
            val accountRes = client.proxyGetAccount(
                input = ProxyTGetAccountBody(
                    filterType = "OIDC_TOKEN",
                    filterValue = oidcToken
                )
            )
            if (accountRes.organizationId.isNullOrBlank()) {
                if (providerName.isNullOrBlank()) throw TurnkeyKotlinError.SignUpFailed("Provider name is required for OAuth sign up")
                val signUpRes = signUpWithOAuth(
                    oidcToken,
                    publicKey,
                    providerName,
                    createSubOrgParams,
                    sessionKey,
                )
                return LoginOrSignUpWithOAuthResult(sessionJwt = signUpRes.sessionJwt)
            } else {
                val loginRes = loginWithOAuth(
                    oidcToken,
                    publicKey,
                    invalidateExisting,
                    sessionKey
                )
                return LoginOrSignUpWithOAuthResult(sessionJwt = loginRes.sessionJwt)
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginOrSignUpWithOAuth(t)
        }
    }

    suspend fun loginWithPasskey(
        activity: Activity,
        sessionKey: String? = null,
        expirationSeconds: String? = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        organizationId: String? = null,
        publicKey: String? = null,
        invalidateExisting: Boolean? = false,
        rpId: String? = null,
    ): LoginWithPasskeyResult {
        val sessionKey = sessionKey ?: SessionStorage.DEFAULT_SESSION_KEY
        val rpId = rpId ?: config.authConfig?.rpId ?: throw TurnkeyKotlinError.MissingRpId
        val organizationId = organizationId ?: config.organizationId
        val generatedPublicKey: String?

        try {
            generatedPublicKey = publicKey ?: createKeyPair()
            val privKey = KeyPairStore.getPrivateHex(appContext, generatedPublicKey)
            _client = createTurnkeyClient(config, stamper = Stamper(generatedPublicKey, privKey))
            val passkeyStamper = PasskeyStamper(
                activity,
                rpId
            )
            val passkeyClient = TurnkeyClient(
                apiBaseUrl = config.apiBaseUrl,
                stamper = Stamper(passkeyStamper)
            )
            val loginRes = passkeyClient.stampLogin(
                TStampLoginBody(
                    organizationId = organizationId,
                    publicKey = generatedPublicKey,
                    expirationSeconds = expirationSeconds,
                    invalidateExisting = invalidateExisting
                )
            )
            val sessionToken = loginRes.activity.result.stampLoginResult?.session
                ?: throw TurnkeyKotlinError.InvalidResponse("No session token returned from stampLogin")

            createSession(sessionToken, sessionKey)
            return LoginWithPasskeyResult(sessionJwt = sessionToken)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginWithPasskey(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    suspend fun signUpWithPasskey(
        activity: Activity,
        sessionKey: String? = null,
        expirationSeconds: String? = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        passkeyDisplayName: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        invalidateExisting: Boolean? = null,
        rpId: String? = null
    ): SignUpWithPasskeyResult {
        val sessionKey = sessionKey ?: SessionStorage.DEFAULT_SESSION_KEY
        val rpId = rpId ?: config.authConfig?.rpId ?: throw TurnkeyKotlinError.MissingRpId
        val generatedPublicKey: String?
        var temporaryPublicKey: String?

        try {
            temporaryPublicKey = createKeyPair();
            val passkeyName = passkeyDisplayName ?: "passkey-${Date().time}"
            val privKey = KeyPairStore.getPrivateHex(appContext, temporaryPublicKey)
            _client = createTurnkeyClient(config, stamper = Stamper(temporaryPublicKey, privKey))

            val passkey = createPasskey(
                activity = activity,
                user = PasskeyUser(
                    id = UUID.randomUUID().toString(),
                    name = passkeyName,
                    displayName = passkeyName,
                ),
                rpId = rpId,
            )

            val encodedChallenge = passkey.challenge
            val attestation = passkey.attestation

            val overrideParams = PasskeyOverrideParams(
                passkeyName = passkeyName,
                attestation = attestation,
                encodedChallenge = encodedChallenge,
                temporaryPublicKey = temporaryPublicKey
            )
            val updatedCreateSubOrgParams =
                Helpers.getCreateSubOrgParams(createSubOrgParams, masterConfig, overrideParams)

            val signUpBody = Helpers.buildSignUpBody(updatedCreateSubOrgParams)

            val res = client.proxySignup(signUpBody)

            val orgId = res.organizationId
            if (orgId.isEmpty()) {
                throw TurnkeyKotlinError.SignUpFailed("No organizationId returned")
            }

            generatedPublicKey = createKeyPair()

            val loginRes = client.stampLogin(
                TStampLoginBody(
                    organizationId = orgId,
                    publicKey = generatedPublicKey,
                    expirationSeconds = expirationSeconds,
                    invalidateExisting = invalidateExisting
                )
            )

            val sessionToken = loginRes.activity.result.stampLoginResult?.session
                ?: throw TurnkeyKotlinError.InvalidResponse("No session token returned from stampLogin")

            createSession(sessionToken, sessionKey)
            return SignUpWithPasskeyResult(sessionJwt = sessionToken)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignUpWithPasskey(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    suspend fun initOtp(
        otpType: OtpType,
        contact: String
    ): InitOtpResult {
        val res = client.proxyInitOtp(
            ProxyTInitOtpBody(
                contact = contact,
                otpType = otpType.name
            )
        )
        return InitOtpResult(otpId = res.otpId)
    }

    suspend fun verifyOtp(
        otpCode: String,
        otpId: String,
        contact: String,
        otpType: OtpType
    ): VerifyOtpResult {
        val verifyOtpRes = client.proxyVerifyOtp(
            ProxyTVerifyOtpBody(
                otpId,
                otpCode
            )
        )
        if (verifyOtpRes.verificationToken.isEmpty()) throw TurnkeyKotlinError.InvalidResponse("Failed to verify OTP, missing verification token in response.")

        val accountRes = client.proxyGetAccount(
            ProxyTGetAccountBody(
                filterType = otpTypeToFilterTypeMap.getValue(otpType).name,
                filterValue = contact
            )
        )

        val subOrganizationId = accountRes.organizationId
        return VerifyOtpResult(
            subOrganizationId = subOrganizationId,
            verificationToken = verifyOtpRes.verificationToken
        )
    }

    suspend fun loginWithOtp(
        verificationToken: String,
        organizationId: String? = null,
        invalidateExisting: Boolean? = false,
        publicKey: String? = null,
        sessionKey: String? = null,
    ): LoginWithOtpResult {
        var generatedPublicKey: String?

        try {
            generatedPublicKey = publicKey ?: createKeyPair()

            val res = client.proxyOtpLogin(
                ProxyTOtpLoginBody(
                    organizationId = organizationId,
                    publicKey = generatedPublicKey,
                    verificationToken = verificationToken,
                    invalidateExisting = invalidateExisting
                )
            )

            createSession(res.session, sessionKey)

            return LoginWithOtpResult(sessionJwt = res.session)
        } catch (t: Throwable) {
            deleteUnusedKeyPairs()
            throw TurnkeyKotlinError.FailedToLoginWithOtp(t)
        }
    }

    suspend fun signUpWithOtp(
        verificationToken: String,
        contact: String,
        otpType: OtpType,
        publicKey: String? = null,
        sessionKey: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        invalidateExisting: Boolean? = false
    ): SignUpWithOtpResult {
        val overrideParams = OtpOverrireParams(
            otpType = otpType,
            contact = contact,
            verificationToken = verificationToken
        )

        val updatedCreateSubOrgParams =
            Helpers.getCreateSubOrgParams(createSubOrgParams, masterConfig, overrideParams)
        val signUpBody = Helpers.buildSignUpBody(updatedCreateSubOrgParams)

        try {
            val res = client.proxySignup(signUpBody)
            val orgId = res.organizationId

            if (orgId.isEmpty()) throw TurnkeyKotlinError.SignUpFailed("No organizationId returned")

            val loginRes = loginWithOtp(
                verificationToken = verificationToken,
                organizationId = orgId,
                sessionKey = sessionKey,
                invalidateExisting = invalidateExisting,
                publicKey = publicKey
            )

            return SignUpWithOtpResult(sessionJwt = loginRes.sessionJwt)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignUpWithOtp(t)
        }
    }

    suspend fun loginOrSignUpWithOtp(
        otpId: String,
        otpCode: String,
        contact: String,
        otpType: OtpType,
        publicKey: String? = null,
        invalidateExisting: Boolean? = false,
        sessionKey: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null
    ): LoginOrSignUpWithOtpResult {
        try {
            val verifyRes = verifyOtp(
                otpCode = otpCode,
                otpId = otpId,
                contact = contact,
                otpType = otpType
            )
            if (verifyRes.subOrganizationId.isNullOrEmpty()) {
                val signUpRes = signUpWithOtp(
                    verificationToken = verifyRes.verificationToken,
                    contact = contact,
                    otpType = otpType,
                    publicKey = publicKey,
                    sessionKey = sessionKey,
                    createSubOrgParams = createSubOrgParams,
                    invalidateExisting = invalidateExisting
                )

                return LoginOrSignUpWithOtpResult(sessionJwt = signUpRes.sessionJwt)
            } else {
                val loginRes = loginWithOtp(
                    verificationToken = verifyRes.verificationToken,
                    organizationId = verifyRes.subOrganizationId,
                    invalidateExisting = invalidateExisting,
                    publicKey = publicKey,
                    sessionKey = sessionKey
                )
                return LoginOrSignUpWithOtpResult(sessionJwt = loginRes.sessionJwt)
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginOrSignUpWithOtp(t)
        }
    }

    suspend fun handleGoogleOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = Turnkey.OAUTH_ORIGIN_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((String) -> Unit)? = null,
        timeoutMinutes: Long = 10
    ) {
        val scheme = masterConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val googleClientId = clientId
            ?: masterConfig.authConfig?.oAuthConfig?.googleClientId
            ?: throw TurnkeyKotlinError.MissingConfigParam("Google Client ID not configured")

        val resolvedRedirect = redirectUri
            ?: masterConfig.authConfig?.oAuthConfig?.oauthRedirectUri
            ?: "${Turnkey.OAUTH_REDIRECT_URL}?scheme=${Uri.encode(scheme)}"

        val oauthUrl = buildString {
            append(originUri)
            append("?provider=google")
            append("&clientId=").append(Uri.encode(googleClientId))
            append("&redirectUri=").append(Uri.encode(resolvedRedirect))
            append("&nonce=").append(Uri.encode(nonce))
        }

        Helpers.openCustomTab(activity, oauthUrl)

        try {
            val uri = withTimeout(timeoutMinutes * 60_000) {
                OAuthEvents.deepLinks
                    .filter { it.scheme.equals(scheme, ignoreCase = true) }
                    .first()
            }

            val idToken = uri.getQueryParameter("id_token")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing id_token in redirect")

            withContext(Dispatchers.IO) {
                if (onSuccess != null) {
                    onSuccess(idToken)
                } else {
                    // default behavior
                    loginOrSignUpWithOAuth(
                        oidcToken = idToken,
                        publicKey = targetPublicKey,
                        providerName = "google",
                        sessionKey = sessionKey,
                        invalidateExisting = invalidateExisting,
                        createSubOrgParams = createSubOrgParams
                    )
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToHandleGoogleOAuth(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    suspend fun handleAppleOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.APPLE_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((String) -> Unit)? = null,
        timeoutMinutes: Long = 10
    ) {
        val scheme = masterConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val appleClientId = clientId
            ?: masterConfig.authConfig?.oAuthConfig?.appleClientId
            ?: throw TurnkeyKotlinError.MissingConfigParam("Apple Client ID not configured")

        val resolvedRedirect = redirectUri
            ?: masterConfig.authConfig?.oAuthConfig?.oauthRedirectUri
            ?: "${Turnkey.OAUTH_REDIRECT_URL}?scheme=${Uri.encode(scheme)}"

        val oauthUrl = buildString {
            append(originUri)
            append("?provider=apple")
            append("&clientId=").append(Uri.encode(appleClientId))
            append("&redirectUri=").append(Uri.encode(resolvedRedirect))
            append("&nonce=").append(Uri.encode(nonce))
        }

        Helpers.openCustomTab(activity, oauthUrl)

        try {
            val uri = withTimeout(timeoutMinutes * 60_000) {
                OAuthEvents.deepLinks
                    .filter { it.scheme.equals(scheme, ignoreCase = true) }
                    .first()
            }

            val idToken = uri.getQueryParameter("id_token")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing id_token in redirect")

            withContext(Dispatchers.IO) {
                if (onSuccess != null) {
                    onSuccess(idToken)
                } else {
                    // default behavior
                    loginOrSignUpWithOAuth(
                        oidcToken = idToken,
                        publicKey = targetPublicKey,
                        providerName = "apple",
                        sessionKey = sessionKey,
                        createSubOrgParams = createSubOrgParams,
                        invalidateExisting = invalidateExisting
                    )
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToHandleAppleOAuth(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    // TODO: DO THIS LATER
    // suspend fun handleFacebookOAuth() {}

    suspend fun handleXOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.X_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((String) -> Unit)? = null,
        timeoutMinutes: Long = 10,
    ) {
        val scheme = masterConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val xClientId = clientId
            ?: masterConfig.authConfig?.oAuthConfig?.xClientId
            ?: throw TurnkeyKotlinError.MissingConfigParam("X Client ID not configured")

        val resolvedRedirect = redirectUri
            ?: masterConfig.authConfig?.oAuthConfig?.oauthRedirectUri
            ?: "$scheme://"

        val challengePair = Helpers.generateChallengePair()

        val state =
            "provider=twitter&flow=redirect&publicKey=${Uri.encode(targetPublicKey)}&nonce=${nonce}"

        val xAuthUrl = buildString {
            append(originUri)
            append("?client_id=${Uri.encode(xClientId)}")
            append("&redirect_uri=${Uri.encode(resolvedRedirect)}")
            append("&response_type=code")
            append("&code_challenge=${Uri.encode(challengePair.codeChallenge)}")
            append("&code_challenge_method=S256")
            append("&scope=${Uri.encode("tweet.read users.read")}")
            append("&state=${Uri.encode(state)}")
        }

        Helpers.openCustomTab(activity, xAuthUrl)

        try {
            val uri = withTimeout(timeoutMinutes * 60_000) {
                OAuthEvents.deepLinks
                    .filter { it.scheme.equals(scheme, ignoreCase = true) }
                    .first()
            }

            val authCode = uri.getQueryParameter("code")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing code in redirect")

            withContext(Dispatchers.IO) {
                val res = client.proxyOAuth2Authenticate(
                    ProxyTOAuth2AuthenticateBody(
                        provider = V1Oauth2Provider.OAUTH2_PROVIDER_X,
                        authCode = authCode,
                        redirectUri = resolvedRedirect,
                        codeVerifier = challengePair.verifier,
                        clientId = xClientId,
                        nonce = nonce
                    )
                )

                val oidcToken = res.oidcToken

                if (onSuccess != null) {
                    onSuccess(oidcToken)
                } else {
                    // default behavior
                    loginOrSignUpWithOAuth(
                        oidcToken = oidcToken,
                        publicKey = targetPublicKey,
                        providerName = "x",
                        sessionKey = sessionKey,
                        invalidateExisting = invalidateExisting,
                        createSubOrgParams = createSubOrgParams
                    )
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToHandleXOAuth(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    suspend fun handleDiscordOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.DISCORD_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((String) -> Unit)? = null,
        timeoutMinutes: Long = 10,
    ) {
        val scheme = masterConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val discordClientId = clientId
            ?: masterConfig.authConfig?.oAuthConfig?.discordClientId
            ?: throw TurnkeyKotlinError.MissingConfigParam("Discord Client ID not configured")

        val resolvedRedirect = redirectUri
            ?: masterConfig.authConfig?.oAuthConfig?.oauthRedirectUri
            ?: "$scheme://"

        val challengePair = Helpers.generateChallengePair()

        val state =
            "provider=discord&flow=redirect&publicKey=${Uri.encode(targetPublicKey)}&nonce=${nonce}"

        val discordAuthUrl = buildString {
            append(originUri)
            append("?client_id=${Uri.encode(discordClientId)}")
            append("&redirect_uri=${Uri.encode(resolvedRedirect)}")
            append("&response_type=code")
            append("&code_challenge=${Uri.encode(challengePair.codeChallenge)}")
            append("&code_challenge_method=S256")
            append("&scope=${Uri.encode("identify email")}")
            append("&state=${Uri.encode(state)}")
        }

        Helpers.openCustomTab(activity, discordAuthUrl)

        try {
            val uri = withTimeout(timeoutMinutes * 60_000) {
                OAuthEvents.deepLinks
                    .filter { it.scheme.equals(scheme, ignoreCase = true) }
                    .first()
            }

            val authCode = uri.getQueryParameter("code")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing code in redirect")

            withContext(Dispatchers.IO) {
                val res = client.proxyOAuth2Authenticate(
                    ProxyTOAuth2AuthenticateBody(
                        provider = V1Oauth2Provider.OAUTH2_PROVIDER_DISCORD,
                        authCode = authCode,
                        redirectUri = resolvedRedirect,
                        codeVerifier = challengePair.verifier,
                        clientId = discordClientId,
                        nonce = nonce
                    )
                )

                val oidcToken = res.oidcToken

                if (onSuccess != null) {
                    onSuccess(oidcToken)
                } else {
                    // default behavior
                    loginOrSignUpWithOAuth(
                        oidcToken = oidcToken,
                        publicKey = targetPublicKey,
                        providerName = "discord",
                        sessionKey = sessionKey,
                        invalidateExisting = invalidateExisting,
                        createSubOrgParams = createSubOrgParams
                    )
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToHandleDiscordOAuth(t)
        } finally {
            deleteUnusedKeyPairs()
        }
    }

    suspend fun createWallet(
        walletName: String,
        accounts: List<V1WalletAccountParams>,
        mnemonicLength: Long
    ): V1CreateWalletResult {
        try {
            val organizationId =
                session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession
            val res = client.createWallet(
                TCreateWalletBody(
                    organizationId = organizationId,
                    accounts = accounts,
                    walletName = walletName,
                    mnemonicLength = mnemonicLength
                )
            )
            if (res.activity.result.createWalletResult?.walletId.isNullOrEmpty()) throw TurnkeyKotlinError.InvalidResponse(
                "No walletId returned from createWallet"
            )

            if (config.autoRefreshManagedStates) refreshWallets()

            return res.activity.result.createWalletResult!!
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToCreateWallet(t)
        }
    }

    suspend fun signRawPayload(
        signWith: String,
        payload: String,
        encoding: V1PayloadEncoding,
        hashFunction: V1HashFunction
    ): V1SignRawPayloadsResult {
        try {
            val res = client.signRawPayload(
                TSignRawPayloadBody(
                    organizationId = session.value!!.organizationId,
                    signWith = signWith,
                    payload = payload,
                    encoding = encoding,
                    hashFunction = hashFunction
                )
            )
            return res.activity.result.signRawPayloadsResult
                ?: throw TurnkeyKotlinError.InvalidResponse("No result returned from SignRawPayload")
        } catch (e: Throwable) {
            throw TurnkeyKotlinError.FailedToSignRawPayload(e)
        }
    }

    suspend fun signMessage(
        signWith: String,
        addressFormat: V1AddressFormat,
        message: String,
        encoding: V1PayloadEncoding? = null,
        hashFunction: V1HashFunction? = null,
        addEthereumPrefix: Boolean? = null
    ): V1SignRawPayloadResult {
        val defaults = Helpers.defaultsFor(addressFormat)
        val finalEncoding = encoding ?: defaults.encoding
        val finalHash = hashFunction ?: defaults.hashFunction

        var messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        if (addressFormat == V1AddressFormat.ADDRESS_FORMAT_ETHEREUM) {
            val shouldPrefix = addEthereumPrefix ?: true
            if (shouldPrefix) messageBytes = Helpers.ethereumPrefixed(messageBytes)
        }

        val payload = Helpers.encodeMessageBytes(messageBytes, finalEncoding)

        try {
            val res = client.signRawPayload(
                TSignRawPayloadBody(
                    organizationId = session.value?.organizationId
                        ?: throw TurnkeyKotlinError.InvalidSession,
                    encoding = finalEncoding,
                    hashFunction = finalHash,
                    payload = payload,
                    signWith = signWith
                )
            )
            return res.activity.result.signRawPayloadResult
                ?: throw TurnkeyKotlinError.InvalidResponse("Invalid sign raw payload result")
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignMessage(t)
        }
    }

    suspend fun importWallet(
        walletName: String,
        mnemonic: String,
        accounts: List<V1WalletAccountParams>
    ): V1ImportWalletResult {
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession
        val userId = session.value?.userId ?: throw TurnkeyKotlinError.InvalidSession
        try {
            val initRes = client.initImportWallet(
                TInitImportWalletBody(
                    organizationId = organizationId,
                    userId = userId
                )
            )

            val importBundle = initRes.activity.result.initImportWalletResult?.importBundle
                ?: throw TurnkeyKotlinError.InvalidResponse("No import bundle returned from initImportWallet")

            val encrypted = encryptWalletToBundle(
                mnemonic = mnemonic,
                importBundle = importBundle,
                userId = userId,
                organizationId = organizationId,
                null
            )

            val res = client.importWallet(
                TImportWalletBody(
                    organizationId = organizationId,
                    accounts = accounts,
                    encryptedBundle = encrypted,
                    userId = userId,
                    walletName = walletName
                )
            )

            val result = res.activity.result.importWalletResult
                ?: throw TurnkeyKotlinError.InvalidResponse("No result found from importWallet")

            if (config.autoRefreshManagedStates) refreshWallets()
            return result
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToImportWallet(t)
        }
    }

    suspend fun exportWallet(
        walletId: String,
    ): ExportWalletResult {
        val (targetPublicKey, _, embeddedPriv) = generateP256KeyPair()
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession

        try {
            val res = client.exportWallet(
                TExportWalletBody(
                    organizationId = organizationId,
                    targetPublicKey = targetPublicKey,
                    walletId = walletId
                )
            )

            val bundle = res.activity.result.exportWalletResult?.exportBundle
                ?: throw TurnkeyKotlinError.InvalidResponse("No export bundle returned from exportWallet")

            val mnemonicPhrase = decryptExportBundle(
                exportBundle = bundle,
                organizationId = organizationId,
                embeddedPrivateKey = embeddedPriv,
                dangerouslyOverrideSignerPublicKey = null,
                returnMnemonic = true
            )
            return ExportWalletResult(mnemonicPhrase = mnemonicPhrase)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToExportWallet(t)
        }
    }
}