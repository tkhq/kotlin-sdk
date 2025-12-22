package com.turnkey.crypto.utils

/**
 * Constants used throughout the Turnkey crypto operations.
 */
object TurnkeyConstants {
    /** Production enclave signer public key (P-256, uncompressed X9.62 format). */
    const val PRODUCTION_SIGNER_PUBLIC_KEY = "04cf288fe433cc4e1aa0ce1632feac4ea26bf2f5a09dcfe5a42c398e06898710330f0572882f4dbdf0f5304b8fc8703acd69adca9a4bbf7f5d00d20a5e364b2569"
    
    /** HPKE info string used for encryption context. */
    val hpkeInfo = "turnkey_hpke".toByteArray()
}