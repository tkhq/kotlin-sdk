package com.example.kotlin_demo_wallet

import android.app.Application
import com.turnkey.core.TurnkeyContext
import com.turnkey.models.AuthConfig
import com.turnkey.models.CreateSubOrgParams
import com.turnkey.models.CustomWallet
import com.turnkey.models.MethodCreateSubOrgParams
import com.turnkey.models.TurnkeyConfig
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

        TurnkeyContext.init(
            app = this,
            config = TurnkeyConfig(
                apiBaseUrl = "https://api.turnkey.com",
                authProxyBaseUrl = "https://authproxy.turnkey.com",
                authProxyConfigId = "25736d83-3cb0-4d4b-b020-2e2773bc9774",
                organizationId = "62691bf1-9242-4e1e-a82b-9e972564d477",
                appScheme = "kotlindemoapp",
                authConfig = AuthConfig(
                    rpId = "0bce2a9f181e.ngrok-free.app",
                    createSubOrgParams = MethodCreateSubOrgParams(
                        emailOtpAuth = createSubOrgParams,
                        smsOtpAuth = createSubOrgParams,
                        passkeyAuth = createSubOrgParams,
                        oAuth = createSubOrgParams
                    )
                )
            )
        )
    }
}
