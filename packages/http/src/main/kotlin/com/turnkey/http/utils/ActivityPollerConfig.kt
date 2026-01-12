package com.turnkey.http.utils

data class ActivityPollerConfig(
    val intervalMs: Long = 1000L,
    val numRetries: Int = 3,
)