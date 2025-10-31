package com.turnkey.models

data class VerifyOtpResult (
    val subOrganizationId: String? = null,
    val verificationToken: String,
)