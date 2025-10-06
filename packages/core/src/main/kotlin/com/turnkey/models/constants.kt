package com.turnkey.models

object Session {
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