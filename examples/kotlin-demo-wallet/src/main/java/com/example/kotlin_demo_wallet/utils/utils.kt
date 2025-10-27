package com.example.kotlin_demo_wallet.utils

import com.turnkey.http.ProxyTSignupBody
import com.turnkey.http.V1ApiKeyParamsV2
import com.turnkey.http.V1AuthenticatorParamsV2
import com.turnkey.http.V1WalletParams

fun buildSignUpBody(createSubOrgParams: CreateSubOrgParams): ProxyTSignupBody {
    val now = System.currentTimeMillis()

    val authenticators: List<V1AuthenticatorParamsV2> =
        createSubOrgParams.authenticators
            ?.takeIf { it.isNotEmpty() }
            ?.map { a ->
                V1AuthenticatorParamsV2(
                    authenticatorName = a.authenticatorName ?: "A Passkey",
                    challenge = a.challenge,
                    attestation = a.attestation
                )
            }
            ?: emptyList()

    val apiKeys: List<V1ApiKeyParamsV2> =
        createSubOrgParams.apiKeys
            ?.filter { it.curveType != null }
            ?.map { k ->
                V1ApiKeyParamsV2(
                    apiKeyName = k.apiKeyName ?: "api-key-$now",
                    publicKey = k.publicKey,
                    expirationSeconds = k.expirationSeconds ?: "900",
                    curveType = k.curveType!!,
                )
            }
            ?: emptyList()

    val userName = createSubOrgParams.userName
        ?: createSubOrgParams.userEmail
        ?: "user-$now"

    val subOrgName = createSubOrgParams.subOrgName ?: "sub-org-$now"

    val customWallet: V1WalletParams? = createSubOrgParams.customWallet?.let {
        V1WalletParams(walletName = it.walletName, accounts = it.walletAccounts)
    }

    return ProxyTSignupBody(
        userName = userName,
        organizationName = subOrgName,
        userEmail = createSubOrgParams.userEmail,
        userTag = createSubOrgParams.userTag,
        authenticators = authenticators,
        userPhoneNumber = createSubOrgParams.userPhoneNumber,
        verificationToken = createSubOrgParams.verificationToken,
        apiKeys = apiKeys,
        wallet = customWallet,
        oauthProviders = createSubOrgParams.oauthProviders ?: emptyList(),
    )
}