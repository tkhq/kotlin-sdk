package com.example.kotlin_demo_wallet.ui.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.turnkey.core.TurnkeyContext
import com.turnkey.core.models.OtpType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthSheetViewModel : ViewModel() {
    val email = MutableStateFlow("")
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow<Throwable?>(null)

    private val _otpId = MutableStateFlow<String?>(null)
    val otpId: StateFlow<String?> = _otpId.asStateFlow()

    private val _contact = MutableStateFlow<String?>(null)
    val contact: StateFlow<String?> = _contact.asStateFlow()

    suspend fun sendOtpEmail(contact: String): Boolean = try {
        loading.value = true
        val res = TurnkeyContext.initOtp(
            otpType = OtpType.OTP_TYPE_EMAIL,
            contact = contact
        )
        _otpId.value = res.otpId
        _contact.value = contact
        true
    } catch (t: Throwable) {
        error.value = t; false
    } finally { loading.value = false }

    suspend fun verifyOtp(code: String): Boolean = try {
        loading.value = true
        val otpId = otpId.value ?: return false
        val contact = contact.value ?: return false
        TurnkeyContext.loginOrSignUpWithOtp(
            otpId = otpId,
            otpCode = code,
            contact = contact,
            otpType = OtpType.OTP_TYPE_EMAIL
        )
        true
    } catch (t: Throwable) {
        Log.e("AuthSheetViewModel", "Verify Otp failed", t)
        error.value = t; false
    } finally { loading.value = false }

    suspend fun handleGoogleOAuth(activity: Activity): Boolean = try {
        loading.value = true
        TurnkeyContext.handleGoogleOAuth(
            activity = activity,
        )
        true
    } catch (t: Throwable) {
        Log.e("AuthSheetViewModel", "HandleGoogleOAuth failed",t)
        error.value = t; false
    } finally { loading.value = false }
}