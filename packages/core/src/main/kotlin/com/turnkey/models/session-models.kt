package com.turnkey.models

import com.turnkey.http.model.Externaldatav1Timestamp
import com.turnkey.http.model.V1AddressFormat
import com.turnkey.http.model.V1Curve
import com.turnkey.http.model.V1PathFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AuthState {
    loading, authenticated, unauthenticated
}

@Serializable
data class TurnkeySession(
    /** Expiration as epoch-seconds (JWT `exp`). */
    @SerialName("exp") val exp: Double,
    @SerialName("public_key") val publicKey: String,
    @SerialName("session_type") val sessionType: String,
    @SerialName("user_id") val userId: String,
    @SerialName("organization_id") val organizationId: String
)

@Serializable
data class SessionUser(
    val id: String,
    val userName: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val organizationId: String,
    val wallets: MutableList<UserWallet>
) {
    @Serializable
    data class UserWallet(
        val id: String,
        val name: String,
        val accounts: MutableList<WalletAccount>
    ) {
        @Serializable
        data class WalletAccount(
            val id: String,
            val curve: V1Curve,
            val pathFormat: V1PathFormat,
            val path: String,
            val addressFormat: V1AddressFormat,
            val address: String,
            val createdAt: Externaldatav1Timestamp,
            val updatedAt: Externaldatav1Timestamp
        )
    }
}