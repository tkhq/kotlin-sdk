package com.turnkey.core.internal.storage.keys

import android.content.Context
import com.turnkey.core.internal.storage.primitives.SecureStore

object KeyPairStore {
    private const val SECURE_ACCOUNT = "turnkey.secure.account"

    fun save(context: Context, privateHex: String, publicHex: String) {
        SecureStore.set(
            context = context,
            data = privateHex.toByteArray(Charsets.UTF_8),
            service = publicHex,
            account = SECURE_ACCOUNT
        )
    }

    fun getPrivateHex(context: Context, publicHex: String): String {
        val data = SecureStore.get(context, service = publicHex, account = SECURE_ACCOUNT)
            ?: throw IllegalStateException("Key not found")
        return data.toString(Charsets.UTF_8)
    }

    fun listKeys(context: Context): List<String> {
        return SecureStore.listKeys(context, SECURE_ACCOUNT)
    }

    fun delete(context: Context, publicHex: String) {
        SecureStore.delete(context, service = publicHex, account = SECURE_ACCOUNT)
    }

    fun deleteAll(context: Context) {
        SecureStore.deleteAll((context))
    }
}