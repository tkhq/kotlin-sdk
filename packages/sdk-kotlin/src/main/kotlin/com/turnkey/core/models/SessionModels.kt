package com.turnkey.core.models

import com.turnkey.types.V1WalletAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AuthState {
    loading, authenticated, unauthenticated
}

@Serializable
data class SessionJwt (
    @SerialName("user_id")
    val userId: String,
    @SerialName("organization_id")
    val organizationId: String,
    @SerialName("exp")
    val expiry: Double,
    @SerialName("public_key")
    val publicKey: String,
    @SerialName("session_type")
    val sessionType: String
)

@Serializable
data class Session(
    @SerialName("user_id")
    val userId: String,
    @SerialName("organization_id")
    val organizationId: String,
    @SerialName("exp")
    val expiry: Double,
    @SerialName("expiration_seconds")
    val expirationSeconds: String,
    @SerialName("public_key")
    val publicKey: String,
    @SerialName("token")
    val token: String,
    @SerialName("session_type")
    val sessionType: String
)

@Serializable
data class Wallet (
    val id: String,
    val name: String,
    val accounts: List<V1WalletAccount>
)

@Serializable
data class VerificationToken (
    val contact: String,
    val exp: Double,
    val id: String,
    @SerialName("public_key")
    val publicKey: String?,
    @SerialName("verification_type")
    val verificationType: String
)