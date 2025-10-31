package com.turnkey.internal.storage.primitives

import android.content.Context
import android.content.SharedPreferences
import com.turnkey.models.StorageError
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object LocalStore {
    private const val PREFS_NAME = "turnkey.local.store"

    @PublishedApi
    internal fun prefs(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @PublishedApi
    internal val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Throws(StorageError.KeyEncodingFailed::class)
    inline fun <reified T> set(context: Context, key: String, value: T) {
        try {
            val blob = json.encodeToString(serializer<T>(), value)
            prefs(context).edit().putString(key, blob).apply()
        } catch (t: Throwable) {
            throw StorageError.KeyEncodingFailed(key, t)
        }
    }

    @Throws(StorageError.DecodingFailed::class)
    inline fun <reified T> get(context: Context, key: String): T? {
        val blob = prefs(context).getString(key, null) ?: return null
        return try {
            json.decodeFromString(serializer<T>(), blob)
        } catch (t: Throwable) {
            throw StorageError.KeyDecodingFailed(key, t)
        }
    }

    fun listKeys(
        context: Context,
    ): List<String> = try {
        prefs(context).all.keys
            .asSequence()
            .toList()
    } catch (t: Throwable) {
        throw StorageError.KeychainListKeysFailed(-1)
    }

    fun delete(context: Context, key: String) {
        prefs(context).edit().remove(key).apply()
    }
}
