package com.turnkey.core

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.turnkey.http.TGetUserBody
import com.turnkey.http.TGetWalletsBody
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
import com.turnkey.models.SessionUser
import com.turnkey.models.Storage
import com.turnkey.models.StorageError
import com.turnkey.models.TurnkeyConfig
import com.turnkey.models.TurnkeySession
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
import com.turnkey.http.TStampLoginBody
import com.turnkey.stamper.Stamper
import okhttp3.OkHttpClient

class TurnkeyContext(
    val appContext: Context,
    val config: TurnkeyConfig
) {
    private val _authState = MutableStateFlow(AuthState.loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _client = MutableStateFlow<TurnkeyClient?>(null)
    val client: StateFlow<TurnkeyClient?> = _client.asStateFlow()

    private val _selectedSessionKey = MutableStateFlow<String?>(null)
    val selectedSessionKey: StateFlow<String?> = _selectedSessionKey.asStateFlow()

    private val _user = MutableStateFlow<SessionUser?>(null)
    val user: StateFlow<SessionUser?> = _user.asStateFlow()

    val okHttpClient = OkHttpClient()

    //    Internal state
    private val expiryJobs = ConcurrentHashMap<String, Job>()
    private val timerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun registerForegroundObserver(onEnterForeground: () -> Unit) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                onEnterForeground()
            }
        })
    }

    suspend fun init() {
        withContext(Dispatchers.IO) {
            SessionRegistryStore.purgeExpiredSessions(appContext)
            PendingKeysStore.purge(appContext)
        }

        withContext(Dispatchers.Main) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    // Don’t block main; do purges on IO
                    CoroutineScope(Dispatchers.IO).launch {
                        PendingKeysStore.purge(appContext)
                        SessionRegistryStore.purgeExpiredSessions(appContext)
                    }
                }
            })

            rescheduleAllSessionExpiries(appContext)
            restoreSelectedSession(appContext)
        }
    }

    /**
     * Attempt to restore a previously selected session.
     * @return true if a valid selected session exists; false otherwise (and clears selection).
     */
    suspend fun restoreSelectedSession(context: Context) = withContext(Dispatchers.IO) {
        try {
            val sessionKey = SelectedSessionStore.load(context, null)
            if (sessionKey == null) {
                SelectedSessionStore.delete(context, null)
                // update state
                _selectedSessionKey.value = null
                _authState.value = AuthState.unauthenticated
                _client.value = TurnkeyClient(
                    apiBaseUrl = config.apiBaseUrl,
                    authProxyUrl = config.authProxyBaseUrl,
                    authProxyConfigId = config.authProxyConfigId,
                    stamper = null,
                    http = OkHttpClient()
                )
                return@withContext
            }

            val dto = JwtSessionStore.load(context, sessionKey)
            if (dto == null) {
                // Selected session missing/expired → clear and set unauthenticated
                SelectedSessionStore.delete(context, null)
                _selectedSessionKey.value = null
                _authState.value = AuthState.unauthenticated
                _client.value = TurnkeyClient(
                    apiBaseUrl = config.apiBaseUrl,
                    authProxyUrl = config.authProxyBaseUrl,
                    authProxyConfigId = config.authProxyConfigId,
                    stamper = null,
                    http = OkHttpClient()
                )
                return@withContext
            }

            scheduleExpiryTimer(sessionKey, dto.exp)
            _selectedSessionKey.value = sessionKey
            _authState.value = AuthState.authenticated

            runCatching { setSelectedSession(sessionKey) }.onFailure {
                _selectedSessionKey.value = null
                _authState.value = AuthState.unauthenticated
                _client.value = TurnkeyClient(
                    apiBaseUrl = config.apiBaseUrl,
                    authProxyUrl = config.authProxyBaseUrl,
                    authProxyConfigId = config.authProxyConfigId,
                    stamper = null,
                    http = OkHttpClient()
                )
            }

        } catch (_: Throwable) {
            // On any error, fall back to unauthenticated
            _selectedSessionKey.value = null
            _authState.value = AuthState.unauthenticated
            _client.value = TurnkeyClient(
                apiBaseUrl = config.apiBaseUrl,
                authProxyUrl = config.authProxyBaseUrl,
                authProxyConfigId = config.authProxyConfigId,
                stamper = null,
                http = OkHttpClient()
            )
        }
    }


    /**
     * Recreate expiry timers for all stored sessions (e.g., after process restart).
     */
    suspend fun rescheduleAllSessionExpiries(context: Context) = withContext(Dispatchers.IO) {
        try {
            SessionRegistryStore.all(context).forEach { key ->
                JwtSessionStore.load(context, key)?.let { dto ->
                    scheduleExpiryTimer(key, dto.exp)
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
        dto: TurnkeySession,
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

        scheduleExpiryTimer(sessionKey, dto.exp)
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
        expiryJobs.remove(sessionKey)?.cancel()

        JwtSessionStore.load(context, sessionKey)?.let { dto ->
            runCatching { KeyPairStore.delete(context, dto.publicKey) }
        }

        JwtSessionStore.delete(context, sessionKey)
        runCatching { SessionRegistryStore.remove(context, sessionKey) }

        if (!keepAutoRefresh) {
            runCatching { AutoRefreshStore.remove(context, sessionKey) }
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
        sessionKey: String = Storage.SELECTED_SESSION_KEY
    ) = withContext(Dispatchers.IO) {
        // Ensure a session already exists under this key
        val exists = JwtSessionStore.load(context, sessionKey) != null
        if (!exists) throw StorageError.KeyNotFound

        // Remove old artifacts but keep auto-refresh
        purgeStoredSession(context, sessionKey, keepAutoRefresh = true)

        val dto = JwtDecoder.decode(jwt, TurnkeySession::class as Json) as TurnkeySession
        val nextDuration = AutoRefreshStore.durationSeconds(context, sessionKey)
        persistSession(dto, sessionKey, nextDuration)
    }

    /**
     * Fetch a rich SessionUser (user + wallets + accounts)
     */
    suspend fun fetchSessionUser(
        client: TurnkeyClient,
        organizationId: String,
        userId: String
    ): SessionUser = coroutineScope {
        require(organizationId.isNotBlank() && userId.isNotBlank()) {
            "Invalid org/user id"
        }
        // Run user + wallets in parallel
        val userDeferred = async { client.getUser(TGetUserBody(organizationId, userId)) }
        val walletsDeferred = async { client.getWallets(TGetWalletsBody(organizationId)) }

        val userResp = userDeferred.await()
        val walletsResp = walletsDeferred.await()

        val user = userResp.user
        val wallets = walletsResp.wallets

        val walletAccountsDeferred =
            async { Helpers.fetchAllWalletAccountsWithCursor(client, organizationId) }
        val walletAccountsResp = walletAccountsDeferred.await()

        val detailedWallets = Helpers.mapAccountsToWallet(walletAccountsResp, wallets)

        return@coroutineScope SessionUser(
            id = user.userId,
            userName = user.userName,
            email = user.userEmail,
            phoneNumber = user.userPhoneNumber,
            organizationId = organizationId,
            wallets = detailedWallets
        )
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
            _client.value = null
            _user.value = null
            SelectedSessionStore.delete(appContext, null)
        }
    }

    fun createKeyPair(): String {
        val (_, pubKeyCompressed, privKey) = generateP256KeyPair()
        KeyPairStore.save(appContext, privKey, pubKeyCompressed)
        PendingKeysStore.add(appContext, pubKeyCompressed)
        return pubKeyCompressed
    }

    suspend fun createSession(
        jwt: String,
        sessionKey: String = Session.DEFAULT_SESSION_KEY,
        refreshedSessionTTLSeconds: String? = null
    ) {
        try {
            // eventually we should verify that the jwt was signed by Turnkey
            // but for now we just assume it is

            refreshedSessionTTLSeconds?.toIntOrNull()?.let { ttl ->
                if (ttl < 30) throw TurnkeyKotlinError.InvalidRefreshTTL("Minimum allowed TTL is 30 seconds.")
            }

            // Ensure no existing session under same key
            val existingKey = JwtSessionStore.load(appContext, sessionKey)
            if (existingKey != null) {
                throw TurnkeyKotlinError.KeyAlreadyExists(sessionKey)
            }

            val dto = JwtDecoder.decode<TurnkeySession>(jwt)

            persistSession(
                dto = dto,
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
            println("DTO: $dto")

            val privHex = KeyPairStore.getPrivateHex(appContext, dto.publicKey)
            val cli = TurnkeyClient(
                apiBaseUrl = config.apiBaseUrl,
                stamper = Stamper(apiPublicKey = dto.publicKey, apiPrivateKey = privHex),
                http = okHttpClient,
                authProxyUrl = config.authProxyBaseUrl,
                authProxyConfigId = config.authProxyConfigId
            )

            val fetched = fetchSessionUser(
                client = cli,
                organizationId = dto.organizationId,
                userId = dto.userId
            )

            withContext(Dispatchers.Main) {
                SelectedSessionStore.save(appContext, null, sessionKey)
                _selectedSessionKey.value = sessionKey
                _client.value = cli
                _user.value = fetched
            }

            return cli
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToSetSelectedSession(t)
        }
    }

    @Throws(TurnkeyKotlinError::class)
    suspend fun refreshSession(
        expirationSeconds: String = Session.DEFAULT_EXPIRATION_SECONDS,
        sessionKey: String? = null,
        invalidateExisting: Boolean = false
    ) {
        val targetKey = sessionKey ?: selectedSessionKey.value ?: Session.DEFAULT_SESSION_KEY

        // Use current client if this is the selected session; else build one from stored material
        val (clientToUse, orgId) = if (targetKey == selectedSessionKey.value) {
            val curClient = client.value ?: throw TurnkeyKotlinError.InvalidSession
            val curUser = user.value ?: throw TurnkeyKotlinError.InvalidSession
            curClient to curUser.organizationId
        } else {
            val dto =
                JwtSessionStore.load(appContext, targetKey) ?: throw TurnkeyKotlinError.KeyNotFound(
                    targetKey
                )
            val privHex = KeyPairStore.getPrivateHex(appContext, dto.publicKey)
            val cli = TurnkeyClient(
                apiBaseUrl = config.apiBaseUrl,
                stamper = Stamper(apiPublicKey = dto.publicKey, apiPrivateKey = privHex),
                http = okHttpClient,
                authProxyUrl = config.authProxyBaseUrl,
                authProxyConfigId = config.authProxyConfigId
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

            // Pull the JWT out from the response payload (update this path to match your DTOs)
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

                val newClient = TurnkeyClient(
                    apiBaseUrl = config.apiBaseUrl,
                    stamper = Stamper(apiPublicKey = updated.publicKey, apiPrivateKey = privHex),
                    http = okHttpClient,
                    authProxyUrl = config.authProxyBaseUrl,
                    authProxyConfigId = config.authProxyConfigId
                )

                withContext(Dispatchers.Main) {
                    _client.value = newClient
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyKotlinError.FailedToRefreshSession(t)
        }
    }
}