package com.example.kotlin_demo_wallet

import android.app.Application
import com.turnkey.core.TurnkeyContext
import com.turnkey.models.AuthConfig
import com.turnkey.models.TurnkeyConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()

//        TurnkeyContext.init(
//            app = this,
//            config = TurnkeyConfig(
//                apiBaseUrl = "https://api.turnkey.com",
//                authProxyBaseUrl = "https://authproxy.turnkey.com",
//                authProxyConfigId = "25736d83-3cb0-4d4b-b020-2e2773bc9774",
//                organizationId = "62691bf1-9242-4e1e-a82b-9e972564d477",
//                appScheme = "kotlindemoapp",
//                authConfig = AuthConfig(
//                    rpId = "0bce2a9f181e.ngrok-free.app"
//                )
//            )
//        )

        TurnkeyContext.init(
            app = this,
            config = TurnkeyConfig(
                apiBaseUrl = "http://192.168.2.103:8081",
                authProxyBaseUrl = "http://192.168.2.103:8090",
                authProxyConfigId = "9012433f-97e1-4c06-99ea-d4b282614649",
                organizationId = "a588d081-4a19-42fc-962c-4b5c25603cdf",
                appScheme = "kotlindemoapp",
                authConfig = AuthConfig(
                    rpId = "0bce2a9f181e.ngrok-free.app"
                )
            )
        )
    }
}
