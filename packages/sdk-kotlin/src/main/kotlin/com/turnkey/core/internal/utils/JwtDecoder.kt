package com.turnkey.core.internal.utils

import com.turnkey.core.models.StorageError
import com.turnkey.core.models.StorageError.DecodingFailed
import com.turnkey.core.models.StorageError.InvalidJWT
import com.turnkey.encoding.decodeBase64Url
import kotlinx.serialization.json.Json

object JwtDecoder {
    /**
     * Decode a JWT's payload (2nd segment) as type [T].
     *
     * - Validates the 3-part structure.
     * - Base64URL-decodes the payload.
     * - Parses JSON into [T] (unknown fields are ignored by default).
     *
     * @throws StorageError.InvalidJWT if structure or base64 is invalid
     * @throws StorageError.DecodingFailed if JSON parsing fails
     */
    @Throws(StorageError::class)
    inline fun <reified T> decode(
        jwt: String,
        json: Json = Json { ignoreUnknownKeys = true }
    ): T {
        val parts = jwt.split('.')
        if (parts.size != 3) throw InvalidJWT

        val payloadB64Url = parts[1]
        val bytes = try {
            decodeBase64Url(payloadB64Url)
        } catch (_: Throwable) {
            throw InvalidJWT
        }

        return try {
            json.decodeFromString<T>(bytes.toString(Charsets.UTF_8))
        } catch (e: Throwable) {
            throw DecodingFailed(e)
        }
    }
}