package com.turnkey.internal.storage.sessions

import android.content.Context
import com.turnkey.internal.storage.primitives.LocalStore
import com.turnkey.models.KeyValueStore
import com.turnkey.models.Storage
import com.turnkey.models.StorageError

/**
 * Stores and retrieves the session key of the currently selected session.
 * Persists active session across app launches
 */
object SelectedSessionStore: KeyValueStore<String?, String> {
    @Throws(StorageError::class)
    override fun save(context: Context, key: String?, value: String) {
        val k = key ?: Storage.SELECTED_SESSION_KEY
        LocalStore.set(context, k, value)
    }

    @Throws(StorageError::class)
    override fun load(context: Context, key: String? ): String? {
        val k = key ?: Storage.SELECTED_SESSION_KEY
        return LocalStore.get(context, k)
    }

    override fun delete(context: Context, key: String?) {
        val k = key ?: Storage.SELECTED_SESSION_KEY
        LocalStore.delete(context, k)
    }
}