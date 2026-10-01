package com.turnkey.tools.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class VersionedActivityTypesTest {

    @Test
    fun `unpinned activity follows the type declared by the request schema`() {
        // tkhq/kotlin-sdk#95: the body is generated from the V2 intent (signWiths), so the posted type must be V2 too.
        assertEquals(
            "ACTIVITY_TYPE_SOL_SEND_TRANSACTION_V2",
            VersionedActivityTypes.resolve("ACTIVITY_TYPE_SOL_SEND_TRANSACTION_V2", "ACTIVITY_TYPE_SOL_SEND_TRANSACTION"),
        )
        assertEquals(
            "ACTIVITY_TYPE_EXECUTE_SWAP_V2",
            VersionedActivityTypes.resolve("ACTIVITY_TYPE_EXECUTE_SWAP_V2", "ACTIVITY_TYPE_EXECUTE_SWAP"),
        )
    }

    @Test
    fun `pinned activity wins over the declared type`() {
        // ETH send is deliberately pinned to V1 (see #88) even though the spec declares V2.
        assertEquals(
            "ACTIVITY_TYPE_ETH_SEND_TRANSACTION",
            VersionedActivityTypes.resolve("ACTIVITY_TYPE_ETH_SEND_TRANSACTION_V2", "ACTIVITY_TYPE_ETH_SEND_TRANSACTION"),
        )
        assertEquals(
            "ACTIVITY_TYPE_CREATE_POLICY_V3",
            VersionedActivityTypes.resolve("ACTIVITY_TYPE_CREATE_POLICY_V3", "ACTIVITY_TYPE_CREATE_POLICY"),
        )
    }

    @Test
    fun `declared type is used verbatim when it differs from the op-id-derived name`() {
        // toScreamingSnake("EthUndelegate7702") yields ETH_UNDELEGATE7702, which is not a valid activity type.
        assertEquals(
            "ACTIVITY_TYPE_ETH_UNDELEGATE_7702",
            VersionedActivityTypes.resolve("ACTIVITY_TYPE_ETH_UNDELEGATE_7702", "ACTIVITY_TYPE_ETH_UNDELEGATE7702"),
        )
    }

    @Test
    fun `falls back to the op-id-derived name when the request schema declares no type`() {
        assertEquals("ACTIVITY_TYPE_FOO", VersionedActivityTypes.resolve(null, "ACTIVITY_TYPE_FOO"))
        // ...but a pin still applies to the fallback.
        assertEquals(
            "ACTIVITY_TYPE_CREATE_USERS_V4",
            VersionedActivityTypes.resolve(null, "ACTIVITY_TYPE_CREATE_USERS"),
        )
    }

    @Test
    fun `keyFor strips only a version suffix`() {
        assertEquals("ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION", VersionedActivityTypes.keyFor("ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V8"))
        assertEquals("ACTIVITY_TYPE_CREATE_USERS", VersionedActivityTypes.keyFor("ACTIVITY_TYPE_CREATE_USERS"))
        assertEquals("ACTIVITY_TYPE_ETH_UNDELEGATE_7702", VersionedActivityTypes.keyFor("ACTIVITY_TYPE_ETH_UNDELEGATE_7702"))
    }
}
