package com.turnkey.tools.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeclaredActivityTypeTest {

    private fun defs(json: String) = Json.parseToJsonElement(json).jsonObject

    @Test
    fun `reads the type enum declared on a request schema`() {
        val d = defs(
            """
            {
              "v1SolSendTransactionRequest": {
                "type": "object",
                "properties": {
                  "type": { "type": "string", "enum": ["ACTIVITY_TYPE_SOL_SEND_TRANSACTION_V2"] },
                  "parameters": { "${'$'}ref": "#/definitions/v1SolSendTransactionIntentV2" }
                }
              }
            }
            """
        )
        assertEquals("ACTIVITY_TYPE_SOL_SEND_TRANSACTION_V2", declaredActivityType(d["v1SolSendTransactionRequest"]?.jsonObject))
    }

    @Test
    fun `is null when the schema declares no type, an empty enum, or is missing`() {
        val d = defs(
            """
            {
              "noType": { "type": "object", "properties": { "parameters": { "type": "object" } } },
              "emptyEnum": { "type": "object", "properties": { "type": { "type": "string", "enum": [] } } },
              "noProps": { "type": "object" }
            }
            """
        )
        assertNull(declaredActivityType(d["noType"]?.jsonObject))
        assertNull(declaredActivityType(d["emptyEnum"]?.jsonObject))
        assertNull(declaredActivityType(d["noProps"]?.jsonObject))
        assertNull(declaredActivityType(null))
    }

    @Test
    fun `resolves end to end through VersionedActivityTypes`() {
        val d = defs("""{ "r": { "properties": { "type": { "enum": ["ACTIVITY_TYPE_EXECUTE_SWAP_V2"] } } } }""")
        val declared = declaredActivityType(d["r"]?.jsonObject)
        assertEquals("ACTIVITY_TYPE_EXECUTE_SWAP_V2", VersionedActivityTypes.resolve(declared, "ACTIVITY_TYPE_EXECUTE_SWAP"))
    }
}
