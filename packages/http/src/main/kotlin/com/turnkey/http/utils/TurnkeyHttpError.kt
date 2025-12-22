package com.turnkey.http.utils

sealed class TurnkeyHttpError (message: String, cause: Throwable? = null): Exception(
    if (cause != null) "$message - error: ${cause.message}" else message,
    cause
){
    data class MissingAuthProxyConfigId (override val cause: Throwable? = null): TurnkeyHttpError("Missing authProxyConfigId, please initialize the TurnkeyClient with the proper auth proxy params", cause)
    data class StamperNotInitialized (override val cause: Throwable? = null): TurnkeyHttpError("No stampers found, please initialized a stamper and pass it into the client.", cause)
    data class EmptyResponseBody(val url: String, override val cause: Throwable? = null): TurnkeyHttpError("Empty response body from $url", cause)
    data class OperationFailed(override val message: String, override val cause: Throwable): TurnkeyHttpError(message, cause)

    companion object {
        fun wrap (s: String, t: Throwable): TurnkeyHttpError {
            return when (t) {
                is TurnkeyHttpError -> t
                else -> OperationFailed(s, t)
            }
        }
    }
}