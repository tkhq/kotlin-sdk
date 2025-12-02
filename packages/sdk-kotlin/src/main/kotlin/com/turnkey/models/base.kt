package com.turnkey.models

import com.turnkey.types.V1HashFunction
import com.turnkey.types.V1PayloadEncoding

open class TurnkeyConfig(
    val organizationId: String,
    val apiBaseUrl: String? = null,
    val authProxyBaseUrl: String? = null,
    val authProxyConfigId: String? = null,
    open val authConfig: AuthConfig? = null,
    val appScheme: String? = null,
    val autoRefreshManagedStates: Boolean = true,
    val autoFetchWalletKitConfig: Boolean = true,

    // Callbacks
    val onSessionCreated: ((Session) -> Unit)? = null,
    val onSessionSelected: ((Session) -> Unit)? = null,
    val onSessionExpired: ((Session) -> Unit)? = null,
    val onSessionRefreshed: ((Session) -> Unit)? = null,
    // val onSessionCleared: ((Session) -> Unit)? = null,
    // val onSessionEmpty: (() -> Unit)? = null,
)

class TurnkeyRuntimeConfig (
    organizationId: String,
    apiBaseUrl: String? = null,
    authProxyBaseUrl: String? = null,
    authProxyConfigId: String? = null,
    override val authConfig: RuntimeAuthConfig? = null,
    appScheme: String? = null,
    autoRefreshManagedStates: Boolean = true,
    autoFetchWalletKitConfig: Boolean = true,

    // Callbacks
    onSessionCreated: ((Session) -> Unit)? = null,
    onSessionSelected: ((Session) -> Unit)? = null,
    onSessionExpired: ((Session) -> Unit)? = null,
    onSessionRefreshed: ((Session) -> Unit)? = null,
    // val onSessionCleared: ((Session) -> Unit)? = null,
    // val onSessionEmpty: (() -> Unit)? = null,
) : TurnkeyConfig (
    organizationId = organizationId,
    apiBaseUrl = apiBaseUrl,
    authProxyBaseUrl = authProxyBaseUrl,
    authProxyConfigId = authProxyConfigId,
    authConfig = authConfig
)

open class AuthConfig(
    val oAuthConfig: OAuthConfig? = null,
    val rpId: String? = null,
    val createSubOrgParams: MethodCreateSubOrgParams? = null,
)

class RuntimeAuthConfig(
    oAuthConfig: OAuthConfig? = null,
    val sessionExpirationSeconds: String? = null,
    val otpAlphanumeric: Boolean? = null,
    val otpLength: String? = null,
    rpId: String? = null,
    createSubOrgParams: MethodCreateSubOrgParams? = null,
) : AuthConfig (
    oAuthConfig = oAuthConfig,
    rpId = rpId,
    createSubOrgParams = createSubOrgParams
)

data class MethodCreateSubOrgParams(
    val emailOtpAuth: CreateSubOrgParams? = null,
    val smsOtpAuth: CreateSubOrgParams? = null,
    val passkeyAuth: CreateSubOrgParams? = null,
    val oAuth: CreateSubOrgParams? = null,
)

data class AuthMethods(
    val emailOtpAuthEnabled: Boolean? = null,
    val smsOtpAuthEnabled: Boolean? = null,
    val passkeyAuthEnabled: Boolean? = null,
    val walletAuthEnabled: Boolean? = null,
    val googleOauthEnabled: Boolean? = null,
    val appleOauthEnabled: Boolean? = null,
    val xOauthEnabled: Boolean? = null,
    val discordOauthEnabled: Boolean? = null,
    val facebookOauthEnabled: Boolean? = null,
)

data class OAuthConfig(
    /** Redirect URI for OAuth. */
    val oauthRedirectUri: String? = null,
    /** Client ID for Google OAuth. */
    val googleClientId: String? = null,
    /** Client ID for Apple OAuth. */
    val appleClientId: String? = null,
    /** Client ID for Facebook OAuth. */
    val facebookClientId: String? = null,
    /** Client ID for X (formerly Twitter) OAuth. */
    val xClientId: String? = null,
    /** Client ID for Discord OAuth. */
    val discordClientId: String? = null,
)

data class Defaults(
    val encoding: V1PayloadEncoding,
    val hashFunction: V1HashFunction
)