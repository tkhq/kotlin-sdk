package com.turnkey.internal.storage.primitives

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.turnkey.models.StorageError
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureStore {
    private const val KEY_ALIAS = "turnkey.aes.master"
    private const val PREFS_NAME = "turnkey.secure.store"
    private const val IV_LEN = 12 // GCM standard

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        kg.init(spec)
        return kg.generateKey()
    }

    private fun encrypt(plain: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val ct = cipher.doFinal(plain)
        val out = ByteArray(iv.size + ct.size)
        System.arraycopy(iv, 0, out, 0, iv.size)
        System.arraycopy(ct, 0, out, iv.size, ct.size)
        return Base64.encodeToString(out, Base64.NO_WRAP)
    }

    private fun decrypt(b64: String): ByteArray {
        val blob = Base64.decode(b64, Base64.NO_WRAP)
        if (blob.size < IV_LEN + 16) throw StorageError.InvalidCiphertext
        val iv = blob.copyOfRange(0, IV_LEN)
        val ct = blob.copyOfRange(IV_LEN, blob.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(ct) // throws on auth failure
    }

    /**
     * Store data under (service, account).
     * `accessible` and `itemClass` are accepted for parity; ignored on Android.
     */
    @Throws(StorageError::class)
    fun set(
        context: Context,
        data: ByteArray,
        service: String,
        account: String,
    ) {
        // Compose a single key as "<account>::<service>".
        val key = "$account::$service"
        val enc = try { encrypt(data) } catch (t: Throwable) {
            throw StorageError.KeychainAddFailed(-1)
        }
        prefs(context).edit().putString(key, enc).apply()
    }

    /**
     * Get data for (service, account). Returns null if not found.
     */
    @Throws(StorageError::class)
    fun get(
        context: Context,
        service: String,
        account: String,
    ): ByteArray? {
        val key = "$account::$service"
        val b64 = prefs(context).getString(key, null) ?: return null
        return try { decrypt(b64) } catch (t: Throwable) {
            throw StorageError.KeychainFetchFailed(-1)
        }
    }

    /**
     * Delete data for (service, account). No-op if missing.
     */
    @Throws(StorageError::class)
    fun delete(
        context: Context,
        service: String,
        account: String,
    ) {
        val key = "$account::$service"
        val ok = prefs(context).edit().remove(key).commit()
        if (!ok) throw StorageError.KeychainDeleteFailed(-1)
    }
}
