package com.turnkey.passkey

data class AssertionResult (
    val credentialId: ByteArray,
    val signature: ByteArray,
    val authenticatorData: ByteArray,
    val clientDataBytes: ByteArray,
    val userHandle: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssertionResult

        if (!credentialId.contentEquals(other.credentialId)) return false
        if (!signature.contentEquals(other.signature)) return false
        if (!authenticatorData.contentEquals(other.authenticatorData)) return false
        if (!clientDataBytes.contentEquals(other.clientDataBytes)) return false
        if (!userHandle.contentEquals(other.userHandle)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = credentialId.hashCode()
        result = 31 * result + signature.contentHashCode()
        result = 31 * result + authenticatorData.contentHashCode()
        result = 31 * result + clientDataBytes.hashCode()
        result = 31 * result + (userHandle?.contentHashCode() ?: 0)
        return result
    }
}