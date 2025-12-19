package com.turnkey.passkey.utils

import com.turnkey.encoding.toBase64Url
import kotlinx.serialization.json.Json

fun buildCreatePublicKeyOptionsJson(
    rpId: String,
    userId: ByteArray,
    userName: String,
    challenge: ByteArray,
    excludeCredentialIds: List<ByteArray>,
    attestation: String = "none" // "none" keeps the flow simple; change if you need full attestation
): String {
    val exclude = excludeCredentialIds.joinToString(prefix = "[", postfix = "]") {
        """{"type":"public-key","id":"${it.toBase64Url()}"}"""
    }

    // Minimal but valid set per WebAuthn: ES256 only; tweak as needed
    val pubKeyCredParams = """[{"type":"public-key","alg":-7}]""" // -7 = ES256

    val json = """
      {
        "rp": { "id": "$rpId", "name": "$rpId" },
        "user": {
          "id": "${userId.toBase64Url()}",
          "name": "$userName",
          "displayName": "$userName"
        },
        "challenge": "${challenge.toBase64Url()}",
        "pubKeyCredParams": $pubKeyCredParams,
        "timeout": 60000,
        "attestation": "$attestation",
        "excludeCredentials": $exclude,
        "authenticatorSelection": { "residentKey":"preferred", "userVerification":"preferred" }
      }
    """.trimIndent()

    // A quick sanity parse (throws if malformed)
    Json.parseToJsonElement(json)
    return json
}

// Build WebAuthn "publicKey" GET options JSON for Credential Manager.
fun buildGetPublicKeyOptionsJson(
    rpId: String,
    challenge: ByteArray,
    allowCredentialIds: List<ByteArray> = emptyList()
): String {
    val allow = if (allowCredentialIds.isEmpty()) "[]"
    else allowCredentialIds.joinToString(prefix = "[", postfix = "]") {
        """{"type":"public-key","id":"${it.toBase64Url()}"}"""
    }
    val jsonStr = """
      {
        "challenge": "${challenge.toBase64Url()}",
        "rpId": "$rpId",
        "timeout": 60000,
        "userVerification": "preferred",
        "allowCredentials": $allow
      }
    """.trimIndent()
    Json.parseToJsonElement(jsonStr)
    return jsonStr
}