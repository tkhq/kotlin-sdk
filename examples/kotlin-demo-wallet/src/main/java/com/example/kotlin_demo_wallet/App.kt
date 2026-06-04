package com.example.kotlin_demo_wallet

import android.app.Application
import com.turnkey.core.TurnkeyContext
import com.turnkey.core.models.AuthConfig
import com.turnkey.core.models.CreateSubOrgParams
import com.turnkey.core.models.CustomWallet
import com.turnkey.core.models.GoogleOAuthProviderParams
import com.turnkey.core.models.MethodCreateSubOrgParams
import com.turnkey.core.models.OAuthConfig
import com.turnkey.core.models.TurnkeyConfig
import com.turnkey.types.V1AddressFormat
import com.turnkey.types.V1Curve
import com.turnkey.types.V1PathFormat
import com.turnkey.types.V1WalletAccountParams

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val createSubOrgParams = CreateSubOrgParams(
            customWallet = CustomWallet(
                walletName = "Wallet 1",
                walletAccounts = listOf(
                    V1WalletAccountParams(
                        addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM,
                        curve = V1Curve.CURVE_SECP256K1,
                        path = "m/44'/60'/0'/0/0",
                        pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                    ),
                    V1WalletAccountParams(
                        addressFormat = V1AddressFormat.ADDRESS_FORMAT_SOLANA,
                        curve = V1Curve.CURVE_ED25519,
                        path = "m/44'/501'/0'/0'",
                        pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                    )
                )
            )
        )

        // Additional Google client IDs that should resolve to the same sub-organization
        // as the primary client ID (e.g. a web client ID alongside this mobile client ID).
        // Parsed from a comma-separated BuildConfig value; null when none are configured.
        val googleSecondaryClientIds = BuildConfig.GOOGLE_SECONDARY_CLIENT_IDS
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { null }

        TurnkeyContext.init(
            app = this,
            config = TurnkeyConfig(
                apiBaseUrl = BuildConfig.API_BASE_URL,
                authProxyBaseUrl = BuildConfig.AUTH_PROXY_BASE_URL,
                authProxyConfigId = BuildConfig.AUTH_PROXY_CONFIG_ID,
                organizationId = BuildConfig.ORGANIZATION_ID,
                appScheme = BuildConfig.APP_SHEME,
                authConfig = AuthConfig(
                    // Per-provider OAuth config. A blank primaryClientId falls back to the
                    // auth proxy's configured client ID; secondaryClientIds let users sign
                    // into the same sub-org from clients using a different client ID.
                    oAuthConfig = OAuthConfig(
                        google = GoogleOAuthProviderParams(
                            primaryClientId = BuildConfig.GOOGLE_CLIENT_ID.ifBlank { null },
                            secondaryClientIds = googleSecondaryClientIds,
                        ),
                    ),
                    rpId = BuildConfig.RP_ID,
                    createSubOrgParams = MethodCreateSubOrgParams(
                        emailOtpAuth = createSubOrgParams,
                        smsOtpAuth = createSubOrgParams,
                        passkeyAuth = createSubOrgParams,
                        oAuth = createSubOrgParams
                    )
                ),
                onSessionCreated = { s -> println("created: $s")},
                onSessionSelected = { s -> println("selected: $s")},
                onSessionExpired  = { s -> println("expired at: ${s.expiry}") },
                onSessionRefreshed = { s -> println("refreshed token=${s.token.take(6)}...") }
            )
        )
    }
}
