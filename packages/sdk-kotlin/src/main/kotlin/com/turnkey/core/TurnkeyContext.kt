package com.turnkey.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.turnkey.crypto.decryptExportBundle
import com.turnkey.http.TurnkeyClient
import com.turnkey.core.internal.utils.Helpers
import com.turnkey.core.internal.utils.JwtDecoder
import com.turnkey.core.internal.storage.keys.PendingKeysStore
import com.turnkey.core.internal.storage.sessions.AutoRefreshStore
import com.turnkey.core.internal.storage.sessions.JwtSessionStore
import com.turnkey.core.internal.storage.sessions.SelectedSessionStore
import com.turnkey.core.internal.storage.sessions.SessionRegistryStore
import com.turnkey.core.models.AuthState
import com.turnkey.core.models.TurnkeyConfig
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
import com.turnkey.crypto.generateP256KeyPair
import com.turnkey.core.internal.utils.ClientSignature
import com.turnkey.core.models.CreateSubOrgParams
import com.turnkey.core.models.ExportWalletResult
import com.turnkey.core.models.InitOtpResult
import com.turnkey.core.models.LoginOrSignUpWithOAuthResult
import com.turnkey.core.models.LoginOrSignUpWithOtpResult
import com.turnkey.core.models.LoginWithOAuthResult
import com.turnkey.core.models.LoginWithOtpResult
import com.turnkey.core.models.LoginWithPasskeyResult
import com.turnkey.core.models.OAuth
import com.turnkey.core.models.OAuthConfig
import com.turnkey.core.models.OAuthOverrideParams
import com.turnkey.core.models.OtpOverrireParams
import com.turnkey.core.models.OtpType
import com.turnkey.core.models.PasskeyOverrideParams
import com.turnkey.core.models.RuntimeAuthConfig
import com.turnkey.core.models.Session
import com.turnkey.core.models.SessionJwt
import com.turnkey.core.models.SessionStorage
import com.turnkey.core.models.SignUpWithOAuthResult
import com.turnkey.core.models.SignUpWithOtpResult
import com.turnkey.core.models.SignUpWithPasskeyResult
import com.turnkey.core.models.Turnkey
import com.turnkey.core.models.errors.TurnkeyKotlinError
import com.turnkey.core.models.TurnkeyRuntimeConfig
import com.turnkey.core.models.VerifyOtpResult
import com.turnkey.core.models.Wallet
import com.turnkey.core.models.errors.TurnkeyStorageError
import com.turnkey.core.models.otpTypeToFilterTypeMap
import com.turnkey.crypto.encryptWalletToBundle
import com.turnkey.passkey.PasskeyStamper
import com.turnkey.passkey.PasskeyUser
import com.turnkey.passkey.createPasskey
import com.turnkey.stamper.utils.SignatureFormat
import com.turnkey.stamper.Stamper
import com.turnkey.types.ProxyTGetAccountBody
import com.turnkey.types.ProxyTGetWalletKitConfigBody
import com.turnkey.types.ProxyTGetWalletKitConfigResponse
import com.turnkey.types.ProxyTInitOtpBody
import com.turnkey.types.ProxyTOAuth2AuthenticateBody
import com.turnkey.types.ProxyTOAuthLoginBody
import com.turnkey.types.ProxyTOtpLoginBody
import com.turnkey.types.ProxyTSignupBody
import com.turnkey.types.ProxyTVerifyOtpBody
import com.turnkey.types.TCreateWalletBody
import com.turnkey.types.TExportWalletBody
import com.turnkey.types.TGetUserBody
import com.turnkey.types.TGetWalletsBody
import com.turnkey.types.TImportWalletBody
import com.turnkey.types.TInitImportWalletBody
import com.turnkey.types.TSignRawPayloadBody
import com.turnkey.types.TStampLoginBody
import com.turnkey.types.V1AddressFormat
import com.turnkey.types.V1ClientSignature
import com.turnkey.types.V1ClientSignatureScheme
import com.turnkey.types.V1CreateWalletResult
import com.turnkey.types.V1HashFunction
import com.turnkey.types.V1ImportWalletResult
import com.turnkey.types.V1Oauth2Provider
import com.turnkey.types.V1PayloadEncoding
import com.turnkey.types.V1SignRawPayloadResult
import com.turnkey.types.V1User
import com.turnkey.types.V1WalletAccountParams
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.UUID

object TurnkeyContext {
    @Volatile
    private var initialized = false
    private val initMutex = Mutex()
    lateinit var appContext: Context
    private lateinit var config: TurnkeyConfig
    private lateinit var runtimeConfig: TurnkeyRuntimeConfig

    private val io = Dispatchers.IO
    private val bg = Dispatchers.Default

    private val _authState = MutableStateFlow(AuthState.loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @Volatile
    private var _client: TurnkeyClient? = null
    private val clientReady = CompletableDeferred<Unit>()
    val client: TurnkeyClient
        get() = _client ?: throw TurnkeyKotlinError.ClientNotInitialized()

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

    /**
     * Registers a callback to be invoked when the application enters the foreground.
     *
     * This method allows you to execute custom logic whenever the app transitions from
     * background to foreground state. The callback is tied to the process lifecycle.
     *
     * @param onEnterForeground callback to invoke when the app enters foreground
     */
    fun registerForegroundObserver(onEnterForeground: () -> Unit) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                onEnterForeground()
            }
        })
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /**
     * Initializes the Turnkey SDK with the provided configuration.
     *
     * This is a non-blocking initialization method that must be called from `Application.onCreate()`.
     * It launches initialization in the background and returns immediately. The SDK will not be
     * ready to use until initialization completes.
     *
     * For initialization that requires waiting for readiness, use [awaitReady] after calling this method,
     * or call [initSuspend] directly from a coroutine.
     *
     * Example:
     * ```kotlin
     * class MyApp : Application() {
     *     override fun onCreate() {
     *         super.onCreate()
     *         TurnkeyContext.init(this, config)
     *     }
     * }
     * ```
     *
     * @param app the Application instance
     * @param config the Turnkey configuration
     * @see awaitReady
     * @see initSuspend
     */
    fun init(app: Application, config: TurnkeyConfig) {
        scope.launch {
            initSuspend(app, config)
        }
    }

    /**
     * Suspends until the Turnkey client is fully initialized and ready to use.
     *
     * Call this method from a coroutine after [init] to ensure the SDK is ready before
     * attempting to use any authenticated functionality.
     *
     * Example:
     * ```kotlin
     * lifecycleScope.launch {
     *     TurnkeyContext.awaitReady()
     *     // Now safe to use TurnkeyContext.client
     * }
     * ```
     *
     * @see init
     * @see initSuspend
     */
    suspend fun awaitReady() {
        clientReady.await()
    }

    private suspend fun awaitClient(): TurnkeyClient {
        _client?.let { return it }
        clientReady.await()
        return checkNotNull(_client) { "Client not available after initialization." }
    }

    /**
     * Suspending version of [init] that blocks until initialization is complete.
     *
     * This method performs the full SDK initialization including:
     * - Setting up the application context
     * - Fetching auth proxy configuration (if configured)
     * - Creating the Turnkey HTTP client
     * - Restoring the previously selected session (if any)
     * - Rescheduling session expiry timers
     *
     * Unlike [init], this method suspends until all initialization is complete. Use this when
     * you need to ensure the SDK is ready before proceeding, or when calling from within a
     * coroutine context.
     *
     * @param app the Application instance
     * @param cfg the Turnkey configuration
     * @see init
     * @see awaitReady
     */
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

                Stamper.configure(appContext, config.authConfig?.rpId)
                val client = withContext(bg) { createTurnkeyClient(config) }

                _client = client
                rescheduleAllSessionExpiries(appContext)
                restoreSelectedSession(appContext)

                clientReady.complete(Unit)

                // Resolve final config (proxy wins; failure is non-fatal)
                val proxyConfig = runCatching { proxyDeferred.await() }.getOrNull()
                runtimeConfig = config.resolveWithProxy(proxyConfig)
            }
        } catch (t: Throwable) {
            if (!clientReady.isCompleted) clientReady.completeExceptionally(t)
            throw t
        }
    }

    private fun TurnkeyConfig.resolveWithProxy(
        authProxyConfig: ProxyTGetWalletKitConfigResponse?
    ): TurnkeyRuntimeConfig {
        fun resolveClientId(local: String?, key: String): String? {
            if (!local.isNullOrEmpty()) return local
            return authProxyConfig?.oauthClientIds?.get(key)
        }

        fun resolveRedirect(local: String?): String? {
            if (!local.isNullOrEmpty()) return local
            return authProxyConfig?.oauthRedirectUrl
        }

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

        // ---- proxy-only values ----
        val sessionExpirationSeconds = authProxyConfig?.sessionExpirationSeconds
        val otpAlphanumeric = authProxyConfig?.otpAlphanumeric
        val otpLength = authProxyConfig?.otpLength

        val resolvedAuth = RuntimeAuthConfig(
            oAuthConfig = resolvedOAuth,
            sessionExpirationSeconds = sessionExpirationSeconds,
            otpAlphanumeric = otpAlphanumeric,
            otpLength = otpLength,
            createSubOrgParams = this.authConfig?.createSubOrgParams
        )

        return TurnkeyRuntimeConfig(
            organizationId = this.organizationId,
            apiBaseUrl = this.apiBaseUrl,
            authProxyBaseUrl = this.authProxyBaseUrl,
            authProxyConfigId = this.authProxyConfigId,
            authConfig = resolvedAuth,
            appScheme = this.appScheme,
            autoRefreshManagedStates = this.autoRefreshManagedStates,
            autoFetchWalletKitConfig = this.autoFetchWalletKitConfig,
            onSessionCreated = this.onSessionCreated,
            onSessionSelected = this.onSessionSelected,
            onSessionExpired = this.onSessionExpired,
            onSessionRefreshed = this.onSessionRefreshed
        )
    }

    @Throws(TurnkeyKotlinError.FailedToGetAuthProxyConfig::class)
    private suspend fun getAuthProxyConfig(): ProxyTGetWalletKitConfigResponse? {
        try {
            if (config.authProxyConfigId.isNullOrEmpty() || !config.autoFetchWalletKitConfig) return null

            val client = awaitClient()
            return withContext(io) {
                client.proxyGetWalletKitConfig(
                    input = ProxyTGetWalletKitConfigBody()
                )
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToGetAuthProxyConfig(t)
        }
    }

    /**
     * Attempts to restore a previously selected session from secure storage.
     *
     * This method loads the previously selected session (if any) and validates it. If successful,
     * it restores the session as the active session and updates the authentication state to
     * [AuthState.authenticated]. If the session is invalid or doesn't exist, it resets to
     * [AuthState.unauthenticated] and clears the selection.
     *
     * Called automatically during SDK initialization to restore user sessions across app restarts.
     *
     * @param context Android context for accessing secure storage
     * @return true if a valid session was restored, false otherwise
     * @throws TurnkeyKotlinError if an error occurs during session restoration
     */
    @Throws(TurnkeyKotlinError::class)
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
            scheduleExpiryTimer(sessionKey, dto.expiry, session = dto)
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

    /**
     * Creates a new Turnkey HTTP client instance.
     *
     * This is a low-level method for creating HTTP client instances. If no stamper is provided,
     * the client will be unauthenticated and can only make auth proxy API calls.
     *
     * @param cfg configuration to use for the client
     * @param stamper optional stamper for signing requests (if null, creates an unauthenticated client)
     * @param organizationId optional organization ID to override the one in cfg
     * @return a new TurnkeyClient instance configured with the provided parameters
     */
    fun createTurnkeyClient(
        cfg: TurnkeyConfig,
        stamper: Stamper? = null,
        organizationId: String? = null
    ): TurnkeyClient {
        return TurnkeyClient(
            organizationId = organizationId ?: cfg.organizationId,
            apiBaseUrl = cfg.apiBaseUrl,
            authProxyUrl = cfg.authProxyBaseUrl,
            authProxyConfigId = cfg.authProxyConfigId,
            stamper = stamper,
            http = http
        )
    }

    /**
     * Recreates expiry timers for all stored sessions.
     *
     * This method is called automatically during SDK initialization to restore session expiry
     * timers after a process restart. It iterates through all stored sessions and reschedules
     * their expiry/refresh timers based on their expiration timestamps.
     *
     * @param context Android context for accessing secure storage
     * @throws TurnkeyKotlinError.FailedToRescheduleSessionExpiries if rescheduling fails
     */
    @Throws(TurnkeyKotlinError.FailedToRescheduleSessionExpiries::class)
    suspend fun rescheduleAllSessionExpiries(context: Context) = withContext(Dispatchers.IO) {
        try {
            SessionRegistryStore.all(context).forEach { key ->
                JwtSessionStore.load(context, key)?.let { dto ->
                    scheduleExpiryTimer(key, dto.expiry, session = dto)
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRescheduleSessionExpiries(t)
        }
    }

    /**
     * Schedules a one-shot timer that automatically handles session expiry.
     *
     * When the timer fires, it will either:
     * - Automatically refresh the session if an auto-refresh duration was configured, or
     * - Clear the session and invoke the onSessionExpired callback if no auto-refresh is configured
     *
     * The timer fires slightly before the actual expiry time (controlled by bufferSeconds) to
     * ensure seamless session refresh before expiration.
     *
     * If a timer already exists for this session key, it will be cancelled and replaced.
     *
     * @param sessionKey session key to schedule an expiry timer for
     * @param expTimestampSeconds expiry timestamp in seconds since epoch
     * @param bufferSeconds how many seconds before expiry to fire the timer (default 5s)
     * @param session the session object associated with the sessionKey
     * @throws TurnkeyKotlinError.FailedToScheduleExpiryTimer if timer scheduling fails
     */
    @Throws(TurnkeyKotlinError.FailedToScheduleExpiryTimer::class)
    suspend fun scheduleExpiryTimer(
        sessionKey: String,
        expTimestampSeconds: Double,
        bufferSeconds: Double = 5.0,
        session: Session
    ) {
        try {
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
                    } catch (t: Throwable) {
                        config.onSessionExpired?.invoke(session)
                        clearSession(sessionKey)
                        throw TurnkeyKotlinError.FailedToRefreshSession(t)
                    }
                } else {
                    config.onSessionExpired?.let { it(session) }
                    clearSession(sessionKey)
                }
            }

            expiryJobs[sessionKey] = job
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToScheduleExpiryTimer(sessionKey, t)
        }
    }

    /**
     * Persists all storage artifacts for a session and schedules its expiry timer.
     *
     * This method stores the session JWT, registers the session in the session registry,
     * validates that the session's key pair exists in secure storage, and optionally
     * configures auto-refresh behavior. Finally, it schedules the session expiry timer.
     *
     * @param dto session object to persist
     * @param sessionKey key under which to store the session
     * @param refreshedSessionTTLSeconds if present, configures auto-refresh with this TTL duration
     * @throws TurnkeyKotlinError.FailedToPersistSession if persistence fails
     * @throws TurnkeyStorageError.KeyNotFound if the session's key pair is not found in secure storage
     */
    @Throws(TurnkeyKotlinError.FailedToPersistSession::class)
    suspend fun persistSession(
        dto: Session, sessionKey: String, refreshedSessionTTLSeconds: String? = null
    ) = withContext(Dispatchers.IO) {
        try {
            JwtSessionStore.save(appContext, sessionKey, dto)
            SessionRegistryStore.add(appContext, sessionKey)

            // Ensure key material is present
            val hasKeyPair = Stamper.hasOnDeviceKeyPair(publicKey = dto.publicKey)
            if (!hasKeyPair) throw TurnkeyStorageError.KeyNotFound()
            PendingKeysStore.remove(appContext, dto.publicKey)

            if (refreshedSessionTTLSeconds != null) {
                AutoRefreshStore.set(appContext, sessionKey, refreshedSessionTTLSeconds)
            }

            scheduleExpiryTimer(sessionKey, dto.expiry, session = dto)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToPersistSession(t)
        }
    }

    /**
     * Removes all stored artifacts for a session without affecting in-memory state.
     *
     * This low-level method purges session data from secure storage including:
     * - The session JWT
     * - The associated key pair
     * - The session registry entry
     * - Optionally, the auto-refresh configuration
     *
     * This method does NOT update in-memory state like authState or selectedSessionKey.
     * For a complete session clearing that updates UI state, use [clearSession] instead.
     *
     * @param context Android context for accessing secure storage
     * @param sessionKey session key to purge
     * @param keepAutoRefresh if true, preserves auto-refresh duration; if false, removes it
     * @throws TurnkeyKotlinError.FailedToPurgeSession if purging fails
     */
    @Throws(TurnkeyKotlinError.FailedToPurgeSession::class)
    suspend fun purgeStoredSession(
        context: Context, sessionKey: String, keepAutoRefresh: Boolean
    ) = withContext(Dispatchers.IO) {
        try {
            expiryJobs.remove(sessionKey)?.cancel()

            JwtSessionStore.load(context, sessionKey)?.let { dto ->
                runCatching { Stamper.deleteOnDeviceKeyPair(context, dto.publicKey) }
            }

            JwtSessionStore.delete(context, sessionKey)
            runCatching { SessionRegistryStore.remove(context, sessionKey) }

            if (!keepAutoRefresh) runCatching { AutoRefreshStore.remove(context, sessionKey) }

        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToPurgeSession(t)
        }
    }

    /**
     * Updates an existing session with a fresh JWT token.
     *
     * This method replaces an existing session's JWT while preserving the auto-refresh
     * configuration. It's used internally during session refresh to swap out expired tokens.
     *
     * The method validates that a session exists under the given key before proceeding.
     * It purges the old session data (while keeping auto-refresh settings), decodes the new JWT,
     * and persists the updated session.
     *
     * @param context Android context for accessing secure storage
     * @param jwt new JWT string to replace the current session token
     * @param sessionKey session key to update (defaults to default session key)
     * @throws TurnkeyKotlinError.FailedToUpdateSession if the update fails
     * @throws TurnkeyStorageError.KeyNotFound if no session exists under the given key
     */
    @Throws(TurnkeyKotlinError.FailedToUpdateSession::class)
    suspend fun updateSession(
        context: Context, jwt: String, sessionKey: String = SessionStorage.DEFAULT_SESSION_KEY
    ) = withContext(Dispatchers.IO) {
        try {
            // Ensure a session already exists under this key
            val exists = JwtSessionStore.load(context, sessionKey) != null
            if (!exists) throw TurnkeyStorageError.KeyNotFound()

            // Remove old artifacts but keep auto-refresh
            purgeStoredSession(context, sessionKey, keepAutoRefresh = true)

            val dto = JwtDecoder.decode(jwt, Session::class as Json) as Session
            val nextDuration = AutoRefreshStore.durationSeconds(context, sessionKey)
            persistSession(dto, sessionKey, nextDuration)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToUpdateSession(t)
        }
    }

    /**
     * Refreshes the current user's information from the Turnkey API.
     *
     * Fetches the latest user data for the currently authenticated session and updates
     * the [user] StateFlow. Requires an active authenticated session.
     *
     * @throws TurnkeyKotlinError.FailedToRefreshUsers if the refresh fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     */
    @Throws(TurnkeyKotlinError.FailedToRefreshUsers::class)
    suspend fun refreshUser() = coroutineScope {
        try {
            val organizationId =
                session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession()
            val userId = session.value?.userId ?: throw TurnkeyKotlinError.InvalidSession()
            val res = client.getUser(TGetUserBody(organizationId, userId))
            _user.value = res.user
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRefreshUsers(t)
        }
    }

    /**
     * Refreshes the current user's wallet information from the Turnkey API.
     *
     * Fetches the latest wallet and wallet account data for the currently authenticated
     * session's organization and updates the [wallets] StateFlow. Requires an active
     * authenticated session.
     *
     * @throws TurnkeyKotlinError.FailedToRefreshWallets if the refresh fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     */
    @Throws(TurnkeyKotlinError.FailedToRefreshWallets::class)
    suspend fun refreshWallets() = coroutineScope {
        try {
            val organizationId =
                session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession()
            val wallets = client.getWallets(TGetWalletsBody(organizationId))
            val walletAccounts = Helpers.fetchAllWalletAccountsWithCursor(client, organizationId)

            _wallets.value = Helpers.mapAccountsToWallet(walletAccounts, wallets.wallets)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRefreshWallets(t)
        }
    }

    /**
     * Clears a session from both storage and in-memory state.
     *
     * This method removes all traces of a session including:
     * - All stored artifacts (JWT, key pair, registry entry, auto-refresh config)
     * - If clearing the selected session, resets in-memory state (authState, client, user, wallets)
     * - Cancels the session's expiry timer
     *
     * If the session being cleared is the currently selected session, the SDK will reset to
     * an unauthenticated state with a public client.
     *
     * @param sessionKey optional session key to clear; if null, clears the currently selected session
     * @throws TurnkeyKotlinError.FailedToClearSession if clearing fails
     * @throws IllegalArgumentException if sessionKey is null and no session is currently selected
     */
    @Throws(TurnkeyKotlinError::class)
    suspend fun clearSession(sessionKey: String? = null) {
        try {
            val key = sessionKey ?: selectedSessionKey.value
            ?: throw IllegalArgumentException("No session key found to clear")

            try {
                purgeStoredSession(appContext, sessionKey = key, keepAutoRefresh = false)
            } catch (t: Throwable) {
                Log.e("Turnkey SDK Error", "Failed to purge stored session: $t")
            }

            // If we cleared the selected session, reset in-memory state
            if (selectedSessionKey.value == key) {
                _authState.value = AuthState.unauthenticated
                _selectedSessionKey.value = null
                _client = createTurnkeyClient(runtimeConfig)
                _user.value = null
                _session.value = null
                _wallets.value = null
                SelectedSessionStore.delete(appContext)
            }

        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToClearSession(t)
        }
    }

    /**
     * Clears all sessions stored in the session registry.
     *
     * This method iterates through all stored sessions and clears each one, effectively
     * logging out all users and resetting the SDK to an unauthenticated state.
     *
     * @throws TurnkeyKotlinError.FailedToClearAllSessions if clearing fails
     */
    @Throws(TurnkeyKotlinError.FailedToClearAllSessions::class)
    suspend fun clearAllSessions() {
        try {
            val keys = SessionRegistryStore.all(appContext)
            for (k in keys) {
                clearSession(k)
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToClearAllSessions(t)
        }
    }

    /**
     * Creates a new P-256 key pair, stores it securely, and marks it as pending.
     *
     * The generated key pair is stored in the device's secure keystore. The public key is marked
     * as "pending" until it's associated with a session, at which point it's unmarked. Pending
     * keys that are never used can be cleaned up with [deleteUnusedKeyPairs].
     *
     * @return the public key in compressed hexadecimal format
     * @throws TurnkeyKotlinError.FailedToCreateKeyPair if key pair generation or storage fails
     */
    @Throws(TurnkeyKotlinError.FailedToCreateKeyPair::class)
    fun createKeyPair(): String {
        try {
            val pubKeyCompressed = Stamper.createOnDeviceKeyPair()
            PendingKeysStore.add(appContext, pubKeyCompressed)
            return pubKeyCompressed
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToCreateKeyPair(t)
        }
    }

    /**
     * Deletes a key pair from secure storage.
     *
     * Removes the key pair associated with the given public key from the device's secure keystore.
     * Use with caution - deleting a key pair that's still in use by an active session will make
     * that session unusable.
     *
     * @param publicKey the public key (compressed hex) identifying the key pair to delete
     * @throws TurnkeyKotlinError.FailedToDeleteKeyPair if deletion fails
     */
    @Throws(TurnkeyKotlinError.FailedToDeleteKeyPair::class)
    fun deleteKeyPair(publicKey: String) {
        try {
            Stamper.deleteOnDeviceKeyPair(publicKey = publicKey)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToDeleteKeyPair(publicKey, t)
        }
    }

    /**
     * Deletes all key pairs that are not referenced by any active session.
     *
     * This cleanup method identifies key pairs in secure storage that are not associated with
     * any stored session and removes them. This is useful for cleaning up orphaned keys from
     * failed authentication attempts or deleted sessions.
     *
     * Called automatically after passkey authentication flows to clean up temporary keys.
     *
     * @throws TurnkeyKotlinError.FailedToDeleteUnusedKeyPairs if cleanup fails
     */
    @Throws(TurnkeyKotlinError.FailedToDeleteUnusedKeyPairs::class)
    fun deleteUnusedKeyPairs() {
        try {
            val storedKeys: List<String> = Stamper.listOnDeviceKeyPairs()
            if (storedKeys.isEmpty()) return

            // Build a set of all public keys currently used by sessions
            val sessionKeys: List<String> = SessionRegistryStore.all(appContext)
            val usedKeys: Set<String> =
                sessionKeys.mapNotNull { sKey -> JwtSessionStore.load(appContext, sKey)?.publicKey }
                    .toSet()

            for (pk in storedKeys) {
                if (pk !in usedKeys) {
                    deleteKeyPair(pk)
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToDeleteUnusedKeyPairs(t)
        }
    }

    /**
     * Creates a new session from a JWT token.
     *
     * This method decodes the JWT, validates it, stores it securely, and optionally sets up
     * auto-refresh. If this is the first session created, it automatically becomes the selected
     * session. The session's expiry timer is scheduled automatically.
     *
     * After creation, the onSessionCreated callback (if configured) will be invoked.
     *
     * @param jwt the session JWT string returned from a login or sign-up flow
     * @param sessionKey optional key under which to store the session (defaults to default session key)
     * @param refreshedSessionTTLSeconds optional auto-refresh TTL in seconds (minimum 30 seconds)
     * @return the newly created Session object
     * @throws TurnkeyKotlinError.FailedToCreateSession if session creation fails
     * @throws TurnkeyKotlinError.KeyAlreadyExists if a session with this key already exists
     * @throws TurnkeyKotlinError.InvalidRefreshTTL if refreshedSessionTTLSeconds is less than 30
     */
    @Throws(TurnkeyKotlinError.FailedToCreateSession::class)
    suspend fun createSession(
        jwt: String, sessionKey: String? = null, refreshedSessionTTLSeconds: String? = null
    ): Session {
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
            config.onSessionCreated?.let { it(s) }
            return s
        } catch (error: Throwable) {
            throw TurnkeyKotlinError.FailedToCreateSession(error)
        }
    }

    /**
     * Sets the currently active session.
     *
     * This method loads the session from storage, creates an authenticated client with the
     * session's key pair, and updates all in-memory state (authState, client, session, user, wallets).
     * If auto-refresh is enabled in the config, user and wallet data are fetched automatically.
     *
     * After selection, the onSessionSelected callback (if configured) will be invoked.
     *
     * @param sessionKey the session key to make active
     * @return the authenticated TurnkeyClient instance for this session
     * @throws TurnkeyKotlinError.FailedToSetSelectedSession if selection fails
     * @throws TurnkeyKotlinError.KeyNotFound if no session exists with the given key
     */
    @Throws(TurnkeyKotlinError.FailedToSetSelectedSession::class)
    suspend fun setSelectedSession(sessionKey: String): TurnkeyClient {
        try {
            val dto = JwtSessionStore.load(appContext, sessionKey)
                ?: throw TurnkeyKotlinError.KeyNotFound(sessionKey)

            val cli = createTurnkeyClient(
                config,
                Stamper.fromPublicKey(dto.publicKey),
                organizationId = dto.organizationId,
            )

            withContext(Dispatchers.Main) {
                SelectedSessionStore.save(appContext, sessionKey)
                _selectedSessionKey.value = sessionKey
                _client = cli
                _session.value = dto
                _authState.value = AuthState.authenticated
            }

            if (config.autoRefreshManagedStates) {
                coroutineScope {
                    val userRefreshDeferred = async { refreshUser() }
                    val walletsRefreshDeferred = async { refreshWallets() }
                    awaitAll(userRefreshDeferred, walletsRefreshDeferred)
                }
            }

            config.onSessionSelected?.let { it(dto) }
            return cli
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSetSelectedSession(t)
        }
    }

    /**
     * Refreshes a session by obtaining a new JWT with an extended expiration.
     *
     * This method generates a new ephemeral key pair, calls the Turnkey stamp login endpoint
     * to get a fresh JWT, and replaces the existing session's JWT while preserving auto-refresh
     * configuration. If refreshing the currently selected session, the in-memory client is also
     * updated with the new key pair.
     *
     * After refresh, the onSessionRefreshed callback (if configured) will be invoked.
     *
     * @param expirationSeconds the new expiration duration in seconds for the refreshed session
     * @param sessionKey optional session key to refresh; if null, refreshes the currently selected session
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @throws TurnkeyKotlinError.FailedToRefreshSession if refresh fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists to refresh
     * @throws TurnkeyKotlinError.KeyNotFound if the specified session key doesn't exist
     */
    @Throws(TurnkeyKotlinError.FailedToRefreshSession::class)
    suspend fun refreshSession(
        expirationSeconds: String = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false
    ) {
        val targetKey = sessionKey ?: selectedSessionKey.value ?: SessionStorage.DEFAULT_SESSION_KEY

        // Use current client if this is the selected session; else build one from stored material
        val (clientToUse, orgId) = if (targetKey == selectedSessionKey.value) {
            val curClient = _client ?: throw TurnkeyKotlinError.InvalidSession()
            val curSession = session.value ?: throw TurnkeyKotlinError.InvalidSession()
            curClient to curSession.organizationId
        } else {
            val dto =
                JwtSessionStore.load(appContext, targetKey) ?: throw TurnkeyKotlinError.KeyNotFound(
                    targetKey
                )
            val cli = createTurnkeyClient(
                config,
                Stamper.fromPublicKey(dto.publicKey),
                organizationId = dto.organizationId
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

            val jwt = resp.result.session

            // Swap the stored session contents (preserve auto-refresh duration)
            updateSession(appContext, jwt = jwt, sessionKey = targetKey)

            // If this was the selected session, replace the client in memory
            val updatedSession = JwtSessionStore.load(appContext, targetKey)!!

            if (targetKey == selectedSessionKey.value) {
                val newClient = createTurnkeyClient(
                    config,
                    Stamper.fromPublicKey(updatedSession.publicKey),
                    organizationId = updatedSession.organizationId
                )

                withContext(Dispatchers.Main) {
                    _client = newClient
                }
            }
            config.onSessionRefreshed?.let { it(updatedSession) }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRefreshSession(t)
        }
    }

    /**
     * Logs in a user using an OAuth provider's OIDC token.
     *
     * This method authenticates an existing user with their OAuth provider's OIDC token.
     * The user must have previously signed up with the OAuth provider. A new session is
     * created and stored locally.
     *
     * For new users, use [signUpWithOAuth] instead. To handle both cases automatically,
     * use [loginOrSignUpWithOAuth].
     *
     * @param oidcToken the OIDC token obtained from the OAuth provider
     * @param publicKey the public key (compressed hex) for the new session's key pair
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @param sessionKey optional key under which to store the session
     * @param organizationId optional organization ID to log in to (for multi-org scenarios)
     * @return LoginWithOAuthResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToLoginWithOAuth if login fails
     */
    @Throws(TurnkeyKotlinError.FailedToLoginWithOAuth::class)
    suspend fun loginWithOAuth(
        oidcToken: String,
        publicKey: String,
        invalidateExisting: Boolean = false,
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

    /**
     * Signs up a new user using an OAuth provider's OIDC token.
     *
     * This method creates a new Turnkey sub-organization for the user and authenticates them
     * with their OAuth provider's OIDC token. After successful sign-up, the user is automatically
     * logged in and a session is created.
     *
     * For existing users, use [loginWithOAuth] instead. To handle both cases automatically,
     * use [loginOrSignUpWithOAuth].
     *
     * @param oidcToken the OIDC token obtained from the OAuth provider
     * @param publicKey the public key (compressed hex) for the new session's key pair
     * @param providerName the OAuth provider name (e.g., "google", "apple", "facebook", "x", "discord")
     * @param createSubOrgParams optional sub-organization creation parameters (overrides config defaults)
     * @param sessionKey optional key under which to store the session
     * @return SignUpWithOAuthResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToSignUpWithOAuth if sign-up fails
     * @throws TurnkeyKotlinError.SignUpFailed if the server doesn't return an organization ID
     */
    @Throws(TurnkeyKotlinError.FailedToSignUpWithOAuth::class)
    suspend fun signUpWithOAuth(
        oidcToken: String,
        publicKey: String,
        providerName: String,
        createSubOrgParams: CreateSubOrgParams? = null,
        sessionKey: String? = null
    ): SignUpWithOAuthResult {
        val overrideParams = OAuthOverrideParams(
            providerName, oidcToken
        )
        val updatedCreateSubOrgParams = Helpers.getCreateSubOrgParams(
            createSubOrgParams, runtimeConfig, overrideParams
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
                oidcToken = oidcToken, publicKey = publicKey, sessionKey = sessionKey
            )

            return SignUpWithOAuthResult(sessionJwt = loginRes.sessionJwt)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignUpWithOAuth(t)
        }
    }

    /**
     * Automatically logs in an existing user or signs up a new user with OAuth.
     *
     * This convenience method checks if an account exists for the given OIDC token. If an account
     * exists, it logs in the user. If no account exists, it signs up a new user (providerName is
     * required in this case).
     *
     * This is the recommended method for OAuth authentication when you don't know if the user
     * is new or returning.
     *
     * @param oidcToken the OIDC token obtained from the OAuth provider
     * @param publicKey the public key (compressed hex) for the new session's key pair
     * @param providerName optional OAuth provider name (required for sign-up, e.g., "google", "apple")
     * @param sessionKey optional key under which to store the session
     * @param invalidateExisting if true, invalidates all other sessions for this user (login only)
     * @param createSubOrgParams optional sub-organization creation parameters (sign-up only)
     * @return LoginOrSignUpWithOAuthResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToLoginOrSignUpWithOAuth if the operation fails
     * @throws TurnkeyKotlinError.SignUpFailed if providerName is null during sign-up
     */
    @Throws(TurnkeyKotlinError.FailedToLoginOrSignUpWithOAuth::class)
    suspend fun loginOrSignUpWithOAuth(
        oidcToken: String,
        publicKey: String,
        providerName: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false,
        createSubOrgParams: CreateSubOrgParams? = null,
    ): LoginOrSignUpWithOAuthResult {
        try {
            val accountRes = client.proxyGetAccount(
                input = ProxyTGetAccountBody(
                    filterType = "OIDC_TOKEN", filterValue = oidcToken
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
                    oidcToken, publicKey, invalidateExisting, sessionKey
                )
                return LoginOrSignUpWithOAuthResult(sessionJwt = loginRes.sessionJwt)
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginOrSignUpWithOAuth(t)
        }
    }

    /**
     * Logs in a user using a passkey (WebAuthn).
     *
     * This method authenticates an existing user with their registered passkey. The Android
     * system UI will prompt the user to select and verify their passkey (e.g., with biometrics).
     * After successful authentication, a new session is created.
     *
     * For new users, use [signUpWithPasskey] instead.
     *
     * @param activity the current Android activity for displaying the passkey UI
     * @param sessionKey optional key under which to store the session
     * @param expirationSeconds optional expiration duration in seconds for the session
     * @param organizationId optional organization ID to log in to (defaults to config organizationId)
     * @param publicKey optional public key for the session; if null, a new one is generated
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @param rpId optional relying party ID; if null, uses the value from config
     * @return LoginWithPasskeyResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToLoginWithPasskey if login fails
     * @throws TurnkeyKotlinError.MissingRpId if rpId is not provided and not configured
     */
    @Throws(TurnkeyKotlinError.FailedToLoginWithPasskey::class)
    suspend fun loginWithPasskey(
        activity: Activity,
        sessionKey: String? = null,
        expirationSeconds: String? = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        organizationId: String? = null,
        publicKey: String? = null,
        invalidateExisting: Boolean = false,
        rpId: String? = null,
    ): LoginWithPasskeyResult {
        val sessionKey = sessionKey ?: SessionStorage.DEFAULT_SESSION_KEY
        val organizationId = organizationId ?: config.organizationId
        try {
            val generatedPublicKey = publicKey ?: createKeyPair()
            val passkeyStamper = Stamper.fromPasskey(activity, rpId)

            val passkeyClient = TurnkeyClient(
                apiBaseUrl = config.apiBaseUrl,
                stamper = passkeyStamper,
                organizationId = organizationId,
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

    /**
     * Signs up a new user using a passkey (WebAuthn).
     *
     * This method creates a new Turnkey sub-organization for the user and registers their
     * passkey. The Android system UI will prompt the user to create a new passkey (e.g., with
     * biometric registration). After successful sign-up, the user is automatically logged in
     * and a session is created.
     *
     * For existing users, use [loginWithPasskey] instead.
     *
     * @param activity the current Android activity for displaying the passkey UI
     * @param sessionKey optional key under which to store the session
     * @param expirationSeconds optional expiration duration in seconds for the session
     * @param passkeyDisplayName optional display name for the passkey (defaults to "passkey-{timestamp}")
     * @param createSubOrgParams optional sub-organization creation parameters (overrides config defaults)
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @param rpId optional relying party ID; if null, uses the value from config
     * @return SignUpWithPasskeyResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToSignUpWithPasskey if sign-up fails
     * @throws TurnkeyKotlinError.MissingRpId if rpId is not provided and not configured
     * @throws TurnkeyKotlinError.SignUpFailed if the server doesn't return an organization ID
     */
    @Throws(TurnkeyKotlinError.FailedToSignUpWithPasskey::class)
    suspend fun signUpWithPasskey(
        activity: Activity,
        sessionKey: String? = null,
        expirationSeconds: String? = SessionStorage.DEFAULT_EXPIRATION_SECONDS,
        passkeyDisplayName: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        invalidateExisting: Boolean = false,
        rpId: String? = null
    ): SignUpWithPasskeyResult {
        val sessionKey = sessionKey ?: SessionStorage.DEFAULT_SESSION_KEY
        val rpId = rpId ?: config.authConfig?.rpId ?: throw TurnkeyKotlinError.MissingRpId()
        val generatedPublicKey: String?
        var temporaryPublicKey: String?

        try {
            temporaryPublicKey = createKeyPair()
            _client = createTurnkeyClient(config, stamper = Stamper.fromPublicKey(temporaryPublicKey))
            val passkeyName = passkeyDisplayName ?: "passkey-${Date().time}"

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
                Helpers.getCreateSubOrgParams(createSubOrgParams, runtimeConfig, overrideParams)

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

    /**
     * Initializes an OTP (One-Time Password) for a given contact.
     *
     * This is the first step in OTP authentication. It sends an OTP code to the user's
     * email address or phone number. The user must then verify the code using [verifyOtp]
     * before they can log in or sign up.
     *
     * @param otpType the type of OTP (e.g., OtpType.OTP_TYPE_EMAIL, OtpType.OTP_TYPE_SMS)
     * @param contact the contact information (email address or phone number)
     * @return InitOtpResult containing the OTP ID to use for verification
     * @throws TurnkeyKotlinError.FailedToInitOtp if OTP initialization fails
     */
    @Throws(TurnkeyKotlinError.FailedToInitOtp::class)
    suspend fun initOtp(
        otpType: OtpType, contact: String
    ): InitOtpResult {
        try {
            val res = client.proxyInitOtp(
                ProxyTInitOtpBody(
                    contact = contact, otpType = otpType.name
                )
            )
            return InitOtpResult(otpId = res.otpId)
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToInitOtp(t)
        }
    }

    /**
     * Verifies an OTP code.
     *
     * This is the second step in OTP authentication. It verifies the code that was sent to
     * the user's contact. If verification succeeds, a verification token is returned which
     * can be used to log in or sign up.
     *
     * @param otpCode the OTP code entered by the user
     * @param otpId the OTP ID returned from [initOtp]
     * @param publicKey optional public key for the session; if null, a new one is generated
     * @return VerifyOtpResult containing the verification token for login/sign-up
     * @throws TurnkeyKotlinError.FailedToVerifyOtp if verification fails
     * @throws TurnkeyKotlinError.InvalidResponse if the server doesn't return a verification token
     */
    @Throws(TurnkeyKotlinError.FailedToVerifyOtp::class)
    suspend fun verifyOtp(
        otpCode: String, otpId: String, publicKey: String? = null
    ): VerifyOtpResult {
        try {
            val resolvedPublicKey = publicKey ?: createKeyPair()

            val verifyOtpRes = client.proxyVerifyOtp(
                ProxyTVerifyOtpBody(
                    otpId, otpCode, resolvedPublicKey
                )
            )
            if (verifyOtpRes.verificationToken.isEmpty()) {
                throw TurnkeyKotlinError.InvalidResponse(
                    """
        OTP verification succeeded but the server did not return a verification token.
        
        This is likely a temporary server issue. Please try:
        1. Retry the OTP verification
        2. If the issue persists, contact Turnkey support with this error
        
        Debug info: otpId=$otpId
        """.trimIndent()
                )
            }

            return VerifyOtpResult(
                verificationToken = verifyOtpRes.verificationToken
            )
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToVerifyOtp(t)
        }
    }

    /**
     * Logs in a user using an OTP verification token.
     *
     * This method authenticates an existing user with the verification token obtained from
     * [verifyOtp]. The user must have previously signed up. A new session is created and
     * stored locally.
     *
     * For new users, use [signUpWithOtp] instead. To handle both cases automatically,
     * use [loginOrSignUpWithOtp].
     *
     * @param verificationToken the verification token from [verifyOtp]
     * @param organizationId optional organization ID to log in to (for multi-org scenarios)
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @param publicKey optional public key for the session; if null, a new one is generated
     * @param sessionKey optional key under which to store the session
     * @return LoginWithOtpResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToLoginWithOtp if login fails
     */
    @Throws(TurnkeyKotlinError.FailedToLoginWithOtp::class)
    suspend fun loginWithOtp(
        verificationToken: String,
        organizationId: String? = null,
        invalidateExisting: Boolean = false,
        publicKey: String? = null,
        sessionKey: String? = null,
    ): LoginWithOtpResult {
        try {
            val sessionPublicKey = publicKey ?: createKeyPair()

            val (message, clientSignaturePublicKey) = ClientSignature.forLogin(
                verificationToken, sessionPublicKey
            )

            val stamper = Stamper.fromPublicKey(clientSignaturePublicKey)
            val signature = stamper.sign(payload = message, format = SignatureFormat.raw)

            val clientSignature = V1ClientSignature(
                message = message,
                publicKey = clientSignaturePublicKey,
                scheme = V1ClientSignatureScheme.CLIENT_SIGNATURE_SCHEME_API_P256,
                signature = signature
            )

            val res = client.proxyOtpLogin(
                ProxyTOtpLoginBody(
                    organizationId = organizationId,
                    publicKey = sessionPublicKey,
                    verificationToken = verificationToken,
                    invalidateExisting = invalidateExisting,
                    clientSignature = clientSignature
                )
            )

            createSession(res.session, sessionKey)

            return LoginWithOtpResult(sessionJwt = res.session)
        } catch (t: Throwable) {
            deleteUnusedKeyPairs()
            throw TurnkeyKotlinError.FailedToLoginWithOtp(t)
        }
    }

    /**
     * Signs up a new user using an OTP verification token.
     *
     * This method creates a new Turnkey sub-organization for the user and authenticates them
     * with the verification token obtained from [verifyOtp]. After successful sign-up, the
     * user is automatically logged in and a session is created.
     *
     * For existing users, use [loginWithOtp] instead. To handle both cases automatically,
     * use [loginOrSignUpWithOtp].
     *
     * @param verificationToken the verification token from [verifyOtp]
     * @param contact the contact information (email or phone number) used for OTP
     * @param otpType the type of OTP used (EMAIL or SMS)
     * @param publicKey optional public key for the session; if null, a new one is generated
     * @param sessionKey optional key under which to store the session
     * @param createSubOrgParams optional sub-organization creation parameters (overrides config defaults)
     * @param invalidateExisting if true, invalidates all other sessions for this user
     * @return SignUpWithOtpResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToSignUpWithOtp if sign-up fails
     * @throws TurnkeyKotlinError.SignUpFailed if the server doesn't return an organization ID
     */
    @Throws(TurnkeyKotlinError.FailedToSignUpWithOtp::class)
    suspend fun signUpWithOtp(
        verificationToken: String,
        contact: String,
        otpType: OtpType,
        publicKey: String? = null,
        sessionKey: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null,
        invalidateExisting: Boolean = false
    ): SignUpWithOtpResult {
        val overrideParams = OtpOverrireParams(
            otpType = otpType, contact = contact, verificationToken = verificationToken
        )

        val updatedCreateSubOrgParams =
            Helpers.getCreateSubOrgParams(createSubOrgParams, runtimeConfig, overrideParams)

        // build sign up body without client signature first
        var signUpBody = Helpers.buildSignUpBody(updatedCreateSubOrgParams)

        val (message, clientSignaturePublicKey) = ClientSignature.forSignUp(
            verificationToken = verificationToken,
            email = signUpBody.userEmail,
            phoneNumber = signUpBody.userPhoneNumber,
            apiKeys = signUpBody.apiKeys,
            authenticators = signUpBody.authenticators,
            oauthProviders = signUpBody.oauthProviders
        )

        val stamper = Stamper.fromPublicKey(clientSignaturePublicKey)
        val signature = stamper.sign(payload = message, format = SignatureFormat.raw)

        val clientSignature = V1ClientSignature(
            message = message,
            publicKey = clientSignaturePublicKey,
            scheme = V1ClientSignatureScheme.CLIENT_SIGNATURE_SCHEME_API_P256,
            signature = signature
        )

        // add client signature to sign up body
        signUpBody = ProxyTSignupBody(
            apiKeys = signUpBody.apiKeys,
            authenticators = signUpBody.authenticators,
            oauthProviders = signUpBody.oauthProviders,
            organizationName = signUpBody.organizationName,
            userEmail = signUpBody.userEmail,
            userPhoneNumber = signUpBody.userPhoneNumber,
            userName = signUpBody.userName,
            userTag = signUpBody.userTag,
            verificationToken = signUpBody.verificationToken,
            wallet = signUpBody.wallet,
            clientSignature = clientSignature
        )

        try {
            val res = client.proxySignup(signUpBody)
            val orgId = res.organizationId

            if (orgId.isEmpty()) {
                throw TurnkeyKotlinError.SignUpFailed(
                    """
        Sign up request succeeded but no organization ID was returned.
        
        This indicates an issue with the Turnkey service. Please:
        1. Wait a moment and try signing up again
        2. If this error persists, contact Turnkey support
        
        """.trimIndent()
                )
            }

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

    /**
     * Automatically logs in an existing user or signs up a new user with OTP.
     *
     * This convenience method verifies the OTP code and checks if an account exists for the
     * given contact. If an account exists, it logs in the user. If no account exists, it signs
     * up a new user.
     *
     * This is the recommended method for OTP authentication when you don't know if the user
     * is new or returning. It combines [verifyOtp] with the login/sign-up logic.
     *
     * @param otpId the OTP ID returned from [initOtp]
     * @param otpCode the OTP code entered by the user
     * @param contact the contact information (email or phone number)
     * @param otpType the type of OTP (EMAIL or SMS)
     * @param publicKey optional public key for the session; if null, a new one is generated
     * @param invalidateExisting if true, invalidates all other sessions for this user (login only)
     * @param sessionKey optional key under which to store the session
     * @param createSubOrgParams optional sub-organization creation parameters (sign-up only)
     * @return LoginOrSignUpWithOtpResult containing the session JWT
     * @throws TurnkeyKotlinError.FailedToLoginOrSignUpWithOtp if the operation fails
     */
    @Throws(TurnkeyKotlinError.FailedToLoginOrSignUpWithOtp::class)
    suspend fun loginOrSignUpWithOtp(
        otpId: String,
        otpCode: String,
        contact: String,
        otpType: OtpType,
        publicKey: String? = null,
        invalidateExisting: Boolean = false,
        sessionKey: String? = null,
        createSubOrgParams: CreateSubOrgParams? = null
    ): LoginOrSignUpWithOtpResult {
        try {
            val resolvedPublicKey = publicKey ?: createKeyPair()

            val verifyRes = verifyOtp(
                otpCode = otpCode,
                otpId = otpId,
                publicKey = resolvedPublicKey,
            )

            val accountRes = client.proxyGetAccount(
                ProxyTGetAccountBody(
                    filterType = otpTypeToFilterTypeMap.getValue(otpType).name,
                    filterValue = contact,
                    verificationToken = verifyRes.verificationToken
                )
            )
            val subOrganizationId = accountRes.organizationId

            if (subOrganizationId.isNullOrEmpty()) {
                val signUpRes = signUpWithOtp(
                    verificationToken = verifyRes.verificationToken,
                    contact = contact,
                    otpType = otpType,
                    publicKey = resolvedPublicKey,
                    sessionKey = sessionKey,
                    createSubOrgParams = createSubOrgParams,
                    invalidateExisting = invalidateExisting
                )

                return LoginOrSignUpWithOtpResult(sessionJwt = signUpRes.sessionJwt)
            } else {
                val loginRes = loginWithOtp(
                    verificationToken = verifyRes.verificationToken,
                    organizationId = subOrganizationId,
                    invalidateExisting = invalidateExisting,
                    publicKey = resolvedPublicKey,
                    sessionKey = sessionKey
                )
                return LoginOrSignUpWithOtpResult(sessionJwt = loginRes.sessionJwt)
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToLoginOrSignUpWithOtp(t)
        }
    }

    /**
     * Handles the complete Google OAuth flow including redirect and authentication.
     *
     * This high-level method orchestrates the entire Google OAuth flow:
     * 1. Opens a Chrome Custom Tab with the Google OAuth consent screen
     * 2. Waits for the OAuth redirect with the OIDC token
     * 3. Either invokes your custom onSuccess callback or automatically logs in/signs up the user
     *
     * The method suspends until the OAuth flow completes or times out.
     *
     * @param activity the current Android activity for displaying the OAuth UI
     * @param clientId optional Google Client ID; if null, uses the configured value
     * @param originUri optional OAuth origin URL (defaults to Turnkey's OAuth endpoint)
     * @param redirectUri optional redirect URI; if null, uses the configured value
     * @param sessionKey optional key under which to store the session (when using default behavior)
     * @param invalidateExisting if true, invalidates all other sessions (when using default behavior)
     * @param createSubOrgParams optional sub-organization creation parameters (when using default behavior)
     * @param onSuccess optional callback for handling the OIDC token manually; if null, uses default login/sign-up
     * @param timeoutMinutes how long to wait for the OAuth redirect before timing out (default: 10 minutes)
     * @throws TurnkeyKotlinError.FailedToHandleGoogleOAuth if the OAuth flow fails
     * @throws TurnkeyKotlinError.MissingConfigParam if required configuration is missing
     * @throws TurnkeyKotlinError.InvalidResponse if the redirect is missing the id_token
     */
    @Throws(TurnkeyKotlinError.FailedToHandleGoogleOAuth::class)
    suspend fun handleGoogleOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = Turnkey.OAUTH_ORIGIN_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((oidcToken: String, publicKey: String, providerName: String) -> Unit)? = null,
        timeoutMinutes: Long = 10
    ) {
        val scheme = runtimeConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val googleClientId = clientId ?: runtimeConfig.authConfig?.oAuthConfig?.googleClientId
        ?: throw TurnkeyKotlinError.MissingConfigParam("Google Client ID not configured")

        val resolvedRedirect =
            redirectUri ?: runtimeConfig.authConfig?.oAuthConfig?.oauthRedirectUri
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
                OAuthEvents.deepLinks.filter { it.scheme.equals(scheme, ignoreCase = true) }.first()
            }

            val idToken = uri.getQueryParameter("id_token")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing id_token in redirect")

            withContext(Dispatchers.IO) {
                if (onSuccess != null) {
                    onSuccess(idToken, targetPublicKey, "google")
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

    /**
     * Handles the complete Apple OAuth flow including redirect and authentication.
     *
     * This high-level method orchestrates the entire Apple Sign In flow:
     * 1. Opens a Chrome Custom Tab with the Apple Sign In screen
     * 2. Waits for the OAuth redirect with the OIDC token
     * 3. Either invokes your custom onSuccess callback or automatically logs in/signs up the user
     *
     * The method suspends until the OAuth flow completes or times out.
     *
     * @param activity the current Android activity for displaying the OAuth UI
     * @param clientId optional Apple Client ID; if null, uses the configured value
     * @param originUri optional OAuth origin URL (defaults to Apple's auth endpoint)
     * @param redirectUri optional redirect URI; if null, uses the configured value
     * @param sessionKey optional key under which to store the session (when using default behavior)
     * @param invalidateExisting if true, invalidates all other sessions (when using default behavior)
     * @param createSubOrgParams optional sub-organization creation parameters (when using default behavior)
     * @param onSuccess optional callback for handling the OIDC token manually; if null, uses default login/sign-up
     * @param timeoutMinutes how long to wait for the OAuth redirect before timing out (default: 10 minutes)
     * @throws TurnkeyKotlinError.FailedToHandleAppleOAuth if the OAuth flow fails
     * @throws TurnkeyKotlinError.MissingConfigParam if required configuration is missing
     * @throws TurnkeyKotlinError.InvalidResponse if the redirect is missing the id_token
     */
    @Throws(TurnkeyKotlinError.FailedToHandleAppleOAuth::class)
    suspend fun handleAppleOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.APPLE_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((oidcToken: String, publicKey: String, providerName: String) -> Unit)? = null,
        timeoutMinutes: Long = 10
    ) {
        val scheme = runtimeConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val appleClientId = clientId ?: runtimeConfig.authConfig?.oAuthConfig?.appleClientId
        ?: throw TurnkeyKotlinError.MissingConfigParam("Apple Client ID not configured")

        val resolvedRedirect =
            redirectUri ?: runtimeConfig.authConfig?.oAuthConfig?.oauthRedirectUri
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
                OAuthEvents.deepLinks.filter { it.scheme.equals(scheme, ignoreCase = true) }.first()
            }

            val idToken = uri.getQueryParameter("id_token")
                ?: throw TurnkeyKotlinError.InvalidResponse("Missing id_token in redirect")

            withContext(Dispatchers.IO) {
                if (onSuccess != null) {
                    onSuccess(idToken, targetPublicKey, "apple")
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

    /**
     * Handles the complete X (formerly Twitter) OAuth flow including redirect and authentication.
     *
     * This high-level method orchestrates the entire X OAuth2 flow with PKCE:
     * 1. Generates a code challenge for PKCE
     * 2. Opens a Chrome Custom Tab with the X authorization screen
     * 3. Waits for the OAuth redirect with the authorization code
     * 4. Exchanges the code for an OIDC token
     * 5. Either invokes your custom onSuccess callback or automatically logs in/signs up the user
     *
     * The method suspends until the OAuth flow completes or times out.
     *
     * @param activity the current Android activity for displaying the OAuth UI
     * @param clientId optional X Client ID; if null, uses the configured value
     * @param originUri optional OAuth origin URL (defaults to X's auth endpoint)
     * @param redirectUri optional redirect URI; if null, uses the configured value
     * @param sessionKey optional key under which to store the session (when using default behavior)
     * @param invalidateExisting if true, invalidates all other sessions (when using default behavior)
     * @param createSubOrgParams optional sub-organization creation parameters (when using default behavior)
     * @param onSuccess optional callback for handling the OIDC token manually; if null, uses default login/sign-up
     * @param timeoutMinutes how long to wait for the OAuth redirect before timing out (default: 10 minutes)
     * @throws TurnkeyKotlinError.FailedToHandleXOAuth if the OAuth flow fails
     * @throws TurnkeyKotlinError.MissingConfigParam if required configuration is missing
     * @throws TurnkeyKotlinError.OAuthStateMismatch if the state parameter doesn't match
     * @throws TurnkeyKotlinError.InvalidResponse if the redirect is missing the authorization code
     */
    @Throws(TurnkeyKotlinError.FailedToHandleXOAuth::class)
    suspend fun handleXOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.X_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((oidcToken: String, publicKey: String, providerName: String) -> Unit)? = null,
        timeoutMinutes: Long = 10,
    ) {
        val scheme = runtimeConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val xClientId = clientId ?: runtimeConfig.authConfig?.oAuthConfig?.xClientId
        ?: throw TurnkeyKotlinError.MissingConfigParam("X Client ID not configured")

        val resolvedRedirect =
            redirectUri ?: runtimeConfig.authConfig?.oAuthConfig?.oauthRedirectUri ?: "$scheme://"

        val challengePair = Helpers.generateChallengePair()

        val state = UUID.randomUUID().toString()

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
                OAuthEvents.deepLinks.filter { it.scheme.equals(scheme, ignoreCase = true) }.first()
            }

            if (uri.getQueryParameter("state") != state) {
                throw TurnkeyKotlinError.OAuthStateMismatch("Could not complete X OAuth")
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
                    onSuccess(oidcToken, targetPublicKey, "x")
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

    /**
     * Handles the complete Discord OAuth flow including redirect and authentication.
     *
     * This high-level method orchestrates the entire Discord OAuth2 flow with PKCE:
     * 1. Generates a code challenge for PKCE
     * 2. Opens a Chrome Custom Tab with the Discord authorization screen
     * 3. Waits for the OAuth redirect with the authorization code
     * 4. Exchanges the code for an OIDC token
     * 5. Either invokes your custom onSuccess callback or automatically logs in/signs up the user
     *
     * The method suspends until the OAuth flow completes or times out.
     *
     * @param activity the current Android activity for displaying the OAuth UI
     * @param clientId optional Discord Client ID; if null, uses the configured value
     * @param originUri optional OAuth origin URL (defaults to Discord's auth endpoint)
     * @param redirectUri optional redirect URI; if null, uses the configured value
     * @param sessionKey optional key under which to store the session (when using default behavior)
     * @param invalidateExisting if true, invalidates all other sessions (when using default behavior)
     * @param createSubOrgParams optional sub-organization creation parameters (when using default behavior)
     * @param onSuccess optional callback for handling the OIDC token manually; if null, uses default login/sign-up
     * @param timeoutMinutes how long to wait for the OAuth redirect before timing out (default: 10 minutes)
     * @throws TurnkeyKotlinError.FailedToHandleDiscordOAuth if the OAuth flow fails
     * @throws TurnkeyKotlinError.MissingConfigParam if required configuration is missing
     * @throws TurnkeyKotlinError.OAuthStateMismatch if the state parameter doesn't match
     * @throws TurnkeyKotlinError.InvalidResponse if the redirect is missing the authorization code
     */
    @Throws(TurnkeyKotlinError.FailedToHandleDiscordOAuth::class)
    suspend fun handleDiscordOAuth(
        activity: Activity,
        clientId: String? = null,
        originUri: String = OAuth.DISCORD_AUTH_URL,
        redirectUri: String? = null,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false,
        createSubOrgParams: CreateSubOrgParams? = null,
        onSuccess: ((oidcToken: String, publicKey: String, providerName: String) -> Unit)? = null,
        timeoutMinutes: Long = 10,
    ) {
        val scheme = runtimeConfig.appScheme
            ?: throw TurnkeyKotlinError.MissingConfigParam("App scheme is not configured. Set `appScheme` in TurnkeyConfig.")

        val targetPublicKey = createKeyPair() // returns public key string (p-256)
        val nonce = Helpers.sha256Hex(targetPublicKey)

        val discordClientId = clientId ?: runtimeConfig.authConfig?.oAuthConfig?.discordClientId
        ?: throw TurnkeyKotlinError.MissingConfigParam("Discord Client ID not configured")

        val resolvedRedirect =
            redirectUri ?: runtimeConfig.authConfig?.oAuthConfig?.oauthRedirectUri ?: "$scheme://"

        val challengePair = Helpers.generateChallengePair()

        val state = UUID.randomUUID().toString()

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
                OAuthEvents.deepLinks.filter { it.scheme.equals(scheme, ignoreCase = true) }.first()
            }

            if (uri.getQueryParameter("state") != state) {
                throw TurnkeyKotlinError.OAuthStateMismatch("Could not complete Discord OAuth")
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
                    onSuccess(oidcToken, targetPublicKey, "discord")
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

    /**
     * Creates a new HD wallet with a generated mnemonic.
     *
     * This method creates a new hierarchical deterministic (HD) wallet for the current user's
     * organization. The wallet is generated with a random mnemonic phrase of the specified length.
     * Multiple accounts can be derived from the wallet using the provided account parameters.
     *
     * If auto-refresh is enabled in config, the wallets StateFlow is automatically updated.
     *
     * @param walletName the name for the new wallet
     * @param accounts list of account derivation paths and parameters for the wallet
     * @param mnemonicLength the length of the mnemonic phrase (typically 12 or 24 words)
     * @return V1CreateWalletResult containing the wallet ID and addresses
     * @throws TurnkeyKotlinError.FailedToCreateWallet if wallet creation fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     */
    @Throws(TurnkeyKotlinError.FailedToCreateWallet::class)
    suspend fun createWallet(
        walletName: String, accounts: List<V1WalletAccountParams>, mnemonicLength: Long
    ): V1CreateWalletResult {
        try {
            val organizationId =
                session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession()
            val res = client.createWallet(
                TCreateWalletBody(
                    organizationId = organizationId,
                    accounts = accounts,
                    walletName = walletName,
                    mnemonicLength = mnemonicLength
                )
            )

            if (config.autoRefreshManagedStates) refreshWallets()

            return res.result
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToCreateWallet(t)
        }
    }

    /**
     * Signs a raw payload using a wallet account's private key.
     *
     * This low-level signing method allows you to sign arbitrary payloads with full control
     * over encoding and hashing. For message signing with sensible defaults, use [signMessage] instead.
     *
     * @param signWith the wallet account address to sign with
     * @param payload the raw payload to sign (as a string)
     * @param encoding the encoding format of the payload (e.g., PAYLOAD_ENCODING_HEXADECIMAL)
     * @param hashFunction the hash function to apply (e.g., HASH_FUNCTION_KECCAK256)
     * @return V1SignRawPayloadResult containing the signature
     * @throws TurnkeyKotlinError.FailedToSignRawPayload if signing fails
     */
    @Throws(TurnkeyKotlinError.FailedToSignRawPayload::class)
    suspend fun signRawPayload(
        signWith: String, payload: String, encoding: V1PayloadEncoding, hashFunction: V1HashFunction
    ): V1SignRawPayloadResult {
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
            return res.result
        } catch (e: Throwable) {
            throw TurnkeyKotlinError.FailedToSignRawPayload(e)
        }
    }

    /**
     * Signs a message using a wallet account with sensible defaults.
     *
     * This convenience method signs a human-readable message with appropriate encoding and
     * hashing defaults based on the blockchain. For Ethereum addresses, it optionally adds
     * the standard Ethereum signed message prefix.
     *
     * For full control over encoding and hashing, use [signRawPayload] instead.
     *
     * @param signWith the wallet account address to sign with
     * @param addressFormat the address format of the wallet account (determines defaults)
     * @param message the message to sign (as a UTF-8 string)
     * @param encoding optional encoding override; if null, uses default for addressFormat
     * @param hashFunction optional hash function override; if null, uses default for addressFormat
     * @param addEthereumPrefix if true and addressFormat is Ethereum, prepends "\x19Ethereum Signed Message:\n{len}"
     * @return V1SignRawPayloadResult containing the signature
     * @throws TurnkeyKotlinError.FailedToSignMessage if signing fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     */
    @Throws(TurnkeyKotlinError.FailedToSignMessage::class)
    suspend fun signMessage(
        signWith: String,
        addressFormat: V1AddressFormat,
        message: String,
        encoding: V1PayloadEncoding? = null,
        hashFunction: V1HashFunction? = null,
        addEthereumPrefix: Boolean = true
    ): V1SignRawPayloadResult {
        val defaults = Helpers.defaultsFor(addressFormat)
        val finalEncoding = encoding ?: defaults.encoding
        val finalHash = hashFunction ?: defaults.hashFunction

        var messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        if (addressFormat == V1AddressFormat.ADDRESS_FORMAT_ETHEREUM) {
            val shouldPrefix = addEthereumPrefix
            if (shouldPrefix) messageBytes = Helpers.ethereumPrefixed(messageBytes)
        }

        val payload = Helpers.encodeMessageBytes(messageBytes, finalEncoding)

        try {
            val res = client.signRawPayload(
                TSignRawPayloadBody(
                    organizationId = session.value?.organizationId
                        ?: throw TurnkeyKotlinError.InvalidSession(),
                    encoding = finalEncoding,
                    hashFunction = finalHash,
                    payload = payload,
                    signWith = signWith
                )
            )
            return res.result
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSignMessage(t)
        }
    }

    /**
     * Imports an existing HD wallet using a mnemonic phrase.
     *
     * This method allows users to import a wallet they already own by providing the mnemonic
     * phrase. The mnemonic is encrypted before being sent to Turnkey's servers. Multiple
     * accounts can be derived from the wallet using the provided account parameters.
     *
     * If auto-refresh is enabled in config, the wallets StateFlow is automatically updated.
     *
     * @param walletName the name for the imported wallet
     * @param mnemonic the BIP-39 mnemonic phrase (12 or 24 words)
     * @param accounts list of account derivation paths and parameters for the wallet
     * @return V1ImportWalletResult containing the wallet ID and addresses
     * @throws TurnkeyKotlinError.FailedToImportWallet if import fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     * @throws TurnkeyKotlinError.InvalidResponse if the server response is malformed
     */
    @Throws(TurnkeyKotlinError.FailedToImportWallet::class)
    suspend fun importWallet(
        walletName: String, mnemonic: String, accounts: List<V1WalletAccountParams>
    ): V1ImportWalletResult {
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession()
        val userId = session.value?.userId ?: throw TurnkeyKotlinError.InvalidSession()
        try {
            val initRes = client.initImportWallet(
                TInitImportWalletBody(
                    organizationId = organizationId, userId = userId
                )
            )

            val importBundle = initRes.result.importBundle

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

    /**
     * Exports a wallet and returns its mnemonic phrase.
     *
     * This method allows users to export their wallet's mnemonic phrase for backup or
     * migration purposes. The export is encrypted in transit using a temporary key pair.
     * The decrypted mnemonic is returned directly - handle it securely!
     *
     * **Security Warning**: The mnemonic phrase provides complete control over the wallet.
     * Never log it, store it insecurely, or transmit it over unencrypted channels.
     *
     * @param walletId the ID of the wallet to export
     * @return ExportWalletResult containing the decrypted mnemonic phrase
     * @throws TurnkeyKotlinError.FailedToExportWallet if export fails
     * @throws TurnkeyKotlinError.InvalidSession if no valid session exists
     */
    @Throws(TurnkeyKotlinError.FailedToExportWallet::class)
    suspend fun exportWallet(
        walletId: String,
    ): ExportWalletResult {
        val (targetPublicKey, _, embeddedPriv) = generateP256KeyPair()
        val organizationId =
            session.value?.organizationId ?: throw TurnkeyKotlinError.InvalidSession()

        try {
            val res = client.exportWallet(
                TExportWalletBody(
                    organizationId = organizationId,
                    targetPublicKey = targetPublicKey,
                    walletId = walletId
                )
            )

            val bundle = res.result.exportBundle

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