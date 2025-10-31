package com.turnkey.internal

import android.app.Activity
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.turnkey.utils.PasskeyError
import com.turnkey.passkey.PasskeyRegistrationResult
import com.turnkey.passkey.AssertionResult
import com.turnkey.passkey.PasskeyUser
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.SecureRandom

class PasskeyOperationRunner(
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
    ): PasskeyRegistrationResult = opMutex.withLock {
        val userId = user.id.toByteArray(Charsets.UTF_8)
        if (userId.isEmpty()) throw PasskeyError.InvalidUserId

        val challenge = randomBytes(32)

        val request: CreatePublicKeyCredentialRequest = service.makeRegistrationRequest(
            userId = userId,
            userName = user.name,
            challenge = challenge,
            excludeCredentials = exclude,
        )

        val createResp: CreateCredentialResponse = try {
            credentialManager.createCredential(
                context = activity,
                request = request,
            )
        } catch (e: CreateCredentialException) {
            throw PasskeyError.RegistrationFailed(e)
        }

        service.handleRegistrationResult(createResp as CreatePublicKeyCredentialResponse)
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
    ): AssertionResult = opMutex.withLock {
        val request: GetCredentialRequest = service.makeAssertionRequest(
            challenge = challenge,
            allowedCredentials = allowed,
        )

        val getResp: GetCredentialResponse = try {
            credentialManager.getCredential(
                request = request,
                context = activity
            )
        } catch (e: GetCredentialException) {
            throw PasskeyError.AssertionFailed(e)
        }

        val cred = getResp.credential
        val pk = cred as? PublicKeyCredential
            ?: throw PasskeyError.UnsupportedOperation

        service.handleAssertionResult(pk)
    }

    private fun randomBytes(n: Int): ByteArray = ByteArray(n).also(rng::nextBytes)
}
