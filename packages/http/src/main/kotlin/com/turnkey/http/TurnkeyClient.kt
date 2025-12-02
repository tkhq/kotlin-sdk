@file:Suppress(
  "unused",
  "UNUSED_PARAMETER",
  "UNUSED_VARIABLE",
  "RedundantVisibilityModifier",
  "MemberVisibilityCanBePrivate",
  "RedundantSuspendModifier",
)

package com.turnkey.http

import com.turnkey.http.utils.TurnkeyHttpErrors
import com.turnkey.stamper.Stamper
import com.turnkey.types.ProxyTGetAccountBody
import com.turnkey.types.ProxyTGetAccountResponse
import com.turnkey.types.ProxyTGetWalletKitConfigBody
import com.turnkey.types.ProxyTGetWalletKitConfigResponse
import com.turnkey.types.ProxyTInitOtpBody
import com.turnkey.types.ProxyTInitOtpResponse
import com.turnkey.types.ProxyTOAuth2AuthenticateBody
import com.turnkey.types.ProxyTOAuth2AuthenticateResponse
import com.turnkey.types.ProxyTOAuthLoginBody
import com.turnkey.types.ProxyTOAuthLoginResponse
import com.turnkey.types.ProxyTOtpLoginBody
import com.turnkey.types.ProxyTOtpLoginResponse
import com.turnkey.types.ProxyTSignupBody
import com.turnkey.types.ProxyTSignupResponse
import com.turnkey.types.ProxyTVerifyOtpBody
import com.turnkey.types.ProxyTVerifyOtpResponse
import com.turnkey.types.TApproveActivityBody
import com.turnkey.types.TApproveActivityResponse
import com.turnkey.types.TCreateApiKeysBody
import com.turnkey.types.TCreateApiKeysResponse
import com.turnkey.types.TCreateApiOnlyUsersBody
import com.turnkey.types.TCreateApiOnlyUsersResponse
import com.turnkey.types.TCreateAuthenticatorsBody
import com.turnkey.types.TCreateAuthenticatorsResponse
import com.turnkey.types.TCreateFiatOnRampCredentialBody
import com.turnkey.types.TCreateFiatOnRampCredentialResponse
import com.turnkey.types.TCreateInvitationsBody
import com.turnkey.types.TCreateInvitationsResponse
import com.turnkey.types.TCreateOauth2CredentialBody
import com.turnkey.types.TCreateOauth2CredentialResponse
import com.turnkey.types.TCreateOauthProvidersBody
import com.turnkey.types.TCreateOauthProvidersResponse
import com.turnkey.types.TCreatePoliciesBody
import com.turnkey.types.TCreatePoliciesResponse
import com.turnkey.types.TCreatePolicyBody
import com.turnkey.types.TCreatePolicyResponse
import com.turnkey.types.TCreatePrivateKeyTagBody
import com.turnkey.types.TCreatePrivateKeyTagResponse
import com.turnkey.types.TCreatePrivateKeysBody
import com.turnkey.types.TCreatePrivateKeysResponse
import com.turnkey.types.TCreateReadOnlySessionBody
import com.turnkey.types.TCreateReadOnlySessionResponse
import com.turnkey.types.TCreateReadWriteSessionBody
import com.turnkey.types.TCreateReadWriteSessionResponse
import com.turnkey.types.TCreateSmartContractInterfaceBody
import com.turnkey.types.TCreateSmartContractInterfaceResponse
import com.turnkey.types.TCreateSubOrganizationBody
import com.turnkey.types.TCreateSubOrganizationResponse
import com.turnkey.types.TCreateUserTagBody
import com.turnkey.types.TCreateUserTagResponse
import com.turnkey.types.TCreateUsersBody
import com.turnkey.types.TCreateUsersResponse
import com.turnkey.types.TCreateWalletAccountsBody
import com.turnkey.types.TCreateWalletAccountsResponse
import com.turnkey.types.TCreateWalletBody
import com.turnkey.types.TCreateWalletResponse
import com.turnkey.types.TDeleteApiKeysBody
import com.turnkey.types.TDeleteApiKeysResponse
import com.turnkey.types.TDeleteAuthenticatorsBody
import com.turnkey.types.TDeleteAuthenticatorsResponse
import com.turnkey.types.TDeleteFiatOnRampCredentialBody
import com.turnkey.types.TDeleteFiatOnRampCredentialResponse
import com.turnkey.types.TDeleteInvitationBody
import com.turnkey.types.TDeleteInvitationResponse
import com.turnkey.types.TDeleteOauth2CredentialBody
import com.turnkey.types.TDeleteOauth2CredentialResponse
import com.turnkey.types.TDeleteOauthProvidersBody
import com.turnkey.types.TDeleteOauthProvidersResponse
import com.turnkey.types.TDeletePoliciesBody
import com.turnkey.types.TDeletePoliciesResponse
import com.turnkey.types.TDeletePolicyBody
import com.turnkey.types.TDeletePolicyResponse
import com.turnkey.types.TDeletePrivateKeyTagsBody
import com.turnkey.types.TDeletePrivateKeyTagsResponse
import com.turnkey.types.TDeletePrivateKeysBody
import com.turnkey.types.TDeletePrivateKeysResponse
import com.turnkey.types.TDeleteSmartContractInterfaceBody
import com.turnkey.types.TDeleteSmartContractInterfaceResponse
import com.turnkey.types.TDeleteSubOrganizationBody
import com.turnkey.types.TDeleteSubOrganizationResponse
import com.turnkey.types.TDeleteUserTagsBody
import com.turnkey.types.TDeleteUserTagsResponse
import com.turnkey.types.TDeleteUsersBody
import com.turnkey.types.TDeleteUsersResponse
import com.turnkey.types.TDeleteWalletAccountsBody
import com.turnkey.types.TDeleteWalletAccountsResponse
import com.turnkey.types.TDeleteWalletsBody
import com.turnkey.types.TDeleteWalletsResponse
import com.turnkey.types.TEmailAuthBody
import com.turnkey.types.TEmailAuthResponse
import com.turnkey.types.TEthSendRawTransactionBody
import com.turnkey.types.TEthSendRawTransactionResponse
import com.turnkey.types.TEthSendTransactionBody
import com.turnkey.types.TEthSendTransactionResponse
import com.turnkey.types.TExportPrivateKeyBody
import com.turnkey.types.TExportPrivateKeyResponse
import com.turnkey.types.TExportWalletAccountBody
import com.turnkey.types.TExportWalletAccountResponse
import com.turnkey.types.TExportWalletBody
import com.turnkey.types.TExportWalletResponse
import com.turnkey.types.TGetActivitiesBody
import com.turnkey.types.TGetActivitiesResponse
import com.turnkey.types.TGetActivityBody
import com.turnkey.types.TGetActivityResponse
import com.turnkey.types.TGetApiKeyBody
import com.turnkey.types.TGetApiKeyResponse
import com.turnkey.types.TGetApiKeysBody
import com.turnkey.types.TGetApiKeysResponse
import com.turnkey.types.TGetAppProofsBody
import com.turnkey.types.TGetAppProofsResponse
import com.turnkey.types.TGetAttestationDocumentBody
import com.turnkey.types.TGetAttestationDocumentResponse
import com.turnkey.types.TGetAuthenticatorBody
import com.turnkey.types.TGetAuthenticatorResponse
import com.turnkey.types.TGetAuthenticatorsBody
import com.turnkey.types.TGetAuthenticatorsResponse
import com.turnkey.types.TGetBootProofBody
import com.turnkey.types.TGetBootProofResponse
import com.turnkey.types.TGetLatestBootProofBody
import com.turnkey.types.TGetLatestBootProofResponse
import com.turnkey.types.TGetOauth2CredentialBody
import com.turnkey.types.TGetOauth2CredentialResponse
import com.turnkey.types.TGetOauthProvidersBody
import com.turnkey.types.TGetOauthProvidersResponse
import com.turnkey.types.TGetOnRampTransactionStatusBody
import com.turnkey.types.TGetOnRampTransactionStatusResponse
import com.turnkey.types.TGetOrganizationBody
import com.turnkey.types.TGetOrganizationConfigsBody
import com.turnkey.types.TGetOrganizationConfigsResponse
import com.turnkey.types.TGetOrganizationResponse
import com.turnkey.types.TGetPoliciesBody
import com.turnkey.types.TGetPoliciesResponse
import com.turnkey.types.TGetPolicyBody
import com.turnkey.types.TGetPolicyEvaluationsBody
import com.turnkey.types.TGetPolicyEvaluationsResponse
import com.turnkey.types.TGetPolicyResponse
import com.turnkey.types.TGetPrivateKeyBody
import com.turnkey.types.TGetPrivateKeyResponse
import com.turnkey.types.TGetPrivateKeysBody
import com.turnkey.types.TGetPrivateKeysResponse
import com.turnkey.types.TGetSmartContractInterfaceBody
import com.turnkey.types.TGetSmartContractInterfaceResponse
import com.turnkey.types.TGetSmartContractInterfacesBody
import com.turnkey.types.TGetSmartContractInterfacesResponse
import com.turnkey.types.TGetSubOrgIdsBody
import com.turnkey.types.TGetSubOrgIdsResponse
import com.turnkey.types.TGetUserBody
import com.turnkey.types.TGetUserResponse
import com.turnkey.types.TGetUsersBody
import com.turnkey.types.TGetUsersResponse
import com.turnkey.types.TGetVerifiedSubOrgIdsBody
import com.turnkey.types.TGetVerifiedSubOrgIdsResponse
import com.turnkey.types.TGetWalletAccountBody
import com.turnkey.types.TGetWalletAccountResponse
import com.turnkey.types.TGetWalletAccountsBody
import com.turnkey.types.TGetWalletAccountsResponse
import com.turnkey.types.TGetWalletBody
import com.turnkey.types.TGetWalletResponse
import com.turnkey.types.TGetWalletsBody
import com.turnkey.types.TGetWalletsResponse
import com.turnkey.types.TGetWhoamiBody
import com.turnkey.types.TGetWhoamiResponse
import com.turnkey.types.TImportPrivateKeyBody
import com.turnkey.types.TImportPrivateKeyResponse
import com.turnkey.types.TImportWalletBody
import com.turnkey.types.TImportWalletResponse
import com.turnkey.types.TInitFiatOnRampBody
import com.turnkey.types.TInitFiatOnRampResponse
import com.turnkey.types.TInitImportPrivateKeyBody
import com.turnkey.types.TInitImportPrivateKeyResponse
import com.turnkey.types.TInitImportWalletBody
import com.turnkey.types.TInitImportWalletResponse
import com.turnkey.types.TInitOtpAuthBody
import com.turnkey.types.TInitOtpAuthResponse
import com.turnkey.types.TInitOtpBody
import com.turnkey.types.TInitOtpResponse
import com.turnkey.types.TInitUserEmailRecoveryBody
import com.turnkey.types.TInitUserEmailRecoveryResponse
import com.turnkey.types.TListFiatOnRampCredentialsBody
import com.turnkey.types.TListFiatOnRampCredentialsResponse
import com.turnkey.types.TListOauth2CredentialsBody
import com.turnkey.types.TListOauth2CredentialsResponse
import com.turnkey.types.TListPrivateKeyTagsBody
import com.turnkey.types.TListPrivateKeyTagsResponse
import com.turnkey.types.TListUserTagsBody
import com.turnkey.types.TListUserTagsResponse
import com.turnkey.types.TNOOPCodegenAnchorResponse
import com.turnkey.types.TOauth2AuthenticateBody
import com.turnkey.types.TOauth2AuthenticateResponse
import com.turnkey.types.TOauthBody
import com.turnkey.types.TOauthLoginBody
import com.turnkey.types.TOauthLoginResponse
import com.turnkey.types.TOauthResponse
import com.turnkey.types.TOtpAuthBody
import com.turnkey.types.TOtpAuthResponse
import com.turnkey.types.TOtpLoginBody
import com.turnkey.types.TOtpLoginResponse
import com.turnkey.types.TRecoverUserBody
import com.turnkey.types.TRecoverUserResponse
import com.turnkey.types.TRejectActivityBody
import com.turnkey.types.TRejectActivityResponse
import com.turnkey.types.TRemoveOrganizationFeatureBody
import com.turnkey.types.TRemoveOrganizationFeatureResponse
import com.turnkey.types.TSetOrganizationFeatureBody
import com.turnkey.types.TSetOrganizationFeatureResponse
import com.turnkey.types.TSignRawPayloadBody
import com.turnkey.types.TSignRawPayloadResponse
import com.turnkey.types.TSignRawPayloadsBody
import com.turnkey.types.TSignRawPayloadsResponse
import com.turnkey.types.TSignTransactionBody
import com.turnkey.types.TSignTransactionResponse
import com.turnkey.types.TSignedRequest
import com.turnkey.types.TStamp
import com.turnkey.types.TStampLoginBody
import com.turnkey.types.TStampLoginResponse
import com.turnkey.types.TTestRateLimitsBody
import com.turnkey.types.TTestRateLimitsResponse
import com.turnkey.types.TUpdateFiatOnRampCredentialBody
import com.turnkey.types.TUpdateFiatOnRampCredentialResponse
import com.turnkey.types.TUpdateOauth2CredentialBody
import com.turnkey.types.TUpdateOauth2CredentialResponse
import com.turnkey.types.TUpdatePolicyBody
import com.turnkey.types.TUpdatePolicyResponse
import com.turnkey.types.TUpdatePrivateKeyTagBody
import com.turnkey.types.TUpdatePrivateKeyTagResponse
import com.turnkey.types.TUpdateRootQuorumBody
import com.turnkey.types.TUpdateRootQuorumResponse
import com.turnkey.types.TUpdateUserBody
import com.turnkey.types.TUpdateUserEmailBody
import com.turnkey.types.TUpdateUserEmailResponse
import com.turnkey.types.TUpdateUserNameBody
import com.turnkey.types.TUpdateUserNameResponse
import com.turnkey.types.TUpdateUserPhoneNumberBody
import com.turnkey.types.TUpdateUserPhoneNumberResponse
import com.turnkey.types.TUpdateUserResponse
import com.turnkey.types.TUpdateUserTagBody
import com.turnkey.types.TUpdateUserTagResponse
import com.turnkey.types.TUpdateWalletBody
import com.turnkey.types.TUpdateWalletResponse
import com.turnkey.types.TVerifyOtpBody
import com.turnkey.types.TVerifyOtpResponse
import com.turnkey.types.V1ActivityResponse
import java.io.IOException
import kotlin.String
import kotlin.Suppress
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * HTTP Client for interacting with Turnkey API (generated). DO NOT EDIT BY HAND.
 */
public class TurnkeyClient(
  apiBaseUrl: String? = null,
  private val stamper: Stamper?,
  http: OkHttpClient? = null,
  authProxyUrl: String? = null,
  private val authProxyConfigId: String? = null,
) {
  private val apiBaseUrl: String = apiBaseUrl ?: "https://api.turnkey.com"

  private val http: OkHttpClient = http ?: OkHttpClient()

  private val authProxyUrl: String = authProxyUrl ?: "https://authproxy.turnkey.com"

  private val json: Json = Json { ignoreUnknownKeys = true }

  private suspend fun Call.await(): Response = suspendCancellableCoroutine { cont ->
      this@await.enqueue(object : Callback {
          override fun onFailure(call: Call, e: IOException) {
              if (!cont.isCompleted) cont.resumeWithException(e)
          }
          override fun onResponse(call: Call, response: Response) {
              if (!cont.isCompleted) cont.resume(response)
          }
      })
      cont.invokeOnCancellation { kotlin.runCatching { cancel() }.getOrNull() }
  }

  /**
   * POST `/public/v1/query/get_activity` (operationId: PublicApiService_GetActivity)
   */
  public suspend fun getActivity(input: TGetActivityBody): TGetActivityResponse {
    val url = "$apiBaseUrl/public/v1/query/get_activity"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_activity: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampGetActivity(input: TGetActivityBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_activity"
    val bodyJson = json.encodeToString(TGetActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_api_key` (operationId: PublicApiService_GetApiKey)
   */
  public suspend fun getApiKey(input: TGetApiKeyBody): TGetApiKeyResponse {
    val url = "$apiBaseUrl/public/v1/query/get_api_key"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetApiKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_api_key: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetApiKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetApiKey(input: TGetApiKeyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_api_key"
    val bodyJson = json.encodeToString(TGetApiKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_api_keys` (operationId: PublicApiService_GetApiKeys)
   */
  public suspend fun getApiKeys(input: TGetApiKeysBody): TGetApiKeysResponse {
    val url = "$apiBaseUrl/public/v1/query/get_api_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_api_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetApiKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampGetApiKeys(input: TGetApiKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_api_keys"
    val bodyJson = json.encodeToString(TGetApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_attestation` (operationId: PublicApiService_GetAttestationDocument)
   */
  public suspend fun getAttestationDocument(input: TGetAttestationDocumentBody): TGetAttestationDocumentResponse {
    val url = "$apiBaseUrl/public/v1/query/get_attestation"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetAttestationDocumentBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_attestation: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetAttestationDocumentResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAttestationDocument(input: TGetAttestationDocumentBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_attestation"
    val bodyJson = json.encodeToString(TGetAttestationDocumentBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_authenticator` (operationId: PublicApiService_GetAuthenticator)
   */
  public suspend fun getAuthenticator(input: TGetAuthenticatorBody): TGetAuthenticatorResponse {
    val url = "$apiBaseUrl/public/v1/query/get_authenticator"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetAuthenticatorBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_authenticator: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetAuthenticatorResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAuthenticator(input: TGetAuthenticatorBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_authenticator"
    val bodyJson = json.encodeToString(TGetAuthenticatorBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_authenticators` (operationId: PublicApiService_GetAuthenticators)
   */
  public suspend fun getAuthenticators(input: TGetAuthenticatorsBody): TGetAuthenticatorsResponse {
    val url = "$apiBaseUrl/public/v1/query/get_authenticators"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_authenticators: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetAuthenticatorsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAuthenticators(input: TGetAuthenticatorsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_authenticators"
    val bodyJson = json.encodeToString(TGetAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_boot_proof` (operationId: PublicApiService_GetBootProof)
   */
  public suspend fun getBootProof(input: TGetBootProofBody): TGetBootProofResponse {
    val url = "$apiBaseUrl/public/v1/query/get_boot_proof"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_boot_proof: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetBootProofResponse.serializer(), text)
    }
  }

  public suspend fun stampGetBootProof(input: TGetBootProofBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_boot_proof"
    val bodyJson = json.encodeToString(TGetBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_latest_boot_proof` (operationId: PublicApiService_GetLatestBootProof)
   */
  public suspend fun getLatestBootProof(input: TGetLatestBootProofBody): TGetLatestBootProofResponse {
    val url = "$apiBaseUrl/public/v1/query/get_latest_boot_proof"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetLatestBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_latest_boot_proof: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetLatestBootProofResponse.serializer(), text)
    }
  }

  public suspend fun stampGetLatestBootProof(input: TGetLatestBootProofBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_latest_boot_proof"
    val bodyJson = json.encodeToString(TGetLatestBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_oauth2_credential` (operationId: PublicApiService_GetOauth2Credential)
   */
  public suspend fun getOauth2Credential(input: TGetOauth2CredentialBody): TGetOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/query/get_oauth2_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_oauth2_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetOauth2CredentialResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOauth2Credential(input: TGetOauth2CredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_oauth2_credential"
    val bodyJson = json.encodeToString(TGetOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_oauth_providers` (operationId: PublicApiService_GetOauthProviders)
   */
  public suspend fun getOauthProviders(input: TGetOauthProvidersBody): TGetOauthProvidersResponse {
    val url = "$apiBaseUrl/public/v1/query/get_oauth_providers"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_oauth_providers: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetOauthProvidersResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOauthProviders(input: TGetOauthProvidersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_oauth_providers"
    val bodyJson = json.encodeToString(TGetOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_onramp_transaction_status` (operationId: PublicApiService_GetOnRampTransactionStatus)
   */
  public suspend fun getOnRampTransactionStatus(input: TGetOnRampTransactionStatusBody): TGetOnRampTransactionStatusResponse {
    val url = "$apiBaseUrl/public/v1/query/get_onramp_transaction_status"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetOnRampTransactionStatusBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_onramp_transaction_status: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetOnRampTransactionStatusResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOnRampTransactionStatus(input: TGetOnRampTransactionStatusBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_onramp_transaction_status"
    val bodyJson = json.encodeToString(TGetOnRampTransactionStatusBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_organization` (operationId: PublicApiService_GetOrganization)
   */
  public suspend fun getOrganization(input: TGetOrganizationBody): TGetOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/query/get_organization"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_organization: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetOrganizationResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOrganization(input: TGetOrganizationBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_organization"
    val bodyJson = json.encodeToString(TGetOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_organization_configs` (operationId: PublicApiService_GetOrganizationConfigs)
   */
  public suspend fun getOrganizationConfigs(input: TGetOrganizationConfigsBody): TGetOrganizationConfigsResponse {
    val url = "$apiBaseUrl/public/v1/query/get_organization_configs"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetOrganizationConfigsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_organization_configs: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetOrganizationConfigsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOrganizationConfigs(input: TGetOrganizationConfigsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_organization_configs"
    val bodyJson = json.encodeToString(TGetOrganizationConfigsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_policy` (operationId: PublicApiService_GetPolicy)
   */
  public suspend fun getPolicy(input: TGetPolicyBody): TGetPolicyResponse {
    val url = "$apiBaseUrl/public/v1/query/get_policy"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetPolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_policy: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetPolicyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicy(input: TGetPolicyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_policy"
    val bodyJson = json.encodeToString(TGetPolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_policy_evaluations` (operationId: PublicApiService_GetPolicyEvaluations)
   */
  public suspend fun getPolicyEvaluations(input: TGetPolicyEvaluationsBody): TGetPolicyEvaluationsResponse {
    val url = "$apiBaseUrl/public/v1/query/get_policy_evaluations"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetPolicyEvaluationsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_policy_evaluations: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetPolicyEvaluationsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicyEvaluations(input: TGetPolicyEvaluationsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_policy_evaluations"
    val bodyJson = json.encodeToString(TGetPolicyEvaluationsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_private_key` (operationId: PublicApiService_GetPrivateKey)
   */
  public suspend fun getPrivateKey(input: TGetPrivateKeyBody): TGetPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/query/get_private_key"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_private_key: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetPrivateKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPrivateKey(input: TGetPrivateKeyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_private_key"
    val bodyJson = json.encodeToString(TGetPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_smart_contract_interface` (operationId: PublicApiService_GetSmartContractInterface)
   */
  public suspend fun getSmartContractInterface(input: TGetSmartContractInterfaceBody): TGetSmartContractInterfaceResponse {
    val url = "$apiBaseUrl/public/v1/query/get_smart_contract_interface"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_smart_contract_interface: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetSmartContractInterfaceResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSmartContractInterface(input: TGetSmartContractInterfaceBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_smart_contract_interface"
    val bodyJson = json.encodeToString(TGetSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_user` (operationId: PublicApiService_GetUser)
   */
  public suspend fun getUser(input: TGetUserBody): TGetUserResponse {
    val url = "$apiBaseUrl/public/v1/query/get_user"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_user: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetUserResponse.serializer(), text)
    }
  }

  public suspend fun stampGetUser(input: TGetUserBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_user"
    val bodyJson = json.encodeToString(TGetUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_wallet` (operationId: PublicApiService_GetWallet)
   */
  public suspend fun getWallet(input: TGetWalletBody): TGetWalletResponse {
    val url = "$apiBaseUrl/public/v1/query/get_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWallet(input: TGetWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_wallet"
    val bodyJson = json.encodeToString(TGetWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_wallet_account` (operationId: PublicApiService_GetWalletAccount)
   */
  public suspend fun getWalletAccount(input: TGetWalletAccountBody): TGetWalletAccountResponse {
    val url = "$apiBaseUrl/public/v1/query/get_wallet_account"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetWalletAccountBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/get_wallet_account: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetWalletAccountResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWalletAccount(input: TGetWalletAccountBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/get_wallet_account"
    val bodyJson = json.encodeToString(TGetWalletAccountBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_activities` (operationId: PublicApiService_GetActivities)
   */
  public suspend fun getActivities(input: TGetActivitiesBody): TGetActivitiesResponse {
    val url = "$apiBaseUrl/public/v1/query/list_activities"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetActivitiesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_activities: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetActivitiesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetActivities(input: TGetActivitiesBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_activities"
    val bodyJson = json.encodeToString(TGetActivitiesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_app_proofs` (operationId: PublicApiService_GetAppProofs)
   */
  public suspend fun getAppProofs(input: TGetAppProofsBody): TGetAppProofsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_app_proofs"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetAppProofsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_app_proofs: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetAppProofsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAppProofs(input: TGetAppProofsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_app_proofs"
    val bodyJson = json.encodeToString(TGetAppProofsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_fiat_on_ramp_credentials` (operationId: PublicApiService_ListFiatOnRampCredentials)
   */
  public suspend fun listFiatOnRampCredentials(input: TListFiatOnRampCredentialsBody): TListFiatOnRampCredentialsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_fiat_on_ramp_credentials"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TListFiatOnRampCredentialsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_fiat_on_ramp_credentials: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TListFiatOnRampCredentialsResponse.serializer(), text)
    }
  }

  public suspend fun stampListFiatOnRampCredentials(input: TListFiatOnRampCredentialsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_fiat_on_ramp_credentials"
    val bodyJson = json.encodeToString(TListFiatOnRampCredentialsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_oauth2_credentials` (operationId: PublicApiService_ListOauth2Credentials)
   */
  public suspend fun listOauth2Credentials(input: TListOauth2CredentialsBody): TListOauth2CredentialsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_oauth2_credentials"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TListOauth2CredentialsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_oauth2_credentials: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TListOauth2CredentialsResponse.serializer(), text)
    }
  }

  public suspend fun stampListOauth2Credentials(input: TListOauth2CredentialsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_oauth2_credentials"
    val bodyJson = json.encodeToString(TListOauth2CredentialsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_policies` (operationId: PublicApiService_GetPolicies)
   */
  public suspend fun getPolicies(input: TGetPoliciesBody): TGetPoliciesResponse {
    val url = "$apiBaseUrl/public/v1/query/list_policies"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetPoliciesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_policies: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetPoliciesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicies(input: TGetPoliciesBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_policies"
    val bodyJson = json.encodeToString(TGetPoliciesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_private_key_tags` (operationId: PublicApiService_ListPrivateKeyTags)
   */
  public suspend fun listPrivateKeyTags(input: TListPrivateKeyTagsBody): TListPrivateKeyTagsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_private_key_tags"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TListPrivateKeyTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_private_key_tags: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TListPrivateKeyTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampListPrivateKeyTags(input: TListPrivateKeyTagsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_private_key_tags"
    val bodyJson = json.encodeToString(TListPrivateKeyTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_private_keys` (operationId: PublicApiService_GetPrivateKeys)
   */
  public suspend fun getPrivateKeys(input: TGetPrivateKeysBody): TGetPrivateKeysResponse {
    val url = "$apiBaseUrl/public/v1/query/list_private_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetPrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_private_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetPrivateKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPrivateKeys(input: TGetPrivateKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_private_keys"
    val bodyJson = json.encodeToString(TGetPrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_smart_contract_interfaces` (operationId: PublicApiService_GetSmartContractInterfaces)
   */
  public suspend fun getSmartContractInterfaces(input: TGetSmartContractInterfacesBody): TGetSmartContractInterfacesResponse {
    val url = "$apiBaseUrl/public/v1/query/list_smart_contract_interfaces"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetSmartContractInterfacesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_smart_contract_interfaces: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetSmartContractInterfacesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSmartContractInterfaces(input: TGetSmartContractInterfacesBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_smart_contract_interfaces"
    val bodyJson = json.encodeToString(TGetSmartContractInterfacesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_suborgs` (operationId: PublicApiService_GetSubOrgIds)
   */
  public suspend fun getSubOrgIds(input: TGetSubOrgIdsBody): TGetSubOrgIdsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_suborgs"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_suborgs: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetSubOrgIdsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSubOrgIds(input: TGetSubOrgIdsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_suborgs"
    val bodyJson = json.encodeToString(TGetSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_user_tags` (operationId: PublicApiService_ListUserTags)
   */
  public suspend fun listUserTags(input: TListUserTagsBody): TListUserTagsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_user_tags"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TListUserTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_user_tags: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TListUserTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampListUserTags(input: TListUserTagsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_user_tags"
    val bodyJson = json.encodeToString(TListUserTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_users` (operationId: PublicApiService_GetUsers)
   */
  public suspend fun getUsers(input: TGetUsersBody): TGetUsersResponse {
    val url = "$apiBaseUrl/public/v1/query/list_users"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_users: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetUsersResponse.serializer(), text)
    }
  }

  public suspend fun stampGetUsers(input: TGetUsersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_users"
    val bodyJson = json.encodeToString(TGetUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_verified_suborgs` (operationId: PublicApiService_GetVerifiedSubOrgIds)
   */
  public suspend fun getVerifiedSubOrgIds(input: TGetVerifiedSubOrgIdsBody): TGetVerifiedSubOrgIdsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_verified_suborgs"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetVerifiedSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_verified_suborgs: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetVerifiedSubOrgIdsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetVerifiedSubOrgIds(input: TGetVerifiedSubOrgIdsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_verified_suborgs"
    val bodyJson = json.encodeToString(TGetVerifiedSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_wallet_accounts` (operationId: PublicApiService_GetWalletAccounts)
   */
  public suspend fun getWalletAccounts(input: TGetWalletAccountsBody): TGetWalletAccountsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_wallet_accounts"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetWalletAccountsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_wallet_accounts: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetWalletAccountsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWalletAccounts(input: TGetWalletAccountsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_wallet_accounts"
    val bodyJson = json.encodeToString(TGetWalletAccountsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_wallets` (operationId: PublicApiService_GetWallets)
   */
  public suspend fun getWallets(input: TGetWalletsBody): TGetWalletsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_wallets"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetWalletsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/list_wallets: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetWalletsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWallets(input: TGetWalletsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/list_wallets"
    val bodyJson = json.encodeToString(TGetWalletsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/whoami` (operationId: PublicApiService_GetWhoami)
   */
  public suspend fun getWhoami(input: TGetWhoamiBody): TGetWhoamiResponse {
    val url = "$apiBaseUrl/public/v1/query/whoami"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TGetWhoamiBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/query/whoami: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TGetWhoamiResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWhoami(input: TGetWhoamiBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/query/whoami"
    val bodyJson = json.encodeToString(TGetWhoamiBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/approve_activity` (operationId: PublicApiService_ApproveActivity)
   */
  public suspend fun approveActivity(input: TApproveActivityBody): TApproveActivityResponse {
    val url = "$apiBaseUrl/public/v1/submit/approve_activity"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TApproveActivityBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_APPROVE_ACTIVITY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/approve_activity: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TApproveActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampApproveActivity(input: TApproveActivityBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/approve_activity"
    val inputElem = json.encodeToJsonElement(TApproveActivityBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_APPROVE_ACTIVITY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_api_keys` (operationId: PublicApiService_CreateApiKeys)
   */
  public suspend fun createApiKeys(input: TCreateApiKeysBody): TCreateApiKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_api_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateApiKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_API_KEYS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_api_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createApiKeysResult ?: throw RuntimeException("No result found from /public/v1/submit/create_api_keys")
      return TCreateApiKeysResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateApiKeys(input: TCreateApiKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_api_keys"
    val inputElem = json.encodeToJsonElement(TCreateApiKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_API_KEYS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_api_only_users` (operationId: PublicApiService_CreateApiOnlyUsers)
   */
  public suspend fun createApiOnlyUsers(input: TCreateApiOnlyUsersBody): TCreateApiOnlyUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_api_only_users"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateApiOnlyUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_API_ONLY_USERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_api_only_users: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createApiOnlyUsersResult ?: throw RuntimeException("No result found from /public/v1/submit/create_api_only_users")
      return TCreateApiOnlyUsersResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateApiOnlyUsers(input: TCreateApiOnlyUsersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_api_only_users"
    val inputElem = json.encodeToJsonElement(TCreateApiOnlyUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_API_ONLY_USERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_authenticators` (operationId: PublicApiService_CreateAuthenticators)
   */
  public suspend fun createAuthenticators(input: TCreateAuthenticatorsBody): TCreateAuthenticatorsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_authenticators"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateAuthenticatorsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_AUTHENTICATORS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_authenticators: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createAuthenticatorsResult ?: throw RuntimeException("No result found from /public/v1/submit/create_authenticators")
      return TCreateAuthenticatorsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateAuthenticators(input: TCreateAuthenticatorsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_authenticators"
    val inputElem = json.encodeToJsonElement(TCreateAuthenticatorsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_AUTHENTICATORS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_fiat_on_ramp_credential` (operationId: PublicApiService_CreateFiatOnRampCredential)
   */
  public suspend fun createFiatOnRampCredential(input: TCreateFiatOnRampCredentialBody): TCreateFiatOnRampCredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_fiat_on_ramp_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_fiat_on_ramp_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createFiatOnRampCredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/create_fiat_on_ramp_credential")
      return TCreateFiatOnRampCredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateFiatOnRampCredential(input: TCreateFiatOnRampCredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_fiat_on_ramp_credential"
    val inputElem = json.encodeToJsonElement(TCreateFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_invitations` (operationId: PublicApiService_CreateInvitations)
   */
  public suspend fun createInvitations(input: TCreateInvitationsBody): TCreateInvitationsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_invitations"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateInvitationsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_INVITATIONS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_invitations: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createInvitationsResult ?: throw RuntimeException("No result found from /public/v1/submit/create_invitations")
      return TCreateInvitationsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateInvitations(input: TCreateInvitationsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_invitations"
    val inputElem = json.encodeToJsonElement(TCreateInvitationsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_INVITATIONS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_oauth2_credential` (operationId: PublicApiService_CreateOauth2Credential)
   */
  public suspend fun createOauth2Credential(input: TCreateOauth2CredentialBody): TCreateOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth2_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_oauth2_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createOauth2CredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/create_oauth2_credential")
      return TCreateOauth2CredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateOauth2Credential(input: TCreateOauth2CredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_oauth2_credential"
    val inputElem = json.encodeToJsonElement(TCreateOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_oauth_providers` (operationId: PublicApiService_CreateOauthProviders)
   */
  public suspend fun createOauthProviders(input: TCreateOauthProvidersBody): TCreateOauthProvidersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth_providers"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateOauthProvidersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_OAUTH_PROVIDERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_oauth_providers: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createOauthProvidersResult ?: throw RuntimeException("No result found from /public/v1/submit/create_oauth_providers")
      return TCreateOauthProvidersResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateOauthProviders(input: TCreateOauthProvidersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_oauth_providers"
    val inputElem = json.encodeToJsonElement(TCreateOauthProvidersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_OAUTH_PROVIDERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_policies` (operationId: PublicApiService_CreatePolicies)
   */
  public suspend fun createPolicies(input: TCreatePoliciesBody): TCreatePoliciesResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_policies"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreatePoliciesBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_POLICIES"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_policies: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createPoliciesResult ?: throw RuntimeException("No result found from /public/v1/submit/create_policies")
      return TCreatePoliciesResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreatePolicies(input: TCreatePoliciesBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_policies"
    val inputElem = json.encodeToJsonElement(TCreatePoliciesBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_POLICIES"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_policy` (operationId: PublicApiService_CreatePolicy)
   */
  public suspend fun createPolicy(input: TCreatePolicyBody): TCreatePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_policy"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreatePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_POLICY_V3"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_policy: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createPolicyResult ?: throw RuntimeException("No result found from /public/v1/submit/create_policy")
      return TCreatePolicyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreatePolicy(input: TCreatePolicyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_policy"
    val inputElem = json.encodeToJsonElement(TCreatePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_POLICY_V3"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_private_key_tag` (operationId: PublicApiService_CreatePrivateKeyTag)
   */
  public suspend fun createPrivateKeyTag(input: TCreatePrivateKeyTagBody): TCreatePrivateKeyTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_private_key_tag"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreatePrivateKeyTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_PRIVATE_KEY_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_private_key_tag: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createPrivateKeyTagResult ?: throw RuntimeException("No result found from /public/v1/submit/create_private_key_tag")
      return TCreatePrivateKeyTagResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreatePrivateKeyTag(input: TCreatePrivateKeyTagBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_private_key_tag"
    val inputElem = json.encodeToJsonElement(TCreatePrivateKeyTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_PRIVATE_KEY_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_private_keys` (operationId: PublicApiService_CreatePrivateKeys)
   */
  public suspend fun createPrivateKeys(input: TCreatePrivateKeysBody): TCreatePrivateKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_private_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreatePrivateKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_PRIVATE_KEYS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_private_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createPrivateKeysResultV2 ?: throw RuntimeException("No result found from /public/v1/submit/create_private_keys")
      return TCreatePrivateKeysResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreatePrivateKeys(input: TCreatePrivateKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_private_keys"
    val inputElem = json.encodeToJsonElement(TCreatePrivateKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_PRIVATE_KEYS_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_read_only_session` (operationId: PublicApiService_CreateReadOnlySession)
   */
  public suspend fun createReadOnlySession(input: TCreateReadOnlySessionBody): TCreateReadOnlySessionResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_read_only_session"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateReadOnlySessionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_READ_ONLY_SESSION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_read_only_session: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createReadOnlySessionResult ?: throw RuntimeException("No result found from /public/v1/submit/create_read_only_session")
      return TCreateReadOnlySessionResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateReadOnlySession(input: TCreateReadOnlySessionBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_read_only_session"
    val inputElem = json.encodeToJsonElement(TCreateReadOnlySessionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_READ_ONLY_SESSION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_read_write_session` (operationId: PublicApiService_CreateReadWriteSession)
   */
  public suspend fun createReadWriteSession(input: TCreateReadWriteSessionBody): TCreateReadWriteSessionResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_read_write_session"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateReadWriteSessionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_READ_WRITE_SESSION_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_read_write_session: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createReadWriteSessionResultV2 ?: throw RuntimeException("No result found from /public/v1/submit/create_read_write_session")
      return TCreateReadWriteSessionResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateReadWriteSession(input: TCreateReadWriteSessionBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_read_write_session"
    val inputElem = json.encodeToJsonElement(TCreateReadWriteSessionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_READ_WRITE_SESSION_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_smart_contract_interface` (operationId: PublicApiService_CreateSmartContractInterface)
   */
  public suspend fun createSmartContractInterface(input: TCreateSmartContractInterfaceBody): TCreateSmartContractInterfaceResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_smart_contract_interface"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateSmartContractInterfaceBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_SMART_CONTRACT_INTERFACE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_smart_contract_interface: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createSmartContractInterfaceResult ?: throw RuntimeException("No result found from /public/v1/submit/create_smart_contract_interface")
      return TCreateSmartContractInterfaceResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateSmartContractInterface(input: TCreateSmartContractInterfaceBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_smart_contract_interface"
    val inputElem = json.encodeToJsonElement(TCreateSmartContractInterfaceBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_SMART_CONTRACT_INTERFACE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_sub_organization` (operationId: PublicApiService_CreateSubOrganization)
   */
  public suspend fun createSubOrganization(input: TCreateSubOrganizationBody): TCreateSubOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_sub_organization"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateSubOrganizationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V7"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_sub_organization: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createSubOrganizationResultV7 ?: throw RuntimeException("No result found from /public/v1/submit/create_sub_organization")
      return TCreateSubOrganizationResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateSubOrganization(input: TCreateSubOrganizationBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_sub_organization"
    val inputElem = json.encodeToJsonElement(TCreateSubOrganizationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V7"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_user_tag` (operationId: PublicApiService_CreateUserTag)
   */
  public suspend fun createUserTag(input: TCreateUserTagBody): TCreateUserTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_user_tag"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateUserTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_USER_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_user_tag: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createUserTagResult ?: throw RuntimeException("No result found from /public/v1/submit/create_user_tag")
      return TCreateUserTagResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateUserTag(input: TCreateUserTagBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_user_tag"
    val inputElem = json.encodeToJsonElement(TCreateUserTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_USER_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_users` (operationId: PublicApiService_CreateUsers)
   */
  public suspend fun createUsers(input: TCreateUsersBody): TCreateUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_users"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_USERS_V3"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_users: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createUsersResult ?: throw RuntimeException("No result found from /public/v1/submit/create_users")
      return TCreateUsersResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateUsers(input: TCreateUsersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_users"
    val inputElem = json.encodeToJsonElement(TCreateUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_USERS_V3"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_wallet` (operationId: PublicApiService_CreateWallet)
   */
  public suspend fun createWallet(input: TCreateWalletBody): TCreateWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createWalletResult ?: throw RuntimeException("No result found from /public/v1/submit/create_wallet")
      return TCreateWalletResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateWallet(input: TCreateWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_wallet"
    val inputElem = json.encodeToJsonElement(TCreateWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_wallet_accounts` (operationId: PublicApiService_CreateWalletAccounts)
   */
  public suspend fun createWalletAccounts(input: TCreateWalletAccountsBody): TCreateWalletAccountsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet_accounts"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TCreateWalletAccountsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_WALLET_ACCOUNTS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/create_wallet_accounts: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.createWalletAccountsResult ?: throw RuntimeException("No result found from /public/v1/submit/create_wallet_accounts")
      return TCreateWalletAccountsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampCreateWalletAccounts(input: TCreateWalletAccountsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/create_wallet_accounts"
    val inputElem = json.encodeToJsonElement(TCreateWalletAccountsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_CREATE_WALLET_ACCOUNTS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_api_keys` (operationId: PublicApiService_DeleteApiKeys)
   */
  public suspend fun deleteApiKeys(input: TDeleteApiKeysBody): TDeleteApiKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_api_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteApiKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_API_KEYS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_api_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteApiKeysResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_api_keys")
      return TDeleteApiKeysResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteApiKeys(input: TDeleteApiKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_api_keys"
    val inputElem = json.encodeToJsonElement(TDeleteApiKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_API_KEYS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_authenticators` (operationId: PublicApiService_DeleteAuthenticators)
   */
  public suspend fun deleteAuthenticators(input: TDeleteAuthenticatorsBody): TDeleteAuthenticatorsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_authenticators"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteAuthenticatorsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_AUTHENTICATORS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_authenticators: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteAuthenticatorsResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_authenticators")
      return TDeleteAuthenticatorsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteAuthenticators(input: TDeleteAuthenticatorsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_authenticators"
    val inputElem = json.encodeToJsonElement(TDeleteAuthenticatorsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_AUTHENTICATORS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_fiat_on_ramp_credential` (operationId: PublicApiService_DeleteFiatOnRampCredential)
   */
  public suspend fun deleteFiatOnRampCredential(input: TDeleteFiatOnRampCredentialBody): TDeleteFiatOnRampCredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_fiat_on_ramp_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_fiat_on_ramp_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteFiatOnRampCredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_fiat_on_ramp_credential")
      return TDeleteFiatOnRampCredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteFiatOnRampCredential(input: TDeleteFiatOnRampCredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_fiat_on_ramp_credential"
    val inputElem = json.encodeToJsonElement(TDeleteFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_invitation` (operationId: PublicApiService_DeleteInvitation)
   */
  public suspend fun deleteInvitation(input: TDeleteInvitationBody): TDeleteInvitationResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_invitation"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteInvitationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_INVITATION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_invitation: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteInvitationResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_invitation")
      return TDeleteInvitationResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteInvitation(input: TDeleteInvitationBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_invitation"
    val inputElem = json.encodeToJsonElement(TDeleteInvitationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_INVITATION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_oauth2_credential` (operationId: PublicApiService_DeleteOauth2Credential)
   */
  public suspend fun deleteOauth2Credential(input: TDeleteOauth2CredentialBody): TDeleteOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth2_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_oauth2_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteOauth2CredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_oauth2_credential")
      return TDeleteOauth2CredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteOauth2Credential(input: TDeleteOauth2CredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth2_credential"
    val inputElem = json.encodeToJsonElement(TDeleteOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_oauth_providers` (operationId: PublicApiService_DeleteOauthProviders)
   */
  public suspend fun deleteOauthProviders(input: TDeleteOauthProvidersBody): TDeleteOauthProvidersResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth_providers"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteOauthProvidersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_OAUTH_PROVIDERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_oauth_providers: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteOauthProvidersResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_oauth_providers")
      return TDeleteOauthProvidersResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteOauthProviders(input: TDeleteOauthProvidersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth_providers"
    val inputElem = json.encodeToJsonElement(TDeleteOauthProvidersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_OAUTH_PROVIDERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_policies` (operationId: PublicApiService_DeletePolicies)
   */
  public suspend fun deletePolicies(input: TDeletePoliciesBody): TDeletePoliciesResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_policies"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeletePoliciesBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_POLICIES"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_policies: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deletePoliciesResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_policies")
      return TDeletePoliciesResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeletePolicies(input: TDeletePoliciesBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_policies"
    val inputElem = json.encodeToJsonElement(TDeletePoliciesBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_POLICIES"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_policy` (operationId: PublicApiService_DeletePolicy)
   */
  public suspend fun deletePolicy(input: TDeletePolicyBody): TDeletePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_policy"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeletePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_POLICY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_policy: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deletePolicyResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_policy")
      return TDeletePolicyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeletePolicy(input: TDeletePolicyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_policy"
    val inputElem = json.encodeToJsonElement(TDeletePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_POLICY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_private_key_tags` (operationId: PublicApiService_DeletePrivateKeyTags)
   */
  public suspend fun deletePrivateKeyTags(input: TDeletePrivateKeyTagsBody): TDeletePrivateKeyTagsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_key_tags"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeletePrivateKeyTagsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_PRIVATE_KEY_TAGS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_private_key_tags: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deletePrivateKeyTagsResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_private_key_tags")
      return TDeletePrivateKeyTagsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeletePrivateKeyTags(input: TDeletePrivateKeyTagsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_private_key_tags"
    val inputElem = json.encodeToJsonElement(TDeletePrivateKeyTagsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_PRIVATE_KEY_TAGS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_private_keys` (operationId: PublicApiService_DeletePrivateKeys)
   */
  public suspend fun deletePrivateKeys(input: TDeletePrivateKeysBody): TDeletePrivateKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_keys"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeletePrivateKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_PRIVATE_KEYS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_private_keys: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deletePrivateKeysResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_private_keys")
      return TDeletePrivateKeysResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeletePrivateKeys(input: TDeletePrivateKeysBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_private_keys"
    val inputElem = json.encodeToJsonElement(TDeletePrivateKeysBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_PRIVATE_KEYS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_smart_contract_interface` (operationId: PublicApiService_DeleteSmartContractInterface)
   */
  public suspend fun deleteSmartContractInterface(input: TDeleteSmartContractInterfaceBody): TDeleteSmartContractInterfaceResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_smart_contract_interface"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteSmartContractInterfaceBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_SMART_CONTRACT_INTERFACE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_smart_contract_interface: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteSmartContractInterfaceResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_smart_contract_interface")
      return TDeleteSmartContractInterfaceResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteSmartContractInterface(input: TDeleteSmartContractInterfaceBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_smart_contract_interface"
    val inputElem = json.encodeToJsonElement(TDeleteSmartContractInterfaceBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_SMART_CONTRACT_INTERFACE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_sub_organization` (operationId: PublicApiService_DeleteSubOrganization)
   */
  public suspend fun deleteSubOrganization(input: TDeleteSubOrganizationBody): TDeleteSubOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_sub_organization"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteSubOrganizationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_SUB_ORGANIZATION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_sub_organization: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteSubOrganizationResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_sub_organization")
      return TDeleteSubOrganizationResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteSubOrganization(input: TDeleteSubOrganizationBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_sub_organization"
    val inputElem = json.encodeToJsonElement(TDeleteSubOrganizationBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_SUB_ORGANIZATION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_user_tags` (operationId: PublicApiService_DeleteUserTags)
   */
  public suspend fun deleteUserTags(input: TDeleteUserTagsBody): TDeleteUserTagsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_user_tags"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteUserTagsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_USER_TAGS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_user_tags: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteUserTagsResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_user_tags")
      return TDeleteUserTagsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteUserTags(input: TDeleteUserTagsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_user_tags"
    val inputElem = json.encodeToJsonElement(TDeleteUserTagsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_USER_TAGS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_users` (operationId: PublicApiService_DeleteUsers)
   */
  public suspend fun deleteUsers(input: TDeleteUsersBody): TDeleteUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_users"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_USERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_users: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteUsersResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_users")
      return TDeleteUsersResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteUsers(input: TDeleteUsersBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_users"
    val inputElem = json.encodeToJsonElement(TDeleteUsersBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_USERS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_wallet_accounts` (operationId: PublicApiService_DeleteWalletAccounts)
   */
  public suspend fun deleteWalletAccounts(input: TDeleteWalletAccountsBody): TDeleteWalletAccountsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_wallet_accounts"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteWalletAccountsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_WALLET_ACCOUNTS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_wallet_accounts: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteWalletAccountsResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_wallet_accounts")
      return TDeleteWalletAccountsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteWalletAccounts(input: TDeleteWalletAccountsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_wallet_accounts"
    val inputElem = json.encodeToJsonElement(TDeleteWalletAccountsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_WALLET_ACCOUNTS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_wallets` (operationId: PublicApiService_DeleteWallets)
   */
  public suspend fun deleteWallets(input: TDeleteWalletsBody): TDeleteWalletsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_wallets"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TDeleteWalletsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_WALLETS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_wallets: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.deleteWalletsResult ?: throw RuntimeException("No result found from /public/v1/submit/delete_wallets")
      return TDeleteWalletsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampDeleteWallets(input: TDeleteWalletsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/delete_wallets"
    val inputElem = json.encodeToJsonElement(TDeleteWalletsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_DELETE_WALLETS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/email_auth` (operationId: PublicApiService_EmailAuth)
   */
  public suspend fun emailAuth(input: TEmailAuthBody): TEmailAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/email_auth"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TEmailAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EMAIL_AUTH_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/email_auth: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.emailAuthResult ?: throw RuntimeException("No result found from /public/v1/submit/email_auth")
      return TEmailAuthResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampEmailAuth(input: TEmailAuthBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/email_auth"
    val inputElem = json.encodeToJsonElement(TEmailAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EMAIL_AUTH_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/eth_send_raw_transaction` (operationId: PublicApiService_EthSendRawTransaction)
   */
  public suspend fun ethSendRawTransaction(input: TEthSendRawTransactionBody): TEthSendRawTransactionResponse {
    val url = "$apiBaseUrl/public/v1/submit/eth_send_raw_transaction"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TEthSendRawTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_ETH_SEND_RAW_TRANSACTION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/eth_send_raw_transaction: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.ethSendRawTransactionResult ?: throw RuntimeException("No result found from /public/v1/submit/eth_send_raw_transaction")
      return TEthSendRawTransactionResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampEthSendRawTransaction(input: TEthSendRawTransactionBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/eth_send_raw_transaction"
    val inputElem = json.encodeToJsonElement(TEthSendRawTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_ETH_SEND_RAW_TRANSACTION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/eth_send_transaction` (operationId: PublicApiService_EthSendTransaction)
   */
  public suspend fun ethSendTransaction(input: TEthSendTransactionBody): TEthSendTransactionResponse {
    val url = "$apiBaseUrl/public/v1/submit/eth_send_transaction"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TEthSendTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_ETH_SEND_TRANSACTION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/eth_send_transaction: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.ethSendTransactionResult ?: throw RuntimeException("No result found from /public/v1/submit/eth_send_transaction")
      return TEthSendTransactionResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampEthSendTransaction(input: TEthSendTransactionBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/eth_send_transaction"
    val inputElem = json.encodeToJsonElement(TEthSendTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_ETH_SEND_TRANSACTION"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_private_key` (operationId: PublicApiService_ExportPrivateKey)
   */
  public suspend fun exportPrivateKey(input: TExportPrivateKeyBody): TExportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_private_key"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TExportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/export_private_key: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.exportPrivateKeyResult ?: throw RuntimeException("No result found from /public/v1/submit/export_private_key")
      return TExportPrivateKeyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampExportPrivateKey(input: TExportPrivateKeyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/export_private_key"
    val inputElem = json.encodeToJsonElement(TExportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_wallet` (operationId: PublicApiService_ExportWallet)
   */
  public suspend fun exportWallet(input: TExportWalletBody): TExportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TExportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/export_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.exportWalletResult ?: throw RuntimeException("No result found from /public/v1/submit/export_wallet")
      return TExportWalletResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampExportWallet(input: TExportWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/export_wallet"
    val inputElem = json.encodeToJsonElement(TExportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_wallet_account` (operationId: PublicApiService_ExportWalletAccount)
   */
  public suspend fun exportWalletAccount(input: TExportWalletAccountBody): TExportWalletAccountResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet_account"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TExportWalletAccountBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_WALLET_ACCOUNT"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/export_wallet_account: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.exportWalletAccountResult ?: throw RuntimeException("No result found from /public/v1/submit/export_wallet_account")
      return TExportWalletAccountResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampExportWalletAccount(input: TExportWalletAccountBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/export_wallet_account"
    val inputElem = json.encodeToJsonElement(TExportWalletAccountBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_EXPORT_WALLET_ACCOUNT"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/import_private_key` (operationId: PublicApiService_ImportPrivateKey)
   */
  public suspend fun importPrivateKey(input: TImportPrivateKeyBody): TImportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/import_private_key"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TImportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_IMPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/import_private_key: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.importPrivateKeyResult ?: throw RuntimeException("No result found from /public/v1/submit/import_private_key")
      return TImportPrivateKeyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampImportPrivateKey(input: TImportPrivateKeyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/import_private_key"
    val inputElem = json.encodeToJsonElement(TImportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_IMPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/import_wallet` (operationId: PublicApiService_ImportWallet)
   */
  public suspend fun importWallet(input: TImportWalletBody): TImportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/import_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TImportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_IMPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/import_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.importWalletResult ?: throw RuntimeException("No result found from /public/v1/submit/import_wallet")
      return TImportWalletResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampImportWallet(input: TImportWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/import_wallet"
    val inputElem = json.encodeToJsonElement(TImportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_IMPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_fiat_on_ramp` (operationId: PublicApiService_InitFiatOnRamp)
   */
  public suspend fun initFiatOnRamp(input: TInitFiatOnRampBody): TInitFiatOnRampResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_fiat_on_ramp"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitFiatOnRampBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_FIAT_ON_RAMP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_fiat_on_ramp: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initFiatOnRampResult ?: throw RuntimeException("No result found from /public/v1/submit/init_fiat_on_ramp")
      return TInitFiatOnRampResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitFiatOnRamp(input: TInitFiatOnRampBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_fiat_on_ramp"
    val inputElem = json.encodeToJsonElement(TInitFiatOnRampBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_FIAT_ON_RAMP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_import_private_key` (operationId: PublicApiService_InitImportPrivateKey)
   */
  public suspend fun initImportPrivateKey(input: TInitImportPrivateKeyBody): TInitImportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_import_private_key"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitImportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_IMPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_import_private_key: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initImportPrivateKeyResult ?: throw RuntimeException("No result found from /public/v1/submit/init_import_private_key")
      return TInitImportPrivateKeyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitImportPrivateKey(input: TInitImportPrivateKeyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_import_private_key"
    val inputElem = json.encodeToJsonElement(TInitImportPrivateKeyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_IMPORT_PRIVATE_KEY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_import_wallet` (operationId: PublicApiService_InitImportWallet)
   */
  public suspend fun initImportWallet(input: TInitImportWalletBody): TInitImportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_import_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitImportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_IMPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_import_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initImportWalletResult ?: throw RuntimeException("No result found from /public/v1/submit/init_import_wallet")
      return TInitImportWalletResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitImportWallet(input: TInitImportWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_import_wallet"
    val inputElem = json.encodeToJsonElement(TInitImportWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_IMPORT_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_otp` (operationId: PublicApiService_InitOtp)
   */
  public suspend fun initOtp(input: TInitOtpBody): TInitOtpResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_otp"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitOtpBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_OTP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_otp: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initOtpResult ?: throw RuntimeException("No result found from /public/v1/submit/init_otp")
      return TInitOtpResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitOtp(input: TInitOtpBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_otp"
    val inputElem = json.encodeToJsonElement(TInitOtpBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_OTP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_otp_auth` (operationId: PublicApiService_InitOtpAuth)
   */
  public suspend fun initOtpAuth(input: TInitOtpAuthBody): TInitOtpAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_otp_auth"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitOtpAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_OTP_AUTH_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_otp_auth: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initOtpAuthResultV2 ?: throw RuntimeException("No result found from /public/v1/submit/init_otp_auth")
      return TInitOtpAuthResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitOtpAuth(input: TInitOtpAuthBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_otp_auth"
    val inputElem = json.encodeToJsonElement(TInitOtpAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_OTP_AUTH_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_user_email_recovery` (operationId: PublicApiService_InitUserEmailRecovery)
   */
  public suspend fun initUserEmailRecovery(input: TInitUserEmailRecoveryBody): TInitUserEmailRecoveryResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_user_email_recovery"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TInitUserEmailRecoveryBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_USER_EMAIL_RECOVERY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/init_user_email_recovery: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.initUserEmailRecoveryResult ?: throw RuntimeException("No result found from /public/v1/submit/init_user_email_recovery")
      return TInitUserEmailRecoveryResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampInitUserEmailRecovery(input: TInitUserEmailRecoveryBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/init_user_email_recovery"
    val inputElem = json.encodeToJsonElement(TInitUserEmailRecoveryBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_INIT_USER_EMAIL_RECOVERY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth` (operationId: PublicApiService_Oauth)
   */
  public suspend fun oauth(input: TOauthBody): TOauthResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TOauthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.oauthResult ?: throw RuntimeException("No result found from /public/v1/submit/oauth")
      return TOauthResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampOauth(input: TOauthBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/oauth"
    val inputElem = json.encodeToJsonElement(TOauthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth2_authenticate` (operationId: PublicApiService_Oauth2Authenticate)
   */
  public suspend fun oauth2Authenticate(input: TOauth2AuthenticateBody): TOauth2AuthenticateResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth2_authenticate"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TOauth2AuthenticateBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH2_AUTHENTICATE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth2_authenticate: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.oauth2AuthenticateResult ?: throw RuntimeException("No result found from /public/v1/submit/oauth2_authenticate")
      return TOauth2AuthenticateResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampOauth2Authenticate(input: TOauth2AuthenticateBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/oauth2_authenticate"
    val inputElem = json.encodeToJsonElement(TOauth2AuthenticateBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH2_AUTHENTICATE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth_login` (operationId: PublicApiService_OauthLogin)
   */
  public suspend fun oauthLogin(input: TOauthLoginBody): TOauthLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth_login"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TOauthLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth_login: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.oauthLoginResult ?: throw RuntimeException("No result found from /public/v1/submit/oauth_login")
      return TOauthLoginResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampOauthLogin(input: TOauthLoginBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/oauth_login"
    val inputElem = json.encodeToJsonElement(TOauthLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OAUTH_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/otp_auth` (operationId: PublicApiService_OtpAuth)
   */
  public suspend fun otpAuth(input: TOtpAuthBody): TOtpAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/otp_auth"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TOtpAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OTP_AUTH"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/otp_auth: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.otpAuthResult ?: throw RuntimeException("No result found from /public/v1/submit/otp_auth")
      return TOtpAuthResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampOtpAuth(input: TOtpAuthBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/otp_auth"
    val inputElem = json.encodeToJsonElement(TOtpAuthBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OTP_AUTH"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/otp_login` (operationId: PublicApiService_OtpLogin)
   */
  public suspend fun otpLogin(input: TOtpLoginBody): TOtpLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/otp_login"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TOtpLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OTP_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/otp_login: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.otpLoginResult ?: throw RuntimeException("No result found from /public/v1/submit/otp_login")
      return TOtpLoginResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampOtpLogin(input: TOtpLoginBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/otp_login"
    val inputElem = json.encodeToJsonElement(TOtpLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_OTP_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/recover_user` (operationId: PublicApiService_RecoverUser)
   */
  public suspend fun recoverUser(input: TRecoverUserBody): TRecoverUserResponse {
    val url = "$apiBaseUrl/public/v1/submit/recover_user"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TRecoverUserBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_RECOVER_USER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/recover_user: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.recoverUserResult ?: throw RuntimeException("No result found from /public/v1/submit/recover_user")
      return TRecoverUserResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampRecoverUser(input: TRecoverUserBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/recover_user"
    val inputElem = json.encodeToJsonElement(TRecoverUserBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_RECOVER_USER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/reject_activity` (operationId: PublicApiService_RejectActivity)
   */
  public suspend fun rejectActivity(input: TRejectActivityBody): TRejectActivityResponse {
    val url = "$apiBaseUrl/public/v1/submit/reject_activity"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TRejectActivityBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_REJECT_ACTIVITY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/reject_activity: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TRejectActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampRejectActivity(input: TRejectActivityBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/reject_activity"
    val inputElem = json.encodeToJsonElement(TRejectActivityBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_REJECT_ACTIVITY"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/remove_organization_feature` (operationId: PublicApiService_RemoveOrganizationFeature)
   */
  public suspend fun removeOrganizationFeature(input: TRemoveOrganizationFeatureBody): TRemoveOrganizationFeatureResponse {
    val url = "$apiBaseUrl/public/v1/submit/remove_organization_feature"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TRemoveOrganizationFeatureBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_REMOVE_ORGANIZATION_FEATURE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/remove_organization_feature: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.removeOrganizationFeatureResult ?: throw RuntimeException("No result found from /public/v1/submit/remove_organization_feature")
      return TRemoveOrganizationFeatureResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampRemoveOrganizationFeature(input: TRemoveOrganizationFeatureBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/remove_organization_feature"
    val inputElem = json.encodeToJsonElement(TRemoveOrganizationFeatureBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_REMOVE_ORGANIZATION_FEATURE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/set_organization_feature` (operationId: PublicApiService_SetOrganizationFeature)
   */
  public suspend fun setOrganizationFeature(input: TSetOrganizationFeatureBody): TSetOrganizationFeatureResponse {
    val url = "$apiBaseUrl/public/v1/submit/set_organization_feature"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TSetOrganizationFeatureBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SET_ORGANIZATION_FEATURE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/set_organization_feature: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.setOrganizationFeatureResult ?: throw RuntimeException("No result found from /public/v1/submit/set_organization_feature")
      return TSetOrganizationFeatureResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampSetOrganizationFeature(input: TSetOrganizationFeatureBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/set_organization_feature"
    val inputElem = json.encodeToJsonElement(TSetOrganizationFeatureBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SET_ORGANIZATION_FEATURE"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_raw_payload` (operationId: PublicApiService_SignRawPayload)
   */
  public suspend fun signRawPayload(input: TSignRawPayloadBody): TSignRawPayloadResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payload"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TSignRawPayloadBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_RAW_PAYLOAD_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_raw_payload: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.signRawPayloadResult ?: throw RuntimeException("No result found from /public/v1/submit/sign_raw_payload")
      return TSignRawPayloadResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampSignRawPayload(input: TSignRawPayloadBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payload"
    val inputElem = json.encodeToJsonElement(TSignRawPayloadBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_RAW_PAYLOAD_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_raw_payloads` (operationId: PublicApiService_SignRawPayloads)
   */
  public suspend fun signRawPayloads(input: TSignRawPayloadsBody): TSignRawPayloadsResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payloads"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TSignRawPayloadsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_RAW_PAYLOADS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_raw_payloads: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.signRawPayloadsResult ?: throw RuntimeException("No result found from /public/v1/submit/sign_raw_payloads")
      return TSignRawPayloadsResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampSignRawPayloads(input: TSignRawPayloadsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payloads"
    val inputElem = json.encodeToJsonElement(TSignRawPayloadsBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_RAW_PAYLOADS"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_transaction` (operationId: PublicApiService_SignTransaction)
   */
  public suspend fun signTransaction(input: TSignTransactionBody): TSignTransactionResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_transaction"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TSignTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_TRANSACTION_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_transaction: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.signTransactionResult ?: throw RuntimeException("No result found from /public/v1/submit/sign_transaction")
      return TSignTransactionResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampSignTransaction(input: TSignTransactionBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/sign_transaction"
    val inputElem = json.encodeToJsonElement(TSignTransactionBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_SIGN_TRANSACTION_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/stamp_login` (operationId: PublicApiService_StampLogin)
   */
  public suspend fun stampLogin(input: TStampLoginBody): TStampLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/stamp_login"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TStampLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_STAMP_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/stamp_login: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.stampLoginResult ?: throw RuntimeException("No result found from /public/v1/submit/stamp_login")
      return TStampLoginResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampStampLogin(input: TStampLoginBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/stamp_login"
    val inputElem = json.encodeToJsonElement(TStampLoginBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_STAMP_LOGIN"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_fiat_on_ramp_credential` (operationId: PublicApiService_UpdateFiatOnRampCredential)
   */
  public suspend fun updateFiatOnRampCredential(input: TUpdateFiatOnRampCredentialBody): TUpdateFiatOnRampCredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_fiat_on_ramp_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_fiat_on_ramp_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateFiatOnRampCredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/update_fiat_on_ramp_credential")
      return TUpdateFiatOnRampCredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateFiatOnRampCredential(input: TUpdateFiatOnRampCredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_fiat_on_ramp_credential"
    val inputElem = json.encodeToJsonElement(TUpdateFiatOnRampCredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_FIAT_ON_RAMP_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_oauth2_credential` (operationId: PublicApiService_UpdateOauth2Credential)
   */
  public suspend fun updateOauth2Credential(input: TUpdateOauth2CredentialBody): TUpdateOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_oauth2_credential"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_oauth2_credential: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateOauth2CredentialResult ?: throw RuntimeException("No result found from /public/v1/submit/update_oauth2_credential")
      return TUpdateOauth2CredentialResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateOauth2Credential(input: TUpdateOauth2CredentialBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_oauth2_credential"
    val inputElem = json.encodeToJsonElement(TUpdateOauth2CredentialBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_OAUTH2_CREDENTIAL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_policy` (operationId: PublicApiService_UpdatePolicy)
   */
  public suspend fun updatePolicy(input: TUpdatePolicyBody): TUpdatePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_policy"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdatePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_POLICY_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_policy: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updatePolicyResultV2 ?: throw RuntimeException("No result found from /public/v1/submit/update_policy")
      return TUpdatePolicyResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdatePolicy(input: TUpdatePolicyBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_policy"
    val inputElem = json.encodeToJsonElement(TUpdatePolicyBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_POLICY_V2"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_private_key_tag` (operationId: PublicApiService_UpdatePrivateKeyTag)
   */
  public suspend fun updatePrivateKeyTag(input: TUpdatePrivateKeyTagBody): TUpdatePrivateKeyTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_private_key_tag"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdatePrivateKeyTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_PRIVATE_KEY_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_private_key_tag: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updatePrivateKeyTagResult ?: throw RuntimeException("No result found from /public/v1/submit/update_private_key_tag")
      return TUpdatePrivateKeyTagResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdatePrivateKeyTag(input: TUpdatePrivateKeyTagBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_private_key_tag"
    val inputElem = json.encodeToJsonElement(TUpdatePrivateKeyTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_PRIVATE_KEY_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_root_quorum` (operationId: PublicApiService_UpdateRootQuorum)
   */
  public suspend fun updateRootQuorum(input: TUpdateRootQuorumBody): TUpdateRootQuorumResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_root_quorum"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateRootQuorumBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_ROOT_QUORUM"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_root_quorum: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateRootQuorumResult ?: throw RuntimeException("No result found from /public/v1/submit/update_root_quorum")
      return TUpdateRootQuorumResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateRootQuorum(input: TUpdateRootQuorumBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_root_quorum"
    val inputElem = json.encodeToJsonElement(TUpdateRootQuorumBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_ROOT_QUORUM"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user` (operationId: PublicApiService_UpdateUser)
   */
  public suspend fun updateUser(input: TUpdateUserBody): TUpdateUserResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateUserBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateUserResult ?: throw RuntimeException("No result found from /public/v1/submit/update_user")
      return TUpdateUserResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateUser(input: TUpdateUserBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_user"
    val inputElem = json.encodeToJsonElement(TUpdateUserBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_email` (operationId: PublicApiService_UpdateUserEmail)
   */
  public suspend fun updateUserEmail(input: TUpdateUserEmailBody): TUpdateUserEmailResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_email"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateUserEmailBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_EMAIL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_email: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateUserEmailResult ?: throw RuntimeException("No result found from /public/v1/submit/update_user_email")
      return TUpdateUserEmailResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateUserEmail(input: TUpdateUserEmailBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_user_email"
    val inputElem = json.encodeToJsonElement(TUpdateUserEmailBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_EMAIL"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_name` (operationId: PublicApiService_UpdateUserName)
   */
  public suspend fun updateUserName(input: TUpdateUserNameBody): TUpdateUserNameResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_name"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateUserNameBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_NAME"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_name: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateUserNameResult ?: throw RuntimeException("No result found from /public/v1/submit/update_user_name")
      return TUpdateUserNameResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateUserName(input: TUpdateUserNameBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_user_name"
    val inputElem = json.encodeToJsonElement(TUpdateUserNameBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_NAME"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_phone_number` (operationId: PublicApiService_UpdateUserPhoneNumber)
   */
  public suspend fun updateUserPhoneNumber(input: TUpdateUserPhoneNumberBody): TUpdateUserPhoneNumberResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_phone_number"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateUserPhoneNumberBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_PHONE_NUMBER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_phone_number: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateUserPhoneNumberResult ?: throw RuntimeException("No result found from /public/v1/submit/update_user_phone_number")
      return TUpdateUserPhoneNumberResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateUserPhoneNumber(input: TUpdateUserPhoneNumberBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_user_phone_number"
    val inputElem = json.encodeToJsonElement(TUpdateUserPhoneNumberBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_PHONE_NUMBER"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_tag` (operationId: PublicApiService_UpdateUserTag)
   */
  public suspend fun updateUserTag(input: TUpdateUserTagBody): TUpdateUserTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_tag"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateUserTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_tag: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateUserTagResult ?: throw RuntimeException("No result found from /public/v1/submit/update_user_tag")
      return TUpdateUserTagResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateUserTag(input: TUpdateUserTagBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_user_tag"
    val inputElem = json.encodeToJsonElement(TUpdateUserTagBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_USER_TAG"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_wallet` (operationId: PublicApiService_UpdateWallet)
   */
  public suspend fun updateWallet(input: TUpdateWalletBody): TUpdateWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_wallet"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TUpdateWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/update_wallet: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.updateWalletResult ?: throw RuntimeException("No result found from /public/v1/submit/update_wallet")
      return TUpdateWalletResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampUpdateWallet(input: TUpdateWalletBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/update_wallet"
    val inputElem = json.encodeToJsonElement(TUpdateWalletBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_UPDATE_WALLET"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/verify_otp` (operationId: PublicApiService_VerifyOtp)
   */
  public suspend fun verifyOtp(input: TVerifyOtpBody): TVerifyOtpResponse {
    val url = "$apiBaseUrl/public/v1/submit/verify_otp"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val inputElem = json.encodeToJsonElement(TVerifyOtpBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_VERIFY_OTP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /public/v1/submit/verify_otp: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      val response = json.decodeFromString(V1ActivityResponse.serializer(), text)
      val result = response.activity.result.verifyOtpResult ?: throw RuntimeException("No result found from /public/v1/submit/verify_otp")
      return TVerifyOtpResponse(activity = response.activity, result = result)
    }
  }

  public suspend fun stampVerifyOtp(input: TVerifyOtpBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/public/v1/submit/verify_otp"
    val inputElem = json.encodeToJsonElement(TVerifyOtpBody.serializer(), input)
    val obj = inputElem.jsonObject
    val orgIdElem = obj["organizationId"]
    val tsElem = obj["timestampMs"]
    val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != "organizationId" && k != "timestampMs") put(k, v) } }
    val ts = tsElem?.jsonPrimitive?.content ?: System.currentTimeMillis().toString()
    val activityType = "ACTIVITY_TYPE_VERIFY_OTP"
    val bodyObj = kotlinx.serialization.json.buildJsonObject { put("parameters", params); orgIdElem?.let { put("organizationId", it) }; put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts)); put("type", kotlinx.serialization.json.JsonPrimitive(activityType)) }
    val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/tkhq/api/v1/noop-codegen-anchor` (operationId: PublicApiService_NOOPCodegenAnchor)
   */
  public suspend fun nOOPCodegenAnchor(): TNOOPCodegenAnchorResponse {
    val url = "$apiBaseUrl/tkhq/api/v1/noop-codegen-anchor"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = "{}"
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /tkhq/api/v1/noop-codegen-anchor: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TNOOPCodegenAnchorResponse.serializer(), text)
    }
  }

  public suspend fun stampNOOPCodegenAnchor(): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/tkhq/api/v1/noop-codegen-anchor"
    val bodyJson = ""
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/tkhq/api/v1/test_rate_limits` (operationId: PublicApiService_TestRateLimits)
   */
  public suspend fun testRateLimits(input: TTestRateLimitsBody): TTestRateLimitsResponse {
    val url = "$apiBaseUrl/tkhq/api/v1/test_rate_limits"
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val bodyJson = json.encodeToString(TTestRateLimitsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /tkhq/api/v1/test_rate_limits: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(TTestRateLimitsResponse.serializer(), text)
    }
  }

  public suspend fun stampTestRateLimits(input: TTestRateLimitsBody): TSignedRequest {
    if (stamper == null) throw TurnkeyHttpErrors.StamperNotInitialized
    val url = "$apiBaseUrl/tkhq/api/v1/test_rate_limits"
    val bodyJson = json.encodeToString(TTestRateLimitsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/v1/account` (operationId: AuthProxyService_GetAccount)
   */
  public suspend fun proxyGetAccount(input: ProxyTGetAccountBody): ProxyTGetAccountResponse {
    val url = "$authProxyUrl/v1/account"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTGetAccountBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/account: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTGetAccountResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/oauth2_authenticate` (operationId: AuthProxyService_OAuth2Authenticate)
   */
  public suspend fun proxyOAuth2Authenticate(input: ProxyTOAuth2AuthenticateBody): ProxyTOAuth2AuthenticateResponse {
    val url = "$authProxyUrl/v1/oauth2_authenticate"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOAuth2AuthenticateBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/oauth2_authenticate: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTOAuth2AuthenticateResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/oauth_login` (operationId: AuthProxyService_OAuthLogin)
   */
  public suspend fun proxyOAuthLogin(input: ProxyTOAuthLoginBody): ProxyTOAuthLoginResponse {
    val url = "$authProxyUrl/v1/oauth_login"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOAuthLoginBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/oauth_login: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTOAuthLoginResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_init` (operationId: AuthProxyService_InitOtp)
   */
  public suspend fun proxyInitOtp(input: ProxyTInitOtpBody): ProxyTInitOtpResponse {
    val url = "$authProxyUrl/v1/otp_init"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTInitOtpBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/otp_init: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTInitOtpResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_login` (operationId: AuthProxyService_OtpLogin)
   */
  public suspend fun proxyOtpLogin(input: ProxyTOtpLoginBody): ProxyTOtpLoginResponse {
    val url = "$authProxyUrl/v1/otp_login"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOtpLoginBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/otp_login: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTOtpLoginResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_verify` (operationId: AuthProxyService_VerifyOtp)
   */
  public suspend fun proxyVerifyOtp(input: ProxyTVerifyOtpBody): ProxyTVerifyOtpResponse {
    val url = "$authProxyUrl/v1/otp_verify"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTVerifyOtpBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/otp_verify: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTVerifyOtpResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/signup` (operationId: AuthProxyService_Signup)
   */
  public suspend fun proxySignup(input: ProxyTSignupBody): ProxyTSignupResponse {
    val url = "$authProxyUrl/v1/signup"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTSignupBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/signup: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTSignupResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/wallet_kit_config` (operationId: AuthProxyService_GetWalletKitConfig)
   */
  public suspend fun proxyGetWalletKitConfig(input: ProxyTGetWalletKitConfigBody): ProxyTGetWalletKitConfigResponse {
    val url = "$authProxyUrl/v1/wallet_kit_config"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyHttpErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTGetWalletKitConfigBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", Version.VERSION).build()
    val call = http.newCall(req)
    val resp = call.await()
    resp.use {
      if (!it.isSuccessful) {
        val errBody = withContext(Dispatchers.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }
        throw RuntimeException("""HTTP error from /v1/wallet_kit_config: """ + it.code)
      }
      val text = withContext(Dispatchers.IO) { it.body.string() }
      return json.decodeFromString(ProxyTGetWalletKitConfigResponse.serializer(), text)
    }
  }
}
