package com.turnkey.models

data class InitOtpResult(
    val otpId: String
)

data class VerifyOtpResult (
    val subOrganizationId: String? = null,
    val verificationToken: String,
)

data class LoginWithOtpResult(
    val sessionJwt: String
)

data class SignUpWithOtpResult(
    val sessionJwt: String
)

data class LoginOrSignUpWithOtpResult(
    val sessionJwt: String
)

data class LoginWithOAuthResult (
    val sessionJwt: String
)

data class SignUpWithOAuthResult(
    val sessionJwt: String
)

data class LoginOrSignUpWithOAuthResult(
    val sessionJwt: String
)

data class LoginWithPasskeyResult(
    val sessionJwt: String
)

data class SignUpWithPasskeyResult(
    val sessionJwt: String
)

data class ExportWalletResult(
    val mnemonicPhrase: String
)