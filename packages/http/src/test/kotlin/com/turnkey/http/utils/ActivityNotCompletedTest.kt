package com.turnkey.http.utils

import com.turnkey.types.Externaldatav1Timestamp
import com.turnkey.types.V1Activity
import com.turnkey.types.V1ActivityStatus
import com.turnkey.types.V1ActivityType
import com.turnkey.types.V1Intent
import com.turnkey.types.V1Result
import com.turnkey.types.V1Vote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ActivityNotCompletedTest {

    private fun activity(status: V1ActivityStatus) = V1Activity(
        canApprove = false,
        canReject = false,
        createdAt = Externaldatav1Timestamp(nanos = "0", seconds = "0"),
        fingerprint = "fp",
        id = "activity-123",
        intent = V1Intent(),
        organizationId = "org-1",
        result = V1Result(),
        status = status,
        type = V1ActivityType.ACTIVITY_TYPE_SOL_SEND_TRANSACTION_V2,
        updatedAt = Externaldatav1Timestamp(nanos = "0", seconds = "0"),
        votes = emptyList<V1Vote>(),
    )

    @Test
    fun `carries the activity so callers can resume on its id`() {
        val a = activity(V1ActivityStatus.ACTIVITY_STATUS_PENDING)
        val e = TurnkeyHttpError.ActivityNotCompleted(a, "/public/v1/submit/sol_send_transaction")

        assertSame(a, e.activity)
        assertEquals("activity-123", e.activity.id)
        assertEquals("/public/v1/submit/sol_send_transaction", e.path)
    }

    @Test
    fun `pending and rejected are distinguishable by status`() {
        val pending = TurnkeyHttpError.ActivityNotCompleted(activity(V1ActivityStatus.ACTIVITY_STATUS_PENDING), "/p")
        val rejected = TurnkeyHttpError.ActivityNotCompleted(activity(V1ActivityStatus.ACTIVITY_STATUS_REJECTED), "/p")

        assertEquals(V1ActivityStatus.ACTIVITY_STATUS_PENDING, pending.activity.status)
        assertEquals(V1ActivityStatus.ACTIVITY_STATUS_REJECTED, rejected.activity.status)
    }

    @Test
    fun `message names the activity, path and status`() {
        val e = TurnkeyHttpError.ActivityNotCompleted(activity(V1ActivityStatus.ACTIVITY_STATUS_FAILED), "/public/v1/submit/eth_send_transaction")
        val msg = e.message ?: ""

        assertTrue(msg.contains("activity-123"), msg)
        assertTrue(msg.contains("/public/v1/submit/eth_send_transaction"), msg)
        assertTrue(msg.contains("ACTIVITY_STATUS_FAILED"), msg)
        assertTrue(msg.contains("ACTIVITY_STATUS_COMPLETED"), msg)
    }

    @Test
    fun `is a TurnkeyHttpError so existing sealed-class handling sees it`() {
        val e: Throwable = TurnkeyHttpError.ActivityNotCompleted(activity(V1ActivityStatus.ACTIVITY_STATUS_PENDING), "/p")
        assertIs<TurnkeyHttpError>(e)
        assertSame(e, TurnkeyHttpError.wrap("ignored", e))
    }
}
