package com.example.kotlin_demo_wallet

import com.example.kotlin_demo_wallet.utils.CreateSubOrgParams
import com.example.kotlin_demo_wallet.utils.buildSignUpBody
import com.turnkey.core.TurnkeyCore
import com.turnkey.http.model.ProxyV1GetAccountRequest
import com.turnkey.http.model.ProxyV1OtpLoginRequest
import com.turnkey.http.model.ProxyV1VerifyOtpRequest
import com.turnkey.internal.storage.keys.KeyPairStore
import com.turnkey.models.Session
import com.turnkey.stamper.Stamper
import okhttp3.OkHttpClient


object TurnkeyHelpers {
    val otpTypeToFilterType: Map<String, String> = mapOf(
        "OTP_TYPE_EMAIL" to "EMAIL",
        "OTP_TYPE_SMS" to "PHONE_NUMBER"
    )

    data class VerifyOtpResult(val verificationToken: String, val subOrganizationId: String?)
    suspend fun verifyOtp (contact: String, otpCode: String, otpId: String, otpType: String): VerifyOtpResult {
        try {
            val response = TurnkeyCore.ctx.client.value?.proxyVerifyOtp(ProxyV1VerifyOtpRequest(
                otpId = otpId,
                otpCode = otpCode
            ))
            val verificationToken = response?.verificationToken

            if (verificationToken.isNullOrBlank()) throw RuntimeException("Missing verification token")

            val filterType = otpTypeToFilterType[otpType]
            if (filterType.isNullOrBlank()) throw RuntimeException("Invalid OTP type")
            val accountRes = TurnkeyCore.ctx.client.value?.proxyGetAccount(ProxyV1GetAccountRequest(
                filterType = filterType,
                filterValue = contact
            ))

            val subOrganizationId = accountRes?.organizationId
            return VerifyOtpResult(verificationToken, subOrganizationId)
        } catch (e: Throwable) {
            throw RuntimeException("Error verifying OTP", e)
        }
    }

    suspend fun signUpWithOtp (verificationToken: String, contact: String, otpType: String, createSubOrgParams: CreateSubOrgParams?): String {
        try {
            val signUpBody = buildSignUpBody(
                createSubOrgParams = (createSubOrgParams ?: CreateSubOrgParams()).copy(
                    userEmail = if (otpType == "OTP_TYPE_EMAIL") contact else null,
                    userPhoneNumber = if (otpType == "OTP_TYPE_SMS") contact else null,
                    verificationToken = verificationToken
                ),
            )

            val generatedPublicKey = TurnkeyCore.ctx.createKeyPair()
            val privKeyHex = KeyPairStore.getPrivateHex(TurnkeyCore.ctx.appContext, generatedPublicKey)
            val stamper = Stamper(generatedPublicKey, privKeyHex)

            val apiKeyClient = com.turnkey.http.TurnkeyClient(
                http = OkHttpClient(),
                apiBaseUrl = "http://192.168.0.158:8081",
                authProxyUrl = "http://192.168.0.158:8090",
                authProxyConfigId = "ce093cf1-ee16-42cf-b989-cdade4eaf8ed",
                stamper = stamper
            )

            val res = apiKeyClient.proxySignup(signUpBody)

            return loginWithOtp(verificationToken, publicKey = generatedPublicKey)

        } catch (e: Throwable) {
            throw RuntimeException("Error signing up with OTP", e)
        }
    }

    suspend fun loginWithOtp (verificationToken: String, publicKey: String = TurnkeyCore.ctx.createKeyPair()): String {
        val apiKeyClient = com.turnkey.http.TurnkeyClient(
            http = OkHttpClient(),
            apiBaseUrl = "http://192.168.0.158:8081",
            authProxyUrl = "http://192.168.0.158:8090",
            authProxyConfigId = "ce093cf1-ee16-42cf-b989-cdade4eaf8ed",
            stamper = null
        )

        try {
            val res = apiKeyClient.proxyOtpLogin(ProxyV1OtpLoginRequest(
                verificationToken,
                publicKey
            ))

            TurnkeyCore.ctx.clearSession(Session.DEFAULT_SESSION_KEY)
            println("Cleared active session")
            TurnkeyCore.ctx.createSession(jwt = res.session)

            return res.session
        } catch (e: Throwable) {
            throw RuntimeException("Error logging in with OTP", e)
        }
    }

    data class CompleteOtpResult(val sessionToken: String)
    suspend fun completeOtp (contact: String, otpType: String, otpCode: String, otpId: String): CompleteOtpResult {
        try {
            val ( verificationToken, subOrganizationId ) = verifyOtp(contact, otpCode, otpId, otpType)

            if (subOrganizationId.isNullOrBlank() ) {
                val signupRes = signUpWithOtp(verificationToken, contact, otpType, null)
                return CompleteOtpResult(signupRes)
            } else {
                val loginRes = loginWithOtp(verificationToken)
                return CompleteOtpResult(loginRes)
            }
        } catch (e: Throwable) {
            throw RuntimeException("Error completing OTP", e)
        }
    }
}