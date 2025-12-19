package com.turnkey.crypto

import com.turnkey.crypto.utils.TurnkeyCryptoError
import kotlin.test.*

class EncryptWalletApiTest {

    @Test
    fun invalid_import_bundle_json_throws_DecodingFailed() {
        val ex = assertFailsWith<TurnkeyCryptoError> {
            encryptWalletToBundle(
                mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
                importBundle = "not-json",
                userId = "user-123",
                organizationId = "org-123",
                dangerouslyOverrideSignerPublicKey = null
            )
        }
        assertTrue(ex is TurnkeyCryptoError.OperationFailed)
    }

    @Test
    fun signature_verification_failure_bubbles() {
        val bundle = """{ "enclaveQuorumPublic":"x","dataSignature":"y","data":"00"}"""

        val ex = assertFailsWith<TurnkeyCryptoError> {
            encryptWalletToBundle(
                mnemonic = "test test test",
                importBundle = bundle,
                userId = "user-123",
                organizationId = "org-123",
                dangerouslyOverrideSignerPublicKey = null
            )
        }
        assertTrue(ex is TurnkeyCryptoError.SignerMismatch)
    }
}
