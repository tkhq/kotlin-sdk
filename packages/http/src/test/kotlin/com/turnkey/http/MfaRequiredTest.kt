package com.turnkey.http

import com.turnkey.http.utils.ActivityPollerConfig
import com.turnkey.http.utils.TurnkeyHttpError
import com.turnkey.stamper.Stamper
import com.turnkey.types.TDeletePolicyBody
import com.turnkey.types.V1ActivityStatus
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MfaRequiredTest {
    private val server = MockWebServer()

    @BeforeTest
    fun start() = server.start()

    @AfterTest
    fun stop() = server.close()

    private fun fixed32(value: BigInteger): ByteArray {
        val bytes = value.toByteArray()
        return if (bytes.size > 32) bytes.copyOfRange(bytes.size - 32, bytes.size) else ByteArray(32 - bytes.size) + bytes
    }

    private fun hex(bytes: ByteArray) = bytes.joinToString("") { "%02x".format(it) }

    private fun client(): TurnkeyClient {
        val keyPair =
            KeyPairGenerator.getInstance("EC").run {
                initialize(ECGenParameterSpec("secp256r1"))
                generateKeyPair()
            }
        val pub = keyPair.public as ECPublicKey
        val prefix: Byte = if (pub.w.affineY.testBit(0)) 0x03 else 0x02
        val publicKeyHex = hex(byteArrayOf(prefix) + fixed32(pub.w.affineX))
        val privateKeyHex = hex(fixed32((keyPair.private as ECPrivateKey).s))
        return TurnkeyClient(
            apiBaseUrl = server.url("").toString().trimEnd('/'),
            stamper = Stamper(publicKeyHex, privateKeyHex),
            organizationId = "org-1",
            activityPoller = ActivityPollerConfig(intervalMs = 1, numRetries = 3),
        )
    }

    private fun activity(status: String) =
        """
        {"activity":{"id":"activity-1","organizationId":"org-1","status":"$status",
        "type":"ACTIVITY_TYPE_DELETE_POLICY","fingerprint":"fp-1","canApprove":true,"canReject":true,
        "intent":{},"result":{},"votes":[],
        "createdAt":{"seconds":"0","nanos":"0"},"updatedAt":{"seconds":"0","nanos":"0"}}}
        """.trimIndent()

    private val mfaStatus =
        """
        {"mfaStatuses":[{"mfaPolicyId":"policy-1","userId":"user-1","satisfied":false,
        "requiredMethods":[],"satisfiedMethods":[]}]}
        """.trimIndent()

    private fun json(body: String, code: Int = 200) =
        MockResponse.Builder().code(code).addHeader("Content-Type", "application/json").body(body).build()

    private fun deletePolicy() = TDeletePolicyBody(organizationId = "org-1", policyId = "policy-x")

    @Test
    fun `stops at once when the submit response needs MFA`() =
        runTest {
            server.enqueue(json(activity("ACTIVITY_STATUS_AUTHENTICATORS_NEEDED")))
            server.enqueue(json(mfaStatus))

            val error = assertFailsWith<TurnkeyHttpError.MfaRequired> { client().deletePolicy(deletePolicy()) }

            assertEquals("activity-1", error.activity.id)
            assertEquals(V1ActivityStatus.ACTIVITY_STATUS_AUTHENTICATORS_NEEDED, error.activity.status)
            assertEquals(listOf("policy-1"), error.mfaStatuses.map { it.mfaPolicyId })
            assertEquals(null, error.cause)
            assertEquals("/public/v1/submit/delete_policy", server.takeRequest().url.encodedPath)
            val statusRequest = server.takeRequest()
            assertEquals("/public/v1/query/get_mfa_status", statusRequest.url.encodedPath)
            assertTrue(statusRequest.body!!.utf8().contains("\"activityId\":\"activity-1\""))
            assertEquals(2, server.requestCount)
        }

    @Test
    fun `stops polling when a poll needs MFA`() =
        runTest {
            server.enqueue(json(activity("ACTIVITY_STATUS_PENDING")))
            server.enqueue(json(activity("ACTIVITY_STATUS_AUTHENTICATORS_NEEDED")))
            server.enqueue(json(mfaStatus))

            val error = assertFailsWith<TurnkeyHttpError.MfaRequired> { client().deletePolicy(deletePolicy()) }

            assertEquals(1, error.mfaStatuses.size)
            assertEquals(
                listOf("/public/v1/submit/delete_policy", "/public/v1/query/get_activity", "/public/v1/query/get_mfa_status"),
                List(3) { server.takeRequest().url.encodedPath },
            )
            assertEquals(3, server.requestCount)
        }

    @Test
    fun `still reports MFA when the status query fails`() =
        runTest {
            server.enqueue(json(activity("ACTIVITY_STATUS_AUTHENTICATORS_NEEDED")))
            server.enqueue(json("""{"message":"internal"}""", code = 500))

            val error = assertFailsWith<TurnkeyHttpError.MfaRequired> { client().deletePolicy(deletePolicy()) }

            assertEquals("activity-1", error.activity.id)
            assertTrue(error.mfaStatuses.isEmpty())
            assertTrue(error.cause != null, "the status query error is kept as the cause")
        }
}
