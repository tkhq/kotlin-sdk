package com.turnkey.core.internal.utils

import com.turnkey.core.models.Session
import com.turnkey.core.models.SessionJwt

internal fun sessionFromJwt(
    jwt: String,
    currentTimeMillis: Long = System.currentTimeMillis()
): Session {
    val payload = JwtDecoder.decode<SessionJwt>(jwt)
    val expirationSeconds =
        ((payload.expiry * 1000.0 - currentTimeMillis) / 1000).toLong()

    return Session(
        userId = payload.userId,
        organizationId = payload.organizationId,
        expiry = payload.expiry,
        publicKey = payload.publicKey,
        sessionType = payload.sessionType,
        expirationSeconds = expirationSeconds.toString(),
        token = jwt
    )
}
