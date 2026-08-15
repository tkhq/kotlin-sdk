package com.turnkey.http.utils

import java.net.ProtocolException
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private const val MAX_FOLLOW_UPS = 20

internal fun OkHttpClient.withSameOriginRedirects(): OkHttpClient =
    if (!followRedirects) this else newBuilder()
        .addInterceptor(SameOriginRedirectInterceptor)
        .followRedirects(false)
        .build()

private object SameOriginRedirectInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val origin = request.url
        var response = chain.proceed(request)
        var followUpCount = 0
        while (true) {
            val followUp = followUpRequest(response, origin) ?: break
            if (++followUpCount > MAX_FOLLOW_UPS) {
                throw ProtocolException("Too many follow-up requests: $followUpCount")
            }
            response.close()
            response = chain.proceed(followUp)
        }
        return response
    }

    private fun followUpRequest(response: Response, origin: HttpUrl): Request? {
        val request = response.request
        val isGetOrHead = request.method == "GET" || request.method == "HEAD"
        when (response.code) {
            307, 308 -> if (!isGetOrHead) return null
            300, 301, 302, 303 -> Unit
            else -> return null
        }
        val location = response.header("Location") ?: return null
        val target = request.url.resolve(location) ?: return null
        if (origin.scheme != target.scheme || origin.host != target.host || origin.port != target.port) {
            return null
        }
        val builder = request.newBuilder().url(target)
        if (!isGetOrHead) {
            builder.method("GET", null)
            builder.removeHeader("Transfer-Encoding")
            builder.removeHeader("Content-Length")
            builder.removeHeader("Content-Type")
        }
        return builder.build()
    }
}
