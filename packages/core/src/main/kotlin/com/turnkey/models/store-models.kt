package com.turnkey.models

import android.content.Context

interface KeyValueStore<K, V> {
    @Throws(StorageError::class) fun save(context: Context, key: K, value: V)
    @Throws(StorageError::class) fun load(context: Context, key: K): V?
    fun delete(context: Context, key: K)
}