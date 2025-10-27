package com.example.kotlin_demo_wallet

import android.app.Application
import com.turnkey.core.TurnkeyCore
import com.turnkey.models.TurnkeyConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        TurnkeyCore.init(
            app = this,
            config = TurnkeyConfig(
                apiBaseUrl = "http://192.168.0.158:8081",
                authProxyBaseUrl = "http://192.168.0.158:8090",
                authProxyConfigId = "9012433f-97e1-4c06-99ea-d4b282614649",
            )
        )

        // OPTIONAL: if something must run only after init:
        // lifecycleScope.launch {
        //     TurnkeyCore.ready.await()
        //     // safe to use TurnkeyCore.ctx
        // }
    }
}
