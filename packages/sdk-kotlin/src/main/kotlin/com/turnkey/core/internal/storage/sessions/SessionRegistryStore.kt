package com.turnkey.core.internal.storage.sessions

import android.content.Context
import android.util.Log
import com.turnkey.core.internal.storage.keys.KeyPairStore
import com.turnkey.core.internal.storage.primitives.LocalStore
import com.turnkey.core.models.Session
import com.turnkey.core.models.errors.TurnkeyStorageError
import com.turnkey.core.models.Storage
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Stores a list of all active session keys.
 * Used to track/manage multiple JWT-backed sessions, and to purge expired ones.
 */
object SessionRegistryStore {

    private const val STORE_KEY = Storage.SESSION_REGISTRY_KEY
    private val lock = ReentrantReadWriteLock()

    /**
     * Add a session key to the registry (no-ops if already present).
     */
    @Throws(TurnkeyStorageError::class)
    fun add(context: Context, sessionKey: String) {
        lock.write {
            val list: MutableList<String> =
                (LocalStore.get<List<String>>(context, STORE_KEY) ?: emptyList()).toMutableList()
            if (!list.contains(sessionKey)) {
                list.add(sessionKey)
                LocalStore.set(context, STORE_KEY, list)
            }
        }
    }

    /**
     * Remove a session key from the registry (no-ops if absent).
     */
    @Throws(TurnkeyStorageError::class)
    fun remove(context: Context, sessionKey: String) {
        lock.write {
            val list: MutableList<String> =
                (LocalStore.get<List<String>>(context, STORE_KEY) ?: emptyList()).toMutableList()
            if (list.removeAll { it == sessionKey }) {
                LocalStore.set(context, STORE_KEY, list)
            }
        }
    }

    /**
     * Return the current list of session keys (possibly empty).
     */
    @Throws(TurnkeyStorageError::class)
    fun all(context: Context): List<String> = lock.read {
        LocalStore.get<List<String>>(context, STORE_KEY) ?: emptyList()
    }

    /**
     * Purge expired sessions:
     * - If a session's JWT is expired, delete its stored session, auto-refresh record,
     *   private key material, and remove it from the registry. If it was the selected
     *   session, clear that selection.
     * - If a session key has no stored session metadata, remove it from the registry.
     */
    fun purgeExpiredSessions(
        context: Context,
    ) {
        try {
            val sessionKeys = all(context)
            val selectedSessionKey = try {
                SelectedSessionStore.load(context)
            } catch (_: Throwable) {
                null
            }

            val nowSec = System.currentTimeMillis() / 1000L

            sessionKeys.forEach { sk ->
                val sess: Session? = try {
                    JwtSessionStore.load(context, sk)
                } catch (e: Throwable) {
                    Log.w("SessionRegistryStore", "Failed to load session for $sk: $e")
                    null
                }

                if (sess == null) {
                    // Orphaned key → just remove from registry
                    try {
                        remove(context, sk)
                    } catch (e: Throwable) {
                        Log.w("SessionRegistryStore", "Failed to remove orphaned session key $sk: $e")
                    }
                    return@forEach
                }

                if (sess.expiry <= nowSec) {
                    // Expired → clean up everything
                    try {
                        JwtSessionStore.delete(context, sk)
                    } catch (e: Throwable) {
                        Log.w("SessionRegistryStore", "Failed to delete expired JWT for $sk: $e")
                    }

                    try {
                        AutoRefreshStore.remove(context, sk)
                    } catch (e: Throwable) {
                        Log.w("SessionRegistryStore", "Failed to remove auto-refresh for $sk: $e")
                    }

                    try {
                        KeyPairStore.delete(context, sess.publicKey)
                    } catch (e: Throwable) {
                        Log.w("SessionRegistryStore", "Failed to delete keypair for pub=${sess.publicKey}: $e")
                    }

                    try {
                        remove(context, sk)
                    } catch (e: Throwable) {
                        Log.w("SessionRegistryStore", "Failed to remove expired session key $sk: $e")
                    }

                    if (selectedSessionKey == sk) {
                        try {
                            SelectedSessionStore.delete(context)
                        } catch (e: Throwable) {
                            Log.w("SessionRegistryStore", "Failed to clear selected session: $e")
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("SessionRegistryStore", "purgeExpiredSessions error: $e")
        }
    }
}
