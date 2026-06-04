package com.turnkey.core.models

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
    appScheme = appScheme,
    autoRefreshManagedStates = autoRefreshManagedStates,
    autoFetchWalletKitConfig = autoFetchWalletKitConfig,
    onSessionCreated = onSessionCreated,
    onSessionSelected = onSessionSelected,
    onSessionExpired = onSessionExpired,
    onSessionRefreshed = onSessionRefreshed
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

/**
 * Per-provider OAuth configuration.
 *
 * Holds everything needed to drive an OAuth flow for a single provider:
 * its primary client ID, any secondary client IDs, and an optional redirect URI.
 */
data class OAuthProviderParams(
    /** Primary client ID registered with the OAuth provider. */
    val primaryClientId: String? = null,
    /**
     * Additional client IDs to register as secondary OAuth providers during
     * sub-organization creation. This lets users sign into the same sub-organization
     * from clients that use a different client ID (e.g. web vs. mobile) instead of
     * getting a brand new sub-organization for each distinct client ID.
     */
    val secondaryClientIds: List<String>? = null,
    /**
     * Redirect URI used for the OAuth callback. If null, falls back to the top-level
     * [OAuthConfig.oauthRedirectUri] and ultimately to a value derived from `appScheme`.
     */
    val redirectUri: String? = null,
)

typealias GoogleOAuthProviderParams = OAuthProviderParams
typealias AppleOAuthProviderParams = OAuthProviderParams
typealias FacebookOAuthProviderParams = OAuthProviderParams
typealias XOAuthProviderParams = OAuthProviderParams
typealias DiscordOAuthProviderParams = OAuthProviderParams

data class OAuthConfig(
    /**
     * Default redirect URI for OAuth, used when a provider does not specify its own
     * [OAuthProviderParams.redirectUri].
     */
    val oauthRedirectUri: String? = null,
    /** Google OAuth provider configuration. */
    val google: GoogleOAuthProviderParams? = null,
    /** Apple OAuth provider configuration. */
    val apple: AppleOAuthProviderParams? = null,
    /** Facebook OAuth provider configuration. */
    val facebook: FacebookOAuthProviderParams? = null,
    /** X (formerly Twitter) OAuth provider configuration. */
    val x: XOAuthProviderParams? = null,
    /** Discord OAuth provider configuration. */
    val discord: DiscordOAuthProviderParams? = null,
)

data class Defaults(
    val encoding: V1PayloadEncoding,
    val hashFunction: V1HashFunction
)

data class ClientSignaturePayload (
    val message: String,
    val clientSignaturePublicKey: String
)