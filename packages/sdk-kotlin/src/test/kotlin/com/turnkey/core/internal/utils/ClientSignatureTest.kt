package com.turnkey.core.internal.utils

import com.turnkey.types.V1LoginUsageV2
import com.turnkey.types.V1RootUserParamsV5
import com.turnkey.types.V1SignupUsageV3
import com.turnkey.types.V1TokenUsage
import com.turnkey.types.V1UsageType
import com.turnkey.types.V1WalletParams
import java.util.Base64
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ClientSignatureTest {
    private val json = Json

    @Test
    fun `uses strict login usage when final request values are known`() {
        val payload = ClientSignature.forLoginForRequest(
            verificationToken = verificationToken(),
            organizationId = "sub-org",
            invalidateExisting = true,
            expirationSeconds = "3600"
        )

        val usage = json.decodeFromString<V1TokenUsage>(payload.message)
        assertEquals(
            V1LoginUsageV2(
                organizationId = "sub-org",
                publicKey = "verification-public-key",
                invalidateExisting = true,
                expirationSeconds = "3600"
            ),
            usage.loginV2
        )
        assertNull(usage.login)
    }

    @Test
    fun `uses legacy login usage when runtime config is unavailable`() {
        val payload = ClientSignature.forLoginForRequest(
            verificationToken = verificationToken(),
            organizationId = "sub-org",
            invalidateExisting = true,
            expirationSeconds = null
        )

        val usage = json.decodeFromString<V1TokenUsage>(payload.message)
        assertEquals("verification-public-key", usage.login?.publicKey)
        assertNull(usage.loginV2)
    }

    @Test
    fun `uses legacy login usage when target organization is blank`() {
        val payload = ClientSignature.forLoginForRequest(
            verificationToken = verificationToken(),
            organizationId = " \t",
            invalidateExisting = true,
            expirationSeconds = "3600"
        )

        val usage = json.decodeFromString<V1TokenUsage>(payload.message)
        assertEquals("verification-public-key", usage.login?.publicKey)
        assertNull(usage.loginV2)
    }

    @Test
    fun `binds strict signup usage to the authoritative request`() {
        val wallet = V1WalletParams(walletName = "wallet", accounts = emptyList())
        val rootUser = V1RootUserParamsV5(
            apiKeys = emptyList(),
            authenticators = emptyList(),
            oauthProviders = emptyList(),
            userEmail = "user@example.com",
            userName = "user",
            userPhoneNumber = null
        )
        val expected = V1TokenUsage(
            signupV3 = V1SignupUsageV3(
                parentOrganizationId = "auth-proxy-parent-org",
                subOrganizationName = "sub-org",
                rootUsers = listOf(rootUser),
                rootQuorumThreshold = 1,
                wallet = wallet
            ),
            tokenId = "verification-token-id",
            type = V1UsageType.USAGE_TYPE_SIGNUP
        )

        val payload = ClientSignature.forSignUpV3(
            verificationToken = verificationToken(),
            parentOrganizationId = "auth-proxy-parent-org",
            subOrganizationName = "sub-org",
            rootUsers = listOf(rootUser),
            rootQuorumThreshold = 1,
            wallet = wallet
        )

        assertEquals(json.encodeToString(V1TokenUsage.serializer(), expected), payload.message)
        val usage = json.decodeFromString<V1TokenUsage>(payload.message).signupV3!!
        assertEquals("auth-proxy-parent-org", usage.parentOrganizationId)
        assertEquals(1, usage.rootQuorumThreshold)
        assertEquals(wallet, usage.wallet)
        assertTrue(usage.rootUsers.single().apiKeys.isEmpty())
        assertTrue(usage.rootUsers.single().authenticators.isEmpty())
        assertTrue(usage.rootUsers.single().oauthProviders.isEmpty())
    }

    private fun verificationToken(): String {
        val payload = """{"contact":"user@example.com","exp":1,"id":"verification-token-id","public_key":"verification-public-key","verification_type":"OTP","organization_id":"verification-org"}"""
        val encodedPayload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payload.encodeToByteArray())
        return "e30.$encodedPayload.signature"
    }
}
