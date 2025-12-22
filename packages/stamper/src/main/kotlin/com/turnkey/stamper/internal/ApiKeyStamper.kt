package com.turnkey.stamper.internal

import com.turnkey.crypto.P256
import org.bouncycastle.asn1.ASN1EncodableVector
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequence
import com.turnkey.encoding.decodeHex
import java.math.BigInteger
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import com.turnkey.encoding.toBase64Url
import com.turnkey.stamper.utils.SignatureFormat
import com.turnkey.stamper.utils.TurnkeyStamperError
import org.bouncycastle.crypto.params.ECDomainParameters


internal object ApiKeyStamper {
    @Throws(TurnkeyStamperError::class)
    fun stamp(
        payloadSha256: ByteArray,
        publicKeyHex: String,
        privateKeyHex: String
    ): String {
        try {
            if (payloadSha256.size != 32) throw TurnkeyStamperError.InvalidDigestLength(
                payloadSha256.size
            )

            // parse private scalar (32 bytes)
            val dBytes = try {
                decodeHex(privateKeyHex)
            } catch (t: Throwable) {
                throw TurnkeyStamperError.InvalidHexCharacter(t)
            }
            if (dBytes.size != 32) throw TurnkeyStamperError.InvalidPrivateKeyBytes(dBytes.size, 32)
            val d = BigInteger(1, dBytes)
            if (d <= BigInteger.ZERO || d >= P256.domain.n) throw TurnkeyStamperError.InvalidPrivateKey(
                IllegalArgumentException("d bytes is invalid")
            )

            // derive compressed public key from scalar; compare to expected
            val q = P256.domain.g.multiply(d).normalize()
            val derivedCompressedHex = q.getEncoded(true).toHexString()
            if (!derivedCompressedHex.equals(publicKeyHex.lowercase(), ignoreCase = true)) {
                throw TurnkeyStamperError.MismatchedPublicKey(publicKeyHex, derivedCompressedHex)
            }

            val signatureHex = sign(payloadSha256, privateKeyHex, SignatureFormat.der)

            val json =
                """{"publicKey":"${publicKeyHex.lowercase()}","scheme":"SIGNATURE_SCHEME_TK_API_P256","signature":"$signatureHex"}"""
            return json.toByteArray().toBase64Url()
        } catch (t: Throwable) {
            throw TurnkeyStamperError.FailedToStamp(t)
        }
    }

    /**
     * Signs a SHA-256 digest using a P-256 private key and returns the signature as hex.
     *
     * @param payloadSha256 32-byte SHA-256 digest to sign.
     * @param privateKeyHex Private key scalar in hex.
     * @param format Output signature format (DER or raw r||s).
     */
    @Throws(TurnkeyStamperError::class)
    fun sign(
        payloadSha256: ByteArray,
        privateKeyHex: String,
        format: SignatureFormat,
    ): String {
        try {
            if (payloadSha256.size != 32) {
                throw TurnkeyStamperError.InvalidDigestLength(payloadSha256.size)
            }

            val dBytes = try {
                decodeHex(privateKeyHex)
            } catch (t: Throwable) {
                throw TurnkeyStamperError.InvalidHexCharacter(t)
            }
            if (dBytes.size != 32) throw TurnkeyStamperError.InvalidPrivateKeyBytes(dBytes.size, 32)

            val d = BigInteger(1, dBytes)
            val n = P256.domain.n
            if (d <= BigInteger.ZERO || d >= n) throw TurnkeyStamperError.InvalidPrivateKey(
                IllegalArgumentException("d bytes is invalid")
            )

            // ECDSA(sign) over the provided digest (no pre-hash) with RFC6979(HMAC-SHA256), low-S enforced
            val priv = ECPrivateKeyParameters(
                d,
                ECDomainParameters(P256.domain.curve, P256.domain.g, P256.domain.n)
            )
            val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
            signer.init(true, priv)

            val (rRaw, sRaw) = signer.generateSignature(payloadSha256).let { sig ->
                sig[0] to sig[1]
            }

            // low-S canonicalization
            val s = if (sRaw > n.shiftRight(1)) n.subtract(sRaw) else sRaw

            return when (format) {
                SignatureFormat.der -> {
                    val derSig = DERSequence(ASN1EncodableVector().apply {
                        add(ASN1Integer(rRaw))
                        add(ASN1Integer(s))
                    }).encoded
                    derSig.toHexString()
                }

                SignatureFormat.raw -> {
                    fun BigInteger.toFixed32(): ByteArray {
                        val bytes = this.toByteArray()
                        return when {
                            bytes.size == 32 -> bytes
                            bytes.size < 32 -> ByteArray(32 - bytes.size) + bytes
                            // handle possible leading sign byte
                            else -> bytes.copyOfRange(bytes.size - 32, bytes.size)
                        }
                    }

                    val rBytes = rRaw.toFixed32()
                    val sBytes = s.toFixed32()
                    (rBytes + sBytes).toHexString()
                }
            }
        } catch (t: Throwable) {
            throw TurnkeyStamperError.FailedToSign(t)
        }
    }
}