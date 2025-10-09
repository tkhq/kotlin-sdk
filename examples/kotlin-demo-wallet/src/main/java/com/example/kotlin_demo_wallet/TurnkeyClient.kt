package com.example.kotlin_demo_wallet

import com.turnkey.http.TurnkeyClient
import com.turnkey.stamper.Stamper
import okhttp3.OkHttpClient

object TurnkeyClient {
    lateinit var client: TurnkeyClient

    fun init(
        apiBaseUrl: String,
        stamper: Stamper?,
        http: OkHttpClient = OkHttpClient(),
        authProxyUrl: String,
        authProxyConfigId: String,
    ) {
        client = TurnkeyClient(
            apiBaseUrl = apiBaseUrl,
            stamper = stamper,
            http = http,
            authProxyUrl = authProxyUrl,
            authProxyConfigId = authProxyConfigId
        )
    }
}