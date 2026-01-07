package com.turnkey.passkey

import android.app.Activity
import androidx.fragment.app.Fragment
import com.turnkey.passkey.internal.AssertionResult
import com.turnkey.passkey.internal.PasskeyOperationRunner
import com.turnkey.passkey.internal.PasskeyRequestBuilder
import com.turnkey.passkey.utils.TurnkeyPasskeyError
import com.turnkey.types.V1Attestation
import kotlinx.serialization.Serializable

data class PasskeyUser(
    val id: String,
    val name: String,
    val displayName: String,
)

@Serializable
data class PasskeyRegistrationResult(
    val challenge: String,
    val attestation: V1Attestation
)

/**
 * Creates and registers a new passkey for the given user and RP.
 *
 * @param activity Activity to present Credential Manager UI (anchor analogue).
 * @param user PasskeyUser (id/name/displayName). Only `id` & `name` are used in JSON.
 * @param rpId rpId for passkey creation
 * @param excludeCredentials Optional list of credential IDs (raw bytes) to exclude.
 */
suspend fun createPasskey(
    activity: Activity,
    user: PasskeyUser,
    rpId: String,
    excludeCredentials: List<ByteArray>? = emptyList(),
): PasskeyRegistrationResult {
    try {
        val service = PasskeyRequestBuilder(
            rpId = rpId,
        )
        val excludeCredentials = excludeCredentials ?: emptyList()
        val runner = PasskeyOperationRunner(activity = activity, service = service)
        return runner.register(
            user = user,
            exclude = excludeCredentials,
        )
    } catch (t: Throwable) {
        throw TurnkeyPasskeyError.wrap(t)
    }
}

suspend fun createPasskey(
    fragment: Fragment,
    user: PasskeyUser,
    rpId: String,
    excludeCredentials: List<ByteArray> = emptyList()
): PasskeyRegistrationResult =
    createPasskey(fragment.requireActivity(), user, rpId, excludeCredentials)

class PasskeyStamper(
    private val activity: Activity,
    private val allowedCredentials: List<ByteArray>? = null,
    rpId: String
) {
    private val session: PasskeyOperationRunner

    init {
        val service = PasskeyRequestBuilder(
            rpId = rpId,
            allowed = allowedCredentials
        )
        session = PasskeyOperationRunner(activity = activity, service = service)
    }

    /**
     * Performs a passkey assertion over the provided challenge.
     *
     * @param challenge Raw challenge bytes.
     * @param allowedCredentials Optional allow list of credential IDs (raw bytes).
     */
    suspend fun assert(
        challenge: ByteArray,
        allowedCredentials: List<ByteArray>? = null,
    ): AssertionResult {
        return session.assert(
            challenge = challenge,
            allowed = allowedCredentials ?: this.allowedCredentials,
        )
    }
}