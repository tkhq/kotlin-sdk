package com.turnkey.http.utils

import com.turnkey.http.TurnkeyClient
import com.turnkey.types.ProxyTGetAccountBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
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
    fun postIsNotSentToAnotherOriginOn307() {
        runServers { first, other ->
            first.enqueue(redirectResponse(307, other.url("/next").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(307, response.code)
            }
            assertEquals(1, first.requestCount)
            assertEquals(0, other.requestCount)
        }
    }

    @Test
    fun postIsNotSentToAnotherOriginOn308() {
        runServers { first, other ->
            first.enqueue(redirectResponse(308, other.url("/next").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(308, response.code)
            }
            assertEquals(1, first.requestCount)
            assertEquals(0, other.requestCount)
        }
    }

    @Test
    fun headersAreNotSentToAnotherOriginOn302() {
        runServers { first, other ->
            first.enqueue(redirectResponse(302, other.url("/next").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(302, response.code)
            }
            assertEquals(0, other.requestCount)
        }
    }

    @Test
    fun sameOrigin302IsFollowedAsGet() {
        runServers { first, _ ->
            first.enqueue(redirectResponse(302, "/next"))
            first.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(200, response.code)
            }
            assertEquals("POST", first.takeRequest().method)
            val followUp = first.takeRequest()
            assertEquals("GET", followUp.method)
            assertEquals("/next", followUp.path)
            assertEquals(0, followUp.bodySize)
        }
    }

    @Test
    fun sameOrigin307PostIsReturnedToCaller() {
        runServers { first, _ ->
            first.enqueue(redirectResponse(307, "/next"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(307, response.code)
            }
            assertEquals(1, first.requestCount)
        }
    }

    @Test
    fun redirectChainLeavingOriginStops() {
        runServers { first, other ->
            first.enqueue(redirectResponse(302, "/next"))
            first.enqueue(redirectResponse(302, other.url("/elsewhere").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            val client = OkHttpClient().withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(302, response.code)
            }
            assertEquals(2, first.requestCount)
            assertEquals(0, other.requestCount)
        }
    }

    @Test
    fun originComparisonUsesSchemeHostAndPort() {
        assertTrue(isSameOrigin("https://example.com/a".toHttpUrl(), "https://example.com/b".toHttpUrl()))
        assertFalse(isSameOrigin("https://example.com/a".toHttpUrl(), "http://example.com/a".toHttpUrl()))
        assertFalse(isSameOrigin("https://example.com/a".toHttpUrl(), "https://example.com:8443/a".toHttpUrl()))
        assertFalse(isSameOrigin("https://example.com/a".toHttpUrl(), "https://example.org/a".toHttpUrl()))
    }

    @Test
    fun clientThatDoesNotFollowRedirectsIsUnchangedInBehavior() {
        runServers { first, _ ->
            first.enqueue(redirectResponse(302, "/next"))
            val base = OkHttpClient.Builder().followRedirects(false).build()
            val client = base.withSameOriginRedirects()
            client.newCall(postRequest(first.url("/start"))).execute().use { response ->
                assertEquals(302, response.code)
            }
            assertEquals(1, first.requestCount)
        }
    }

    @Test
    fun wrappingIsIdempotent() {
        val wrapped = OkHttpClient().withSameOriginRedirects()
        assertSame(wrapped, wrapped.withSameOriginRedirects())
    }

    @Test
    fun injectedClientDoesNotFollowRedirectToAnotherOrigin() {
        runServers { first, other ->
            first.enqueue(redirectResponse(302, other.url("/next").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            val turnkey = TurnkeyClient(
                stamper = null,
                http = OkHttpClient(),
                authProxyUrl = first.url("/").toString().removeSuffix("/"),
                authProxyConfigId = "config-id",
                organizationId = "org-id",
            )
            val error = assertFailsWith<RuntimeException> {
                runBlocking {
                    turnkey.proxyGetAccount(ProxyTGetAccountBody(filterType = "EMAIL", filterValue = "a@b.co"))
                }
            }
            assertTrue(error.message.orEmpty().contains("302"))
            assertEquals(1, first.requestCount)
            assertEquals(0, other.requestCount)
        }
    }
}
