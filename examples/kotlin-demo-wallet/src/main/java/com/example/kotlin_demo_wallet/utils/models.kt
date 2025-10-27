package com.example.kotlin_demo_wallet.utils

import com.turnkey.http.V1ApiKeyCurve
import com.turnkey.http.V1Attestation
import com.turnkey.http.V1OauthProviderParams
import com.turnkey.http.V1WalletAccountParams
import kotlinx.serialization.Serializable

@Serializable
data class CreateSubOrgParams(
    /** name of the user */
    val userName: String? = null,
    /** name of the sub-organization */
    val subOrgName: String? = null,
    /** email of the user */
    val userEmail: String? = null,
    /** tag of the user */
    val userTag: String? = null,
    /** list of authenticators */
    val authenticators: List<CreateSubOrgAuthenticator>? = null,
    /** phone number of the user */
    val userPhoneNumber: String? = null,
    /** verification token if email or phone number is provided */
    val verificationToken: String? = null,
    /** list of api keys */
    val apiKeys: List<CreateSubOrgApiKey>? = null,
    /** custom wallets to create during sub-org creation time */
    val customWallet: CustomWallet? = null,
    /** list of oauth providers */
    val oauthProviders: List<V1OauthProviderParams>? = null,
)

@Serializable
data class CreateSubOrgAuthenticator(
    val authenticatorName: String? = null,
    val challenge: String,
    val attestation: V1Attestation,
)

@Serializable
data class CreateSubOrgApiKey(
    /** name of the api key */
    val apiKeyName: String? = null,
    /** public key in hex format */
    val publicKey: String,
    /** expiration in seconds */
    val expirationSeconds: String? = null,
    /** curve type */
    val curveType: V1ApiKeyCurve? = null,
)

@Serializable
data class CustomWallet(
    /** name of the wallet created */
    val walletName: String,
    /** list of wallet accounts to create */
    val walletAccounts: List<V1WalletAccountParams>,
)

