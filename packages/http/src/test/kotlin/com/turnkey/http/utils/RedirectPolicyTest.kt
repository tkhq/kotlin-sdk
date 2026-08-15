package com.turnkey.http.utils

import com.turnkey.http.TurnkeyClient
import com.turnkey.types.ProxyTGetAccountBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class RedirectPolicyTest {
    private fun postRequest(url: HttpUrl): Request =
        Request.Builder()
            .url(url)
            .post("""{"key":"value"}""".toRequestBody("application/json".toMediaType()))
            .header("X-Stamp", "stamp-header-value")
            .build()

    private fun redirectResponse(code: Int, location: String): MockResponse =
        MockResponse().setResponseCode(code).setHeader("Location", location)

    private fun runServer(block: (MockWebServer) -> Unit) {
        MockWebServer().use { server ->
            server.start()
            block(server)
        }
    }

    private fun runServers(block: (MockWebServer, MockWebServer) -> Unit) {
        MockWebServer().use { first ->
            MockWebServer().use { other ->
                first.start()
                other.start()
                block(first, other)
            }
        }
    }

    @Test
    fun sameOriginPost307And308PreserveMethodBodyAndHeaders() {
        for (code in listOf(307, 308)) {
            runServer { server ->
                server.enqueue(redirectResponse(code, "/next"))
                server.enqueue(MockResponse().setBody("{}"))
                OkHttpClient().withSameOriginRedirects()
                    .newCall(postRequest(server.url("/start")))
                    .execute()
                    .use { response -> assertEquals(200, response.code) }
                assertEquals(2, server.requestCount)
                server.takeRequest()
                val followUp = server.takeRequest()
                assertEquals("POST", followUp.method)
                assertEquals("/next", followUp.path)
                assertEquals("""{"key":"value"}""", followUp.body.readUtf8())
                assertEquals("stamp-header-value", followUp.getHeader("X-Stamp"))
                assertEquals("application/json; charset=utf-8", followUp.getHeader("Content-Type"))
            }
        }
    }

    @Test
    fun crossOriginRedirectIsRefusedThroughGeneratedClient() {
        runServers { first, other ->
            val location = other.url("/next").toString()
            first.enqueue(redirectResponse(307, location))
            other.enqueue(MockResponse().setBody("{}"))
            val turnkey = TurnkeyClient(
                stamper = null,
                http = OkHttpClient(),
                authProxyUrl = first.url("/").toString().removeSuffix("/"),
                authProxyConfigId = "config-id",
                organizationId = "org-id",
            )
            val error = assertFailsWith<TurnkeyHttpError.RedirectRefused> {
                runBlocking {
                    turnkey.proxyGetAccount(ProxyTGetAccountBody(filterType = "EMAIL", filterValue = "a@b.co"))
                }
            }
            assertEquals(307, error.code)
            assertEquals(location, error.location)
            assertEquals(1, first.requestCount)
            assertEquals(0, other.requestCount)
        }
    }

    @Test
    fun nonPreservingRedirectStatusesAreRefused() {
        for (code in listOf(300, 301, 302, 303, 304, 305, 306, 309, 399)) {
            runServer { server ->
                server.enqueue(redirectResponse(code, "/next"))
                val error = assertFailsWith<TurnkeyHttpError.RedirectRefused> {
                    OkHttpClient().withSameOriginRedirects()
                        .newCall(postRequest(server.url("/start")))
                        .execute()
                }
                assertEquals(code, error.code)
                assertEquals(1, server.requestCount)
            }
        }
    }

    @Test
    fun followUpsAreBoundedToTen() {
        runServer { server ->
            repeat(12) { server.enqueue(redirectResponse(307, "/next")) }
            assertFailsWith<TurnkeyHttpError.RedirectRefused> {
                OkHttpClient().withSameOriginRedirects()
                    .newCall(postRequest(server.url("/start")))
                    .execute()
            }
            assertEquals(11, server.requestCount)
        }
    }

    @Test
    fun wrappingIsIdempotent() {
        val wrapped = OkHttpClient().withSameOriginRedirects()
        val redirectsReenabled = wrapped.newBuilder().followRedirects(true).build()
        val protected = redirectsReenabled.withSameOriginRedirects()
        assertEquals(false, protected.followRedirects)
        assertEquals(wrapped.interceptors, protected.interceptors)
    }
}
