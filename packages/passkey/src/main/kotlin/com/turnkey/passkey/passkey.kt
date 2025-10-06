package com.turnkey.passkey

import android.app.Activity
import com.turnkey.internal.PasskeyOperationRunner
import kotlinx.serialization.Serializable

data class PasskeyUser (
    val id: String,
    val name: String,
    val displayName: String,
)

data class RelyingParty (
    val id: String,
    val name: String,
)

@Serializable
data class PasskeyRegistrationResult (
    val challenge: String,
    val attestation: Attestation
)

/**
 * Creates and registers a new passkey for the given user and RP.
 *
 * @param activity Activity to present Credential Manager UI (anchor analogue).
 * @param user PasskeyUser (id/name/displayName). Only `id` & `name` are used in JSON.
 * @param rp RelyingParty (id = domain, name = display name).
 * @param excludeCredentials Optional list of credential IDs (raw bytes) to exclude.
 */
public suspend fun createPasskey(
    activity: Activity,
    user: PasskeyUser,
    rp: RelyingParty,
    excludeCredentials: List<ByteArray> = emptyList(),
): PasskeyRegistrationResult {
    val service = com.turnkey.internal.PasskeyRequestBuilder(
        rpId = rp.id,
        activity = activity
    )
    val runner = PasskeyOperationRunner(activity = activity, service = service)
    return runner.register(
        user = user,
        exclude = excludeCredentials,
    )
}

public class PasskeyStamper(
    private val activity: Activity,
    rpId: String
) {
    private val session: PasskeyOperationRunner

    init {
        val service = com.turnkey.internal.PasskeyRequestBuilder(
            rpId = rpId,
            activity = activity
        )
        session = PasskeyOperationRunner(activity = activity, service = service)
    }

    /**
     * Performs a passkey assertion over the provided challenge.
     *
     * @param challenge Raw challenge bytes.
     * @param allowedCredentials Optional allow list of credential IDs (raw bytes).
     */
    public suspend fun assert(
        challenge: ByteArray,
        allowedCredentials: List<ByteArray>? = null,
    ): AssertionResult {
        return session.assert(
            challenge = challenge,
            allowed = allowedCredentials,
        )
    }
}