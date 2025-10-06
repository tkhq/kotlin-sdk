package com.turnkey.http.utils

sealed class TurnkeyAuthProxyErrors (message: String, cause: Throwable? = null): Exception(message, cause) {
    data object MissingAuthProxyConfigId: TurnkeyAuthProxyErrors("Missing authProxyConfigId, please initialize the TurnkeyClient with the proper auth proxy params")
}