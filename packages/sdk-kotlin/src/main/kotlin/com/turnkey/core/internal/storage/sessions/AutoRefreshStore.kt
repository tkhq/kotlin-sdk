package com.turnkey.core.internal.storage.sessions

import android.content.Context
import com.turnkey.core.internal.storage.primitives.LocalStore
import com.turnkey.core.models.errors.TurnkeyStorageError
import com.turnkey.core.models.Storage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object AutoRefreshStore {
    private val lock = ReentrantReadWriteLock()

    /**
     * Persist an auto-refresh duration (in seconds, as a String) for the given session key.
     */
    @Throws(TurnkeyStorageError::class)
    fun set(context: Context, sessionKey: String, durationSeconds: String) = lock.write {
        val current: Map<String, String> =
            LocalStore.get<Map<String, String>>(context, Storage.AUTO_REFRESH_STORE_KEY) ?: emptyMap()
        val next = current.toMutableMap()
        next[sessionKey] = durationSeconds
        LocalStore.set(context, Storage.AUTO_REFRESH_STORE_KEY, next)
    }

    /**
     * Remove any stored auto-refresh duration for the given session key.
     */
    @Throws(TurnkeyStorageError::class)
    fun remove(context: Context, sessionKey: String) = lock.write {
        val current: Map<String, String> =
            LocalStore.get<Map<String, String>>(context, Storage.AUTO_REFRESH_STORE_KEY) ?: emptyMap()
        if (sessionKey in current) {
            val next = current.toMutableMap()
            next.remove(sessionKey)
            LocalStore.set(context, Storage.AUTO_REFRESH_STORE_KEY, next)
        }
    }

    /**
     * Fetch the stored duration (seconds, as a String) for a session key, or null if absent.
     */
    fun durationSeconds(context: Context, sessionKey: String): String? = lock.read {
        val current: Map<String, String> =
            LocalStore.get<Map<String, String>>(context, Storage.AUTO_REFRESH_STORE_KEY) ?: emptyMap()
        current[sessionKey]
    }
}