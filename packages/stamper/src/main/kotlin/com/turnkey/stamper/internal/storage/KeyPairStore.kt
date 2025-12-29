package com.turnkey.stamper.internal.storage

import android.content.Context
import com.turnkey.crypto.generateP256KeyPair

object KeyPairStore {
    private const val SECURE_ACCOUNT = "turnkey.secure.account"

    fun createAndSaveKeyPair(context: Context): String {
        val (_, pubKeyCompressed, privKey) = generateP256KeyPair()
        save(context, privKey, pubKeyCompressed)

        return pubKeyCompressed
    }

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