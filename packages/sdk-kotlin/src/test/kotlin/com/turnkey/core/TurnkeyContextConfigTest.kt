package com.turnkey.core

import com.turnkey.core.models.TurnkeyConfig
import com.turnkey.core.models.TurnkeyRuntimeConfig
import com.turnkey.types.ProxyTGetWalletKitConfigResponse
import org.junit.jupiter.api.Assertions.assertEquals
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
}
