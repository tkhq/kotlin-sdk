package com.turnkey.http.utils

import com.turnkey.http.TurnkeyClient
import com.turnkey.types.ProxyTGetAccountBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
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

    @Test
    fun requestBodyIsNotSentAcross307Or308() {
        for (code in listOf(307, 308)) {
            runServers { first, other ->
                first.enqueue(redirectResponse(code, other.url("/next").toString()))
                other.enqueue(MockResponse().setBody("{}"))
                OkHttpClient().withSameOriginRedirects()
                    .newCall(postRequest(first.url("/start")))
                    .execute()
                    .use { response -> assertEquals(code, response.code) }
                assertEquals(1, first.requestCount)
                assertEquals(0, other.requestCount)
            }
        }
    }

    @Test
    fun redirectChainCannotChangeOrigin() {
        runServers { first, other ->
            first.enqueue(redirectResponse(302, "/next"))
            first.enqueue(redirectResponse(307, other.url("/elsewhere").toString()))
            other.enqueue(MockResponse().setBody("{}"))
            OkHttpClient().withSameOriginRedirects()
                .newCall(postRequest(first.url("/start")))
                .execute()
                .use { response -> assertEquals(307, response.code) }
            assertEquals("POST", first.takeRequest().method)
            assertEquals("GET", first.takeRequest().method)
            assertEquals(0, other.requestCount)
        }
    }
}
