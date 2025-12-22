package com.turnkey.core.internal.storage.sessions

import android.content.Context
import com.turnkey.core.internal.storage.primitives.LocalStore
import com.turnkey.core.models.KeyValueStore
import com.turnkey.core.models.errors.TurnkeyStorageError
import com.turnkey.core.models.Session

/**
 * Stores and retrieves decoded Turnkey session JWTs by session key.
 * Persists session metadata (org ID, user ID, public key, expiry, etc.)
 * so sessions can be restored/reused across app launches.
 */
object JwtSessionStore: KeyValueStore<String, Session> {

    /**
     * Save a decoded session under a caller-provided key (e.g., the session public key).
     */
    @Throws(TurnkeyStorageError::class)
    override fun save(context: Context, key: String, value: Session) {
        LocalStore.set(context, key, value)
    }

    /**
     * Load a decoded session previously stored under [key], or null if missing.
     */
    @Throws(TurnkeyStorageError::class)
    override fun load(context: Context, key: String): Session? {
        return LocalStore.get(context, key)
    }

    fun listKeys(context: Context): List<String> {
        return LocalStore.listKeys(context)
    }

    /**
     * Remove the stored session (noop if not present).
     */
    override fun delete(context: Context, key: String) {
        LocalStore.delete(context, key)
    }
}