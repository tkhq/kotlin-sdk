package com.turnkey.passkey
import kotlinx.serialization.Serializable

@Serializable
enum class Transport(val value: String) {
    ble("AUTHENTICATOR_TRANSPORT_BLE"),
    internalTransport("AUTHENTICATOR_TRANSPORT_INTERNAL"),
    nfc("AUTHENTICATOR_TRANSPORT_NFC"),
    usb("AUTHENTICATOR_TRANSPORT_USB"),
    hybrid("AUTHENTICATOR_TRANSPORT_HYBRID")
}

@Serializable
data class Attestation (
    val credentialId: String,
    val clientDataJson: String,
    val attestationObject: String,
    val transports: List<Transport>
)

data class AssertionResult (
    val credentialId: String,
    val signature: ByteArray,
    val authenticatorData: ByteArray,
    val clientDataJson: String,
    val userHandle: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssertionResult

        if (credentialId != other.credentialId) return false
        if (!signature.contentEquals(other.signature)) return false
        if (!authenticatorData.contentEquals(other.authenticatorData)) return false
        if (clientDataJson != other.clientDataJson) return false
        if (!userHandle.contentEquals(other.userHandle)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = credentialId.hashCode()
        result = 31 * result + signature.contentHashCode()
        result = 31 * result + authenticatorData.contentHashCode()
        result = 31 * result + clientDataJson.hashCode()
        result = 31 * result + (userHandle?.contentHashCode() ?: 0)
        return result
    }
}