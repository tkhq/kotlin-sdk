package com.turnkey.models

data class TurnkeyConfig(
    val organizationId: String,
    val apiBaseUrl: String? = null,
    val authProxyBaseUrl: String? = null,
    val authProxyConfigId: String? = null,
    val authConfig: AuthConfig? = null,
    val appScheme: String? = null,

    // Callbacks
    val onSessionCreated: ((Session) -> Unit)? = null,
    val onSessionSelected: ((Session) -> Unit)? = null,
    val onSessionExpired: ((Session) -> Unit)? = null,
    val onSessionCleared: ((Session) -> Unit)? = null,
    val onSessionRefreshed: ((Session) -> Unit)? = null,
    val onSessionEmpty: (() -> Unit)? = null,
    val onInitialized: ((Any?) -> Unit)? = null,
)

data class AuthConfig(
    val methods: AuthMethods? = null,
    val oAuthConfig: OAuthConfig? = null,

    /**
     * Session expiration time in seconds.
     * If using the auth proxy, configure this in the dashboard.
     * Changing this through the Turnkey provider will have no effect.
     */
    val sessionExpirationSeconds: String? = null,

    /**
     * If OTP sent will be alphanumeric.
     * If using the auth proxy, configure this in the dashboard.
     * Changing this through the Turnkey provider will have no effect.
     */
    val otpAlphanumeric: Boolean? = null,

    /**
     * Length of the OTP.
     * If using the auth proxy, configure this in the dashboard.
     * Changing this through the Turnkey provider will have no effect.
     */
    val otpLength: String? = null,

    /**
     * The relying party ID for passkey authentication.
     */
    val rpId: String? = null,

    val createSubOrgParams: MethodCreateSubOrgParams? = null,
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