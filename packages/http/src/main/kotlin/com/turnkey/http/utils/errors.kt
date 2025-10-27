package com.turnkey.http.utils

sealed class TurnkeyHttpErrors (message: String, cause: Throwable? = null): Exception(message, cause) {
    data object MissingAuthProxyConfigId: TurnkeyHttpErrors("Missing authProxyConfigId, please initialize the TurnkeyClient with the proper auth proxy params")
    data object StamperNotInitialized: TurnkeyHttpErrors("No stampers found, please initialized a stamper and pass it into the client.")
    data class EmptyResponseBody(val url: String): TurnkeyHttpErrors("Empty response body from $url")
}