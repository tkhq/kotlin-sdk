package com.turnkey.models

data class TurnkeyConfig (
    /** Public API base URL (required) */
    val apiBaseUrl: String,
    /** Optional auth-proxy base URL + config ID (omit if not used) */
    val authProxyBaseUrl: String? = null,
    val authProxyConfigId: String? = null,
)