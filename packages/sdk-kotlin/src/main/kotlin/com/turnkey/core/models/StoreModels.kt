package com.turnkey.core.models

import android.content.Context
import com.turnkey.core.models.errors.TurnkeyStorageError

interface KeyValueStore<K, V> {
    @Throws(TurnkeyStorageError::class) fun save(context: Context, key: K, value: V)
    @Throws(TurnkeyStorageError::class) fun load(context: Context, key: K): V?
    fun delete(context: Context, key: K)
}

interface ValueStore<V> {
    @Throws(TurnkeyStorageError::class) fun save(context: Context, value: V)
    @Throws(TurnkeyStorageError::class) fun load(context: Context): V?
    fun delete(context: Context)
}