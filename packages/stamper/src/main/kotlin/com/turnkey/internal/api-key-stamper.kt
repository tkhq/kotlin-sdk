package com.turnkey.internal

import org.bouncycastle.asn1.ASN1EncodableVector
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequence
import com.turnkey.encoding.decodeHex
import com.turnkey.utils.ApiKeyStampError
import java.math.BigInteger
import org.bouncycastle.asn1.nist.NISTNamedCurves
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.bouncycastle.math.ec.ECPoint
import com.turnkey.encoding.toBase64Url
import org.bouncycastle.crypto.params.ECDomainParameters
import java.security.MessageDigest

private object P256 {
    private val x9 = NISTNamedCurves.getByName("P-256")
    val curve = x9.curve
    val g: ECPoint = x9.g
    val n: BigInteger = x9.n
}

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
        if (d <= BigInteger.ZERO || d >= P256.n) throw ApiKeyStampError.InvalidPrivateKey

        // derive compressed public key from scalar; compare to expected
        val q = P256.g.multiply(d).normalize()
        val derivedCompressedHex = q.getEncoded(true).toHexString()
        if (!derivedCompressedHex.equals(publicKeyHex.lowercase(), ignoreCase = true)) {
            throw ApiKeyStampError.MismatchedPublicKey(publicKeyHex, derivedCompressedHex)
        }

        // ECDSA(sign) over the provided digest (no pre-hash) with RFC6979(HMAC-SHA256), low-S enforced
        val priv = ECPrivateKeyParameters(d, ECDomainParameters(P256.curve, P256.g, P256.n))
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        signer.init(true, priv)

        val (rRaw, sRaw) = signer.generateSignature(payloadSha256).let { it[0] to it[1] }

        // low-S canonicalization
        val s = if (sRaw > P256.n.shiftRight(1)) P256.n.subtract(sRaw) else sRaw

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