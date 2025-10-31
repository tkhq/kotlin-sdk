package com.turnkey.internal.storage.sessions

import android.content.Context
import com.turnkey.internal.storage.primitives.LocalStore
import com.turnkey.models.Storage
import com.turnkey.models.StorageError
import com.turnkey.models.ValueStore

/**
 * Stores and retrieves the session key of the currently selected session.
 * Persists active session across app launches
 */
object SelectedSessionStore: ValueStore<String> {
    @Throws(StorageError::class)
    override fun save(context: Context, value: String) {
        LocalStore.set(context, Storage.SELECTED_SESSION_KEY, value)
    }

    @Throws(StorageError::class)
    override fun load(context: Context): String? {
        return LocalStore.get(context, Storage.SELECTED_SESSION_KEY)
    }

    override fun delete(context: Context) {
        LocalStore.delete(context, Storage.SELECTED_SESSION_KEY)
    }
}