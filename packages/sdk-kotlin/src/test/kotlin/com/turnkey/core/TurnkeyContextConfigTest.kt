package com.turnkey.core

import com.turnkey.core.models.TurnkeyConfig
import com.turnkey.core.models.TurnkeyRuntimeConfig
import com.turnkey.types.ProxyTGetWalletKitConfigResponse
import com.turnkey.types.ProxyTOtpLoginV2Body
import com.turnkey.types.V1ClientSignature
import com.turnkey.types.V1ClientSignatureScheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TurnkeyContextConfigTest {
    @Test
    fun `uses the wallet kit configuration organization for runtime config`() {
        val resolveWithProxy = TurnkeyContext::class.java.getDeclaredMethod(
            "resolveWithProxy",
            TurnkeyConfig::class.java,
            ProxyTGetWalletKitConfigResponse::class.java
        ).apply { isAccessible = true }
        val runtimeConfig = resolveWithProxy.invoke(
            TurnkeyContext,
            TurnkeyConfig(organizationId = "local-constructor-org"),
            ProxyTGetWalletKitConfigResponse(
                enabledProviders = emptyList(),
                organizationId = "auth-proxy-parent-org",
                sessionExpirationSeconds = "3600"
            )
        ) as TurnkeyRuntimeConfig

        assertEquals("auth-proxy-parent-org", runtimeConfig.organizationId)
    }

    @Test
    fun `does not dereference uninitialized runtime config`() {
        val strictRuntimeConfigOrNull = TurnkeyContext::class.java
            .getDeclaredMethod("strictRuntimeConfigOrNull")
            .apply { isAccessible = true }

        assertNull(strictRuntimeConfigOrNull.invoke(TurnkeyContext))
    }

    @Test
    fun `omits a whitespace organization ID from the OTP login request`() {
        val request = ProxyTOtpLoginV2Body(
            verificationToken = "verification-token",
            publicKey = "public-key",
            clientSignature = V1ClientSignature(
                message = "message",
                publicKey = "public-key",
                scheme = V1ClientSignatureScheme.CLIENT_SIGNATURE_SCHEME_API_P256,
                signature = "signature"
            ),
            organizationId = TurnkeyContext.normalizeOtpLoginOrganizationId(" \t ")
        )

        assertNull(request.organizationId)
    }
}
