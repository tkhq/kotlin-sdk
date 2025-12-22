package com.turnkey.passkey.internal

import android.app.Activity
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import com.turnkey.passkey.utils.TurnkeyPasskeyError
import com.turnkey.passkey.PasskeyRegistrationResult
import com.turnkey.passkey.PasskeyUser
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.SecureRandom

internal class PasskeyOperationRunner(
    private val activity: Activity,
    private val service: PasskeyRequestBuilder,
    private val credentialManager: CredentialManager = CredentialManager.create(activity)
) {
    private val rng = SecureRandom()
    private val opMutex = Mutex()

    /**
     * Starts a passkey registration (create) flow.
     *
     * @param user      user's id/name (id will be UTF-8 encoded)
     * @param exclude   existing credential IDs to exclude (raw bytes)
     */
    suspend fun register(
        user: PasskeyUser,
        exclude: List<ByteArray>,
    ): PasskeyRegistrationResult {
        try {
            opMutex.withLock {
                val userId = user.id.toByteArray(Charsets.UTF_8)
                if (userId.isEmpty()) throw IllegalStateException("User ID not found in passkey registration")

                val challenge = randomBytes(32)

                val request: CreatePublicKeyCredentialRequest = service.makeRegistrationRequest(
                    userId = userId,
                    userName = user.name,
                    challenge = challenge,
                    excludeCredentials = exclude,
                )

                val createResp: CreateCredentialResponse = credentialManager.createCredential(
                    context = activity,
                    request = request,
                )

                return service.handleRegistrationResult(createResp as CreatePublicKeyCredentialResponse)
            }
        } catch (t: Throwable) {
            throw TurnkeyPasskeyError.RegistrationFailed(t)
        }
    }

    /**
     * Starts a passkey assertion (get) flow.
     *
     * @param challenge server-provided challenge (raw bytes)
     * @param allowed   optional allow-list of credential IDs (raw bytes)
     */
    suspend fun assert(
        challenge: ByteArray,
        allowed: List<ByteArray>?,
    ): AssertionResult {
        try {
            return opMutex.withLock {
                val request: GetCredentialRequest = service.makeAssertionRequest(
                    challenge = challenge,
                    allowedCredentials = allowed,
                )

                val getResp: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                val cred = getResp.credential
                val pk = cred as? PublicKeyCredential
                    ?: throw IllegalStateException("Public key credential not found")

                service.handleAssertionResult(pk)
            }
        } catch (t: Throwable) {
            throw TurnkeyPasskeyError.AssertionFailed(t)
        }
    }

    private fun randomBytes(n: Int): ByteArray = ByteArray(n).also(rng::nextBytes)
}
