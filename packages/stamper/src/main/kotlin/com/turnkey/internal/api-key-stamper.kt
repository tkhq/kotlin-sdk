package com.turnkey.internal

import org.bouncycastle.asn1.ASN1EncodableVector
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequence
import com.turnkey.encoding.decodeHex
import com.turnkey.utils.ApiKeyStampError
import java.math.BigInteger
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import com.turnkey.encoding.toBase64Url
import org.bouncycastle.crypto.params.ECDomainParameters
import java.security.MessageDigest


object ApiKeyStamper {
    @Throws(ApiKeyStampError::class)
    suspend fun stamp(
        payloadSha256: ByteArray,
        publicKeyHex: String,
        privateKeyHex: String
    ): String {
        if (payloadSha256.size != 32) throw ApiKeyStampError.InvalidDigestLength(payloadSha256.size)

        // parse private scalar (32 bytes)
        val dBytes = try {
            decodeHex(privateKeyHex)
        } catch (_: Exception) {
            throw ApiKeyStampError.InvalidHexCharacter
        }
        if (dBytes.size != 32) throw ApiKeyStampError.InvalidPrivateKey
        val d = BigInteger(1, dBytes)
        if (d <= BigInteger.ZERO || d >= P256.domain.n) throw ApiKeyStampError.InvalidPrivateKey

        // derive compressed public key from scalar; compare to expected
        val q = P256.domain.g.multiply(d).normalize()
        val derivedCompressedHex = q.getEncoded(true).toHexString()
        if (!derivedCompressedHex.equals(publicKeyHex.lowercase(), ignoreCase = true)) {
            throw ApiKeyStampError.MismatchedPublicKey(publicKeyHex, derivedCompressedHex)
        }

        // ECDSA(sign) over the provided digest (no pre-hash) with RFC6979(HMAC-SHA256), low-S enforced
        val priv = ECPrivateKeyParameters(d, ECDomainParameters(P256.domain.curve, P256.domain.g, P256.domain.n))
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        signer.init(true, priv)

        val (rRaw, sRaw) = signer.generateSignature(payloadSha256).let { it[0] to it[1] }

        // low-S canonicalization
        val s = if (sRaw > P256.domain.n.shiftRight(1)) P256.domain.n.subtract(sRaw) else sRaw

        val derSig = DERSequence(ASN1EncodableVector().apply {
            add(ASN1Integer(rRaw))
            add(ASN1Integer(s))
        }).encoded

        // JSON → base64url (no padding)
        val json = """{"publicKey":"${publicKeyHex.lowercase()}","scheme":"SIGNATURE_SCHEME_TK_API_P256","signature":"${derSig.toHexString()}"}"""
        return json.toByteArray().toBase64Url()
    }

    suspend fun stampRawPayload(payload: ByteArray, publicKeyHex: String, privateKeyHex: String): String =
        stamp(MessageDigest.getInstance("SHA-256").digest(payload), publicKeyHex, privateKeyHex)
}