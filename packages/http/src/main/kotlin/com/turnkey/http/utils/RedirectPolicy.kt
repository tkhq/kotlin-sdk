package com.turnkey.http.utils

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private const val MAX_FOLLOW_UPS = 10

internal fun OkHttpClient.withSameOriginRedirects(): OkHttpClient =
    if (SameOriginRedirectInterceptor in interceptors) this else newBuilder()
        .addInterceptor(SameOriginRedirectInterceptor)
        .followRedirects(false)
        .build()

private object SameOriginRedirectInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = chain.request().url
        var response = chain.proceed(chain.request())
        var followUpCount = 0
        while (true) {
            val target = followUpTarget(response, origin) ?: return response
            if (++followUpCount > MAX_FOLLOW_UPS) refuse(response)
            val followUp = response.request.newBuilder().url(target).build()
            response.close()
            response = chain.proceed(followUp)
        }
    }

    private fun followUpTarget(response: Response, origin: HttpUrl): HttpUrl? {
        when (response.code) {
            307, 308 -> Unit
            300, 301, 302, 303 -> refuse(response)
            else -> return null
        }
        val location = response.header("Location") ?: refuse(response)
        val target = response.request.url.resolve(location) ?: refuse(response)
        if (origin.scheme != target.scheme || origin.host != target.host || origin.port != target.port) {
            refuse(response)
        }
        return target
    }

    private fun refuse(response: Response): Nothing {
        val error = TurnkeyHttpError.RedirectRefused(response.code, response.header("Location"))
        response.close()
        throw error
    }
}
