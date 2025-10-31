package com.turnkey.models

object SessionStorage {
    const val DEFAULT_SESSION_KEY = "com.turnkey.sdk.session"
    const val DEFAULT_EXPIRATION_SECONDS = "900"
}

object Storage {
    const val SECURE_ACCOUNT = "p256-private"
    const val SELECTED_SESSION_KEY = "com.turnkey.sdk.selectedSession"
    const val SESSION_REGISTRY_KEY = "com.turnkey.sdk.sessionKeys"
    const val PENDING_KEYS_STORE_KEY = "com.turnkey.sdk.pendingList"
    const val AUTO_REFRESH_STORE_KEY = "com.turnkey.sdk.autoRefresh"
}

object Turnkey {
    const val DEFAULT_API_URL = "https://api.turnkey.com"
    const val OAUTH_ORIGIN_URL = "https://oauth-origin.turnkey.com"
    const val OAUTH_REDIRECT_URL = "https://oauth-redirect.turnkey.com"
}

object OAuth {
    const val X_AUTH_URL = "https://x.com/i/oauth2/authorize";
    const val DISCORD_AUTH_URL = "https://discord.com/oauth2/authorize";
    const val TURNKEY_OAUTH_ORIGIN_URL = "https://oauth-origin.turnkey.com";
    const val TURNKEY_OAUTH_REDIRECT_URL = "https://oauth-redirect.turnkey.com";
    const val APPLE_AUTH_URL = "https://account.apple.com/auth/authorize";
    const val APPLE_AUTH_SCRIPT_URL = "https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js";
}