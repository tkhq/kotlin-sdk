package com.turnkey.http

import com.turnkey.http.utils.TurnkeyAuthProxyErrors
import com.turnkey.stamper.Stamper
import kotlin.String
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * HTTP Client for interacting with Turnkey API (generated). DO NOT EDIT BY HAND.
 */
public class TurnkeyClient(
  apiBaseUrl: String? = null,
  private val stamper: Stamper,
  private val http: OkHttpClient,
  authProxyUrl: String? = null,
  private val authProxyConfigId: String?,
) {
  private val apiBaseUrl: String = apiBaseUrl ?: "https://api.turnkey.com"

  private val authProxyUrl: String = authProxyUrl ?: "https://authproxy.turnkey.com"

  private val json: Json = Json { ignoreUnknownKeys = true }

  /**
   * POST `/public/v1/query/get_activity` (operationId: PublicApiService_GetActivity)
   */
  public suspend fun getActivity(input: TGetActivityBody): TGetActivityResponse {
    val url = "$apiBaseUrl/public/v1/query/get_activity"
    val bodyJson = json.encodeToString(TGetActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_activity: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampGetActivity(input: TGetActivityBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetApiKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_api_key: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetApiKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetApiKey(input: TGetApiKeyBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_api_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetApiKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampGetApiKeys(input: TGetApiKeysBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetAttestationDocumentBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_attestation: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetAttestationDocumentResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAttestationDocument(input: TGetAttestationDocumentBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetAuthenticatorBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_authenticator: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetAuthenticatorResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAuthenticator(input: TGetAuthenticatorBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_authenticators: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetAuthenticatorsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAuthenticators(input: TGetAuthenticatorsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_boot_proof: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetBootProofResponse.serializer(), text)
    }
  }

  public suspend fun stampGetBootProof(input: TGetBootProofBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetLatestBootProofBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_latest_boot_proof: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetLatestBootProofResponse.serializer(), text)
    }
  }

  public suspend fun stampGetLatestBootProof(input: TGetLatestBootProofBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_oauth2_credential: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetOauth2CredentialResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOauth2Credential(input: TGetOauth2CredentialBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_oauth_providers: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetOauthProvidersResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOauthProviders(input: TGetOauthProvidersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/query/get_oauth_providers"
    val bodyJson = json.encodeToString(TGetOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/get_organization` (operationId: PublicApiService_GetOrganization)
   */
  public suspend fun getOrganization(input: TGetOrganizationBody): TGetOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/query/get_organization"
    val bodyJson = json.encodeToString(TGetOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_organization: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetOrganizationResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOrganization(input: TGetOrganizationBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetOrganizationConfigsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_organization_configs: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetOrganizationConfigsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetOrganizationConfigs(input: TGetOrganizationConfigsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetPolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_policy: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetPolicyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicy(input: TGetPolicyBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetPolicyEvaluationsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_policy_evaluations: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetPolicyEvaluationsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicyEvaluations(input: TGetPolicyEvaluationsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_private_key: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetPrivateKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPrivateKey(input: TGetPrivateKeyBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_smart_contract_interface: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetSmartContractInterfaceResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSmartContractInterface(input: TGetSmartContractInterfaceBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_user: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetUserResponse.serializer(), text)
    }
  }

  public suspend fun stampGetUser(input: TGetUserBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWallet(input: TGetWalletBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetWalletAccountBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/get_wallet_account: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetWalletAccountResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWalletAccount(input: TGetWalletAccountBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetActivitiesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_activities: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetActivitiesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetActivities(input: TGetActivitiesBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetAppProofsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_app_proofs: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetAppProofsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetAppProofs(input: TGetAppProofsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/query/list_app_proofs"
    val bodyJson = json.encodeToString(TGetAppProofsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/query/list_oauth2_credentials` (operationId: PublicApiService_ListOauth2Credentials)
   */
  public suspend fun listOauth2Credentials(input: TListOauth2CredentialsBody): TListOauth2CredentialsResponse {
    val url = "$apiBaseUrl/public/v1/query/list_oauth2_credentials"
    val bodyJson = json.encodeToString(TListOauth2CredentialsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_oauth2_credentials: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TListOauth2CredentialsResponse.serializer(), text)
    }
  }

  public suspend fun stampListOauth2Credentials(input: TListOauth2CredentialsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetPoliciesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_policies: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetPoliciesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPolicies(input: TGetPoliciesBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TListPrivateKeyTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_private_key_tags: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TListPrivateKeyTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampListPrivateKeyTags(input: TListPrivateKeyTagsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetPrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_private_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetPrivateKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampGetPrivateKeys(input: TGetPrivateKeysBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetSmartContractInterfacesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_smart_contract_interfaces: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetSmartContractInterfacesResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSmartContractInterfaces(input: TGetSmartContractInterfacesBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_suborgs: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetSubOrgIdsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetSubOrgIds(input: TGetSubOrgIdsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TListUserTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_user_tags: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TListUserTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampListUserTags(input: TListUserTagsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_users: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetUsersResponse.serializer(), text)
    }
  }

  public suspend fun stampGetUsers(input: TGetUsersBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetVerifiedSubOrgIdsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_verified_suborgs: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetVerifiedSubOrgIdsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetVerifiedSubOrgIds(input: TGetVerifiedSubOrgIdsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetWalletAccountsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_wallet_accounts: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetWalletAccountsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWalletAccounts(input: TGetWalletAccountsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetWalletsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/list_wallets: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetWalletsResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWallets(input: TGetWalletsBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TGetWhoamiBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/query/whoami: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TGetWhoamiResponse.serializer(), text)
    }
  }

  public suspend fun stampGetWhoami(input: TGetWhoamiBody): TSignedRequest {
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
    val bodyJson = json.encodeToString(TApproveActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/approve_activity: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TApproveActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampApproveActivity(input: TApproveActivityBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/approve_activity"
    val bodyJson = json.encodeToString(TApproveActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_api_keys` (operationId: PublicApiService_CreateApiKeys)
   */
  public suspend fun createApiKeys(input: TCreateApiKeysBody): TCreateApiKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_api_keys"
    val bodyJson = json.encodeToString(TCreateApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_api_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateApiKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateApiKeys(input: TCreateApiKeysBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_api_keys"
    val bodyJson = json.encodeToString(TCreateApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_api_only_users` (operationId: PublicApiService_CreateApiOnlyUsers)
   */
  public suspend fun createApiOnlyUsers(input: TCreateApiOnlyUsersBody): TCreateApiOnlyUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_api_only_users"
    val bodyJson = json.encodeToString(TCreateApiOnlyUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_api_only_users: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateApiOnlyUsersResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateApiOnlyUsers(input: TCreateApiOnlyUsersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_api_only_users"
    val bodyJson = json.encodeToString(TCreateApiOnlyUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_authenticators` (operationId: PublicApiService_CreateAuthenticators)
   */
  public suspend fun createAuthenticators(input: TCreateAuthenticatorsBody): TCreateAuthenticatorsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_authenticators"
    val bodyJson = json.encodeToString(TCreateAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_authenticators: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateAuthenticatorsResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateAuthenticators(input: TCreateAuthenticatorsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_authenticators"
    val bodyJson = json.encodeToString(TCreateAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_invitations` (operationId: PublicApiService_CreateInvitations)
   */
  public suspend fun createInvitations(input: TCreateInvitationsBody): TCreateInvitationsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_invitations"
    val bodyJson = json.encodeToString(TCreateInvitationsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_invitations: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateInvitationsResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateInvitations(input: TCreateInvitationsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_invitations"
    val bodyJson = json.encodeToString(TCreateInvitationsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_oauth2_credential` (operationId: PublicApiService_CreateOauth2Credential)
   */
  public suspend fun createOauth2Credential(input: TCreateOauth2CredentialBody): TCreateOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth2_credential"
    val bodyJson = json.encodeToString(TCreateOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_oauth2_credential: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateOauth2CredentialResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateOauth2Credential(input: TCreateOauth2CredentialBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth2_credential"
    val bodyJson = json.encodeToString(TCreateOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_oauth_providers` (operationId: PublicApiService_CreateOauthProviders)
   */
  public suspend fun createOauthProviders(input: TCreateOauthProvidersBody): TCreateOauthProvidersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth_providers"
    val bodyJson = json.encodeToString(TCreateOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_oauth_providers: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateOauthProvidersResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateOauthProviders(input: TCreateOauthProvidersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_oauth_providers"
    val bodyJson = json.encodeToString(TCreateOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_policies` (operationId: PublicApiService_CreatePolicies)
   */
  public suspend fun createPolicies(input: TCreatePoliciesBody): TCreatePoliciesResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_policies"
    val bodyJson = json.encodeToString(TCreatePoliciesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_policies: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreatePoliciesResponse.serializer(), text)
    }
  }

  public suspend fun stampCreatePolicies(input: TCreatePoliciesBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_policies"
    val bodyJson = json.encodeToString(TCreatePoliciesBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_policy` (operationId: PublicApiService_CreatePolicy)
   */
  public suspend fun createPolicy(input: TCreatePolicyBody): TCreatePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_policy"
    val bodyJson = json.encodeToString(TCreatePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_policy: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreatePolicyResponse.serializer(), text)
    }
  }

  public suspend fun stampCreatePolicy(input: TCreatePolicyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_policy"
    val bodyJson = json.encodeToString(TCreatePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_private_key_tag` (operationId: PublicApiService_CreatePrivateKeyTag)
   */
  public suspend fun createPrivateKeyTag(input: TCreatePrivateKeyTagBody): TCreatePrivateKeyTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_private_key_tag"
    val bodyJson = json.encodeToString(TCreatePrivateKeyTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_private_key_tag: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreatePrivateKeyTagResponse.serializer(), text)
    }
  }

  public suspend fun stampCreatePrivateKeyTag(input: TCreatePrivateKeyTagBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_private_key_tag"
    val bodyJson = json.encodeToString(TCreatePrivateKeyTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_private_keys` (operationId: PublicApiService_CreatePrivateKeys)
   */
  public suspend fun createPrivateKeys(input: TCreatePrivateKeysBody): TCreatePrivateKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_private_keys"
    val bodyJson = json.encodeToString(TCreatePrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_private_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreatePrivateKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampCreatePrivateKeys(input: TCreatePrivateKeysBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_private_keys"
    val bodyJson = json.encodeToString(TCreatePrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_read_only_session` (operationId: PublicApiService_CreateReadOnlySession)
   */
  public suspend fun createReadOnlySession(input: TCreateReadOnlySessionBody): TCreateReadOnlySessionResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_read_only_session"
    val bodyJson = json.encodeToString(TCreateReadOnlySessionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_read_only_session: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateReadOnlySessionResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateReadOnlySession(input: TCreateReadOnlySessionBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_read_only_session"
    val bodyJson = json.encodeToString(TCreateReadOnlySessionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_read_write_session` (operationId: PublicApiService_CreateReadWriteSession)
   */
  public suspend fun createReadWriteSession(input: TCreateReadWriteSessionBody): TCreateReadWriteSessionResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_read_write_session"
    val bodyJson = json.encodeToString(TCreateReadWriteSessionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_read_write_session: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateReadWriteSessionResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateReadWriteSession(input: TCreateReadWriteSessionBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_read_write_session"
    val bodyJson = json.encodeToString(TCreateReadWriteSessionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_smart_contract_interface` (operationId: PublicApiService_CreateSmartContractInterface)
   */
  public suspend fun createSmartContractInterface(input: TCreateSmartContractInterfaceBody): TCreateSmartContractInterfaceResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_smart_contract_interface"
    val bodyJson = json.encodeToString(TCreateSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_smart_contract_interface: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateSmartContractInterfaceResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateSmartContractInterface(input: TCreateSmartContractInterfaceBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_smart_contract_interface"
    val bodyJson = json.encodeToString(TCreateSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_sub_organization` (operationId: PublicApiService_CreateSubOrganization)
   */
  public suspend fun createSubOrganization(input: TCreateSubOrganizationBody): TCreateSubOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_sub_organization"
    val bodyJson = json.encodeToString(TCreateSubOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_sub_organization: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateSubOrganizationResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateSubOrganization(input: TCreateSubOrganizationBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_sub_organization"
    val bodyJson = json.encodeToString(TCreateSubOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_user_tag` (operationId: PublicApiService_CreateUserTag)
   */
  public suspend fun createUserTag(input: TCreateUserTagBody): TCreateUserTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_user_tag"
    val bodyJson = json.encodeToString(TCreateUserTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_user_tag: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateUserTagResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateUserTag(input: TCreateUserTagBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_user_tag"
    val bodyJson = json.encodeToString(TCreateUserTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_users` (operationId: PublicApiService_CreateUsers)
   */
  public suspend fun createUsers(input: TCreateUsersBody): TCreateUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_users"
    val bodyJson = json.encodeToString(TCreateUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_users: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateUsersResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateUsers(input: TCreateUsersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_users"
    val bodyJson = json.encodeToString(TCreateUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_wallet` (operationId: PublicApiService_CreateWallet)
   */
  public suspend fun createWallet(input: TCreateWalletBody): TCreateWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet"
    val bodyJson = json.encodeToString(TCreateWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateWallet(input: TCreateWalletBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet"
    val bodyJson = json.encodeToString(TCreateWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/create_wallet_accounts` (operationId: PublicApiService_CreateWalletAccounts)
   */
  public suspend fun createWalletAccounts(input: TCreateWalletAccountsBody): TCreateWalletAccountsResponse {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet_accounts"
    val bodyJson = json.encodeToString(TCreateWalletAccountsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/create_wallet_accounts: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TCreateWalletAccountsResponse.serializer(), text)
    }
  }

  public suspend fun stampCreateWalletAccounts(input: TCreateWalletAccountsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/create_wallet_accounts"
    val bodyJson = json.encodeToString(TCreateWalletAccountsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_api_keys` (operationId: PublicApiService_DeleteApiKeys)
   */
  public suspend fun deleteApiKeys(input: TDeleteApiKeysBody): TDeleteApiKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_api_keys"
    val bodyJson = json.encodeToString(TDeleteApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_api_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteApiKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteApiKeys(input: TDeleteApiKeysBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_api_keys"
    val bodyJson = json.encodeToString(TDeleteApiKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_authenticators` (operationId: PublicApiService_DeleteAuthenticators)
   */
  public suspend fun deleteAuthenticators(input: TDeleteAuthenticatorsBody): TDeleteAuthenticatorsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_authenticators"
    val bodyJson = json.encodeToString(TDeleteAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_authenticators: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteAuthenticatorsResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteAuthenticators(input: TDeleteAuthenticatorsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_authenticators"
    val bodyJson = json.encodeToString(TDeleteAuthenticatorsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_invitation` (operationId: PublicApiService_DeleteInvitation)
   */
  public suspend fun deleteInvitation(input: TDeleteInvitationBody): TDeleteInvitationResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_invitation"
    val bodyJson = json.encodeToString(TDeleteInvitationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_invitation: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteInvitationResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteInvitation(input: TDeleteInvitationBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_invitation"
    val bodyJson = json.encodeToString(TDeleteInvitationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_oauth2_credential` (operationId: PublicApiService_DeleteOauth2Credential)
   */
  public suspend fun deleteOauth2Credential(input: TDeleteOauth2CredentialBody): TDeleteOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth2_credential"
    val bodyJson = json.encodeToString(TDeleteOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_oauth2_credential: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteOauth2CredentialResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteOauth2Credential(input: TDeleteOauth2CredentialBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth2_credential"
    val bodyJson = json.encodeToString(TDeleteOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_oauth_providers` (operationId: PublicApiService_DeleteOauthProviders)
   */
  public suspend fun deleteOauthProviders(input: TDeleteOauthProvidersBody): TDeleteOauthProvidersResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth_providers"
    val bodyJson = json.encodeToString(TDeleteOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_oauth_providers: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteOauthProvidersResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteOauthProviders(input: TDeleteOauthProvidersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_oauth_providers"
    val bodyJson = json.encodeToString(TDeleteOauthProvidersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_policy` (operationId: PublicApiService_DeletePolicy)
   */
  public suspend fun deletePolicy(input: TDeletePolicyBody): TDeletePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_policy"
    val bodyJson = json.encodeToString(TDeletePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_policy: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeletePolicyResponse.serializer(), text)
    }
  }

  public suspend fun stampDeletePolicy(input: TDeletePolicyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_policy"
    val bodyJson = json.encodeToString(TDeletePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_private_key_tags` (operationId: PublicApiService_DeletePrivateKeyTags)
   */
  public suspend fun deletePrivateKeyTags(input: TDeletePrivateKeyTagsBody): TDeletePrivateKeyTagsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_key_tags"
    val bodyJson = json.encodeToString(TDeletePrivateKeyTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_private_key_tags: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeletePrivateKeyTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampDeletePrivateKeyTags(input: TDeletePrivateKeyTagsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_key_tags"
    val bodyJson = json.encodeToString(TDeletePrivateKeyTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_private_keys` (operationId: PublicApiService_DeletePrivateKeys)
   */
  public suspend fun deletePrivateKeys(input: TDeletePrivateKeysBody): TDeletePrivateKeysResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_keys"
    val bodyJson = json.encodeToString(TDeletePrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_private_keys: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeletePrivateKeysResponse.serializer(), text)
    }
  }

  public suspend fun stampDeletePrivateKeys(input: TDeletePrivateKeysBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_private_keys"
    val bodyJson = json.encodeToString(TDeletePrivateKeysBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_smart_contract_interface` (operationId: PublicApiService_DeleteSmartContractInterface)
   */
  public suspend fun deleteSmartContractInterface(input: TDeleteSmartContractInterfaceBody): TDeleteSmartContractInterfaceResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_smart_contract_interface"
    val bodyJson = json.encodeToString(TDeleteSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_smart_contract_interface: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteSmartContractInterfaceResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteSmartContractInterface(input: TDeleteSmartContractInterfaceBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_smart_contract_interface"
    val bodyJson = json.encodeToString(TDeleteSmartContractInterfaceBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_sub_organization` (operationId: PublicApiService_DeleteSubOrganization)
   */
  public suspend fun deleteSubOrganization(input: TDeleteSubOrganizationBody): TDeleteSubOrganizationResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_sub_organization"
    val bodyJson = json.encodeToString(TDeleteSubOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_sub_organization: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteSubOrganizationResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteSubOrganization(input: TDeleteSubOrganizationBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_sub_organization"
    val bodyJson = json.encodeToString(TDeleteSubOrganizationBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_user_tags` (operationId: PublicApiService_DeleteUserTags)
   */
  public suspend fun deleteUserTags(input: TDeleteUserTagsBody): TDeleteUserTagsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_user_tags"
    val bodyJson = json.encodeToString(TDeleteUserTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_user_tags: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteUserTagsResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteUserTags(input: TDeleteUserTagsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_user_tags"
    val bodyJson = json.encodeToString(TDeleteUserTagsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_users` (operationId: PublicApiService_DeleteUsers)
   */
  public suspend fun deleteUsers(input: TDeleteUsersBody): TDeleteUsersResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_users"
    val bodyJson = json.encodeToString(TDeleteUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_users: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteUsersResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteUsers(input: TDeleteUsersBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_users"
    val bodyJson = json.encodeToString(TDeleteUsersBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/delete_wallets` (operationId: PublicApiService_DeleteWallets)
   */
  public suspend fun deleteWallets(input: TDeleteWalletsBody): TDeleteWalletsResponse {
    val url = "$apiBaseUrl/public/v1/submit/delete_wallets"
    val bodyJson = json.encodeToString(TDeleteWalletsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/delete_wallets: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TDeleteWalletsResponse.serializer(), text)
    }
  }

  public suspend fun stampDeleteWallets(input: TDeleteWalletsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/delete_wallets"
    val bodyJson = json.encodeToString(TDeleteWalletsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/email_auth` (operationId: PublicApiService_EmailAuth)
   */
  public suspend fun emailAuth(input: TEmailAuthBody): TEmailAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/email_auth"
    val bodyJson = json.encodeToString(TEmailAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/email_auth: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TEmailAuthResponse.serializer(), text)
    }
  }

  public suspend fun stampEmailAuth(input: TEmailAuthBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/email_auth"
    val bodyJson = json.encodeToString(TEmailAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_private_key` (operationId: PublicApiService_ExportPrivateKey)
   */
  public suspend fun exportPrivateKey(input: TExportPrivateKeyBody): TExportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_private_key"
    val bodyJson = json.encodeToString(TExportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/export_private_key: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TExportPrivateKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampExportPrivateKey(input: TExportPrivateKeyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/export_private_key"
    val bodyJson = json.encodeToString(TExportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_wallet` (operationId: PublicApiService_ExportWallet)
   */
  public suspend fun exportWallet(input: TExportWalletBody): TExportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet"
    val bodyJson = json.encodeToString(TExportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/export_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TExportWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampExportWallet(input: TExportWalletBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet"
    val bodyJson = json.encodeToString(TExportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/export_wallet_account` (operationId: PublicApiService_ExportWalletAccount)
   */
  public suspend fun exportWalletAccount(input: TExportWalletAccountBody): TExportWalletAccountResponse {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet_account"
    val bodyJson = json.encodeToString(TExportWalletAccountBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/export_wallet_account: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TExportWalletAccountResponse.serializer(), text)
    }
  }

  public suspend fun stampExportWalletAccount(input: TExportWalletAccountBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/export_wallet_account"
    val bodyJson = json.encodeToString(TExportWalletAccountBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/import_private_key` (operationId: PublicApiService_ImportPrivateKey)
   */
  public suspend fun importPrivateKey(input: TImportPrivateKeyBody): TImportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/import_private_key"
    val bodyJson = json.encodeToString(TImportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/import_private_key: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TImportPrivateKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampImportPrivateKey(input: TImportPrivateKeyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/import_private_key"
    val bodyJson = json.encodeToString(TImportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/import_wallet` (operationId: PublicApiService_ImportWallet)
   */
  public suspend fun importWallet(input: TImportWalletBody): TImportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/import_wallet"
    val bodyJson = json.encodeToString(TImportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/import_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TImportWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampImportWallet(input: TImportWalletBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/import_wallet"
    val bodyJson = json.encodeToString(TImportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_fiat_on_ramp` (operationId: PublicApiService_InitFiatOnRamp)
   */
  public suspend fun initFiatOnRamp(input: TInitFiatOnRampBody): TInitFiatOnRampResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_fiat_on_ramp"
    val bodyJson = json.encodeToString(TInitFiatOnRampBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_fiat_on_ramp: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitFiatOnRampResponse.serializer(), text)
    }
  }

  public suspend fun stampInitFiatOnRamp(input: TInitFiatOnRampBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_fiat_on_ramp"
    val bodyJson = json.encodeToString(TInitFiatOnRampBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_import_private_key` (operationId: PublicApiService_InitImportPrivateKey)
   */
  public suspend fun initImportPrivateKey(input: TInitImportPrivateKeyBody): TInitImportPrivateKeyResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_import_private_key"
    val bodyJson = json.encodeToString(TInitImportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_import_private_key: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitImportPrivateKeyResponse.serializer(), text)
    }
  }

  public suspend fun stampInitImportPrivateKey(input: TInitImportPrivateKeyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_import_private_key"
    val bodyJson = json.encodeToString(TInitImportPrivateKeyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_import_wallet` (operationId: PublicApiService_InitImportWallet)
   */
  public suspend fun initImportWallet(input: TInitImportWalletBody): TInitImportWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_import_wallet"
    val bodyJson = json.encodeToString(TInitImportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_import_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitImportWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampInitImportWallet(input: TInitImportWalletBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_import_wallet"
    val bodyJson = json.encodeToString(TInitImportWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_otp` (operationId: PublicApiService_InitOtp)
   */
  public suspend fun initOtp(input: TInitOtpBody): TInitOtpResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_otp"
    val bodyJson = json.encodeToString(TInitOtpBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_otp: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitOtpResponse.serializer(), text)
    }
  }

  public suspend fun stampInitOtp(input: TInitOtpBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_otp"
    val bodyJson = json.encodeToString(TInitOtpBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_otp_auth` (operationId: PublicApiService_InitOtpAuth)
   */
  public suspend fun initOtpAuth(input: TInitOtpAuthBody): TInitOtpAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_otp_auth"
    val bodyJson = json.encodeToString(TInitOtpAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_otp_auth: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitOtpAuthResponse.serializer(), text)
    }
  }

  public suspend fun stampInitOtpAuth(input: TInitOtpAuthBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_otp_auth"
    val bodyJson = json.encodeToString(TInitOtpAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/init_user_email_recovery` (operationId: PublicApiService_InitUserEmailRecovery)
   */
  public suspend fun initUserEmailRecovery(input: TInitUserEmailRecoveryBody): TInitUserEmailRecoveryResponse {
    val url = "$apiBaseUrl/public/v1/submit/init_user_email_recovery"
    val bodyJson = json.encodeToString(TInitUserEmailRecoveryBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/init_user_email_recovery: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TInitUserEmailRecoveryResponse.serializer(), text)
    }
  }

  public suspend fun stampInitUserEmailRecovery(input: TInitUserEmailRecoveryBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/init_user_email_recovery"
    val bodyJson = json.encodeToString(TInitUserEmailRecoveryBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth` (operationId: PublicApiService_Oauth)
   */
  public suspend fun oauth(input: TOauthBody): TOauthResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth"
    val bodyJson = json.encodeToString(TOauthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TOauthResponse.serializer(), text)
    }
  }

  public suspend fun stampOauth(input: TOauthBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/oauth"
    val bodyJson = json.encodeToString(TOauthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth2_authenticate` (operationId: PublicApiService_Oauth2Authenticate)
   */
  public suspend fun oauth2Authenticate(input: TOauth2AuthenticateBody): TOauth2AuthenticateResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth2_authenticate"
    val bodyJson = json.encodeToString(TOauth2AuthenticateBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth2_authenticate: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TOauth2AuthenticateResponse.serializer(), text)
    }
  }

  public suspend fun stampOauth2Authenticate(input: TOauth2AuthenticateBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/oauth2_authenticate"
    val bodyJson = json.encodeToString(TOauth2AuthenticateBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/oauth_login` (operationId: PublicApiService_OauthLogin)
   */
  public suspend fun oauthLogin(input: TOauthLoginBody): TOauthLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/oauth_login"
    val bodyJson = json.encodeToString(TOauthLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/oauth_login: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TOauthLoginResponse.serializer(), text)
    }
  }

  public suspend fun stampOauthLogin(input: TOauthLoginBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/oauth_login"
    val bodyJson = json.encodeToString(TOauthLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/otp_auth` (operationId: PublicApiService_OtpAuth)
   */
  public suspend fun otpAuth(input: TOtpAuthBody): TOtpAuthResponse {
    val url = "$apiBaseUrl/public/v1/submit/otp_auth"
    val bodyJson = json.encodeToString(TOtpAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/otp_auth: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TOtpAuthResponse.serializer(), text)
    }
  }

  public suspend fun stampOtpAuth(input: TOtpAuthBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/otp_auth"
    val bodyJson = json.encodeToString(TOtpAuthBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/otp_login` (operationId: PublicApiService_OtpLogin)
   */
  public suspend fun otpLogin(input: TOtpLoginBody): TOtpLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/otp_login"
    val bodyJson = json.encodeToString(TOtpLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/otp_login: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TOtpLoginResponse.serializer(), text)
    }
  }

  public suspend fun stampOtpLogin(input: TOtpLoginBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/otp_login"
    val bodyJson = json.encodeToString(TOtpLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/recover_user` (operationId: PublicApiService_RecoverUser)
   */
  public suspend fun recoverUser(input: TRecoverUserBody): TRecoverUserResponse {
    val url = "$apiBaseUrl/public/v1/submit/recover_user"
    val bodyJson = json.encodeToString(TRecoverUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/recover_user: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TRecoverUserResponse.serializer(), text)
    }
  }

  public suspend fun stampRecoverUser(input: TRecoverUserBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/recover_user"
    val bodyJson = json.encodeToString(TRecoverUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/reject_activity` (operationId: PublicApiService_RejectActivity)
   */
  public suspend fun rejectActivity(input: TRejectActivityBody): TRejectActivityResponse {
    val url = "$apiBaseUrl/public/v1/submit/reject_activity"
    val bodyJson = json.encodeToString(TRejectActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/reject_activity: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TRejectActivityResponse.serializer(), text)
    }
  }

  public suspend fun stampRejectActivity(input: TRejectActivityBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/reject_activity"
    val bodyJson = json.encodeToString(TRejectActivityBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/remove_organization_feature` (operationId: PublicApiService_RemoveOrganizationFeature)
   */
  public suspend fun removeOrganizationFeature(input: TRemoveOrganizationFeatureBody): TRemoveOrganizationFeatureResponse {
    val url = "$apiBaseUrl/public/v1/submit/remove_organization_feature"
    val bodyJson = json.encodeToString(TRemoveOrganizationFeatureBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/remove_organization_feature: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TRemoveOrganizationFeatureResponse.serializer(), text)
    }
  }

  public suspend fun stampRemoveOrganizationFeature(input: TRemoveOrganizationFeatureBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/remove_organization_feature"
    val bodyJson = json.encodeToString(TRemoveOrganizationFeatureBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/set_organization_feature` (operationId: PublicApiService_SetOrganizationFeature)
   */
  public suspend fun setOrganizationFeature(input: TSetOrganizationFeatureBody): TSetOrganizationFeatureResponse {
    val url = "$apiBaseUrl/public/v1/submit/set_organization_feature"
    val bodyJson = json.encodeToString(TSetOrganizationFeatureBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/set_organization_feature: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TSetOrganizationFeatureResponse.serializer(), text)
    }
  }

  public suspend fun stampSetOrganizationFeature(input: TSetOrganizationFeatureBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/set_organization_feature"
    val bodyJson = json.encodeToString(TSetOrganizationFeatureBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_raw_payload` (operationId: PublicApiService_SignRawPayload)
   */
  public suspend fun signRawPayload(input: TSignRawPayloadBody): TSignRawPayloadResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payload"
    val bodyJson = json.encodeToString(TSignRawPayloadBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_raw_payload: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TSignRawPayloadResponse.serializer(), text)
    }
  }

  public suspend fun stampSignRawPayload(input: TSignRawPayloadBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payload"
    val bodyJson = json.encodeToString(TSignRawPayloadBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_raw_payloads` (operationId: PublicApiService_SignRawPayloads)
   */
  public suspend fun signRawPayloads(input: TSignRawPayloadsBody): TSignRawPayloadsResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payloads"
    val bodyJson = json.encodeToString(TSignRawPayloadsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_raw_payloads: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TSignRawPayloadsResponse.serializer(), text)
    }
  }

  public suspend fun stampSignRawPayloads(input: TSignRawPayloadsBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/sign_raw_payloads"
    val bodyJson = json.encodeToString(TSignRawPayloadsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/sign_transaction` (operationId: PublicApiService_SignTransaction)
   */
  public suspend fun signTransaction(input: TSignTransactionBody): TSignTransactionResponse {
    val url = "$apiBaseUrl/public/v1/submit/sign_transaction"
    val bodyJson = json.encodeToString(TSignTransactionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/sign_transaction: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TSignTransactionResponse.serializer(), text)
    }
  }

  public suspend fun stampSignTransaction(input: TSignTransactionBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/sign_transaction"
    val bodyJson = json.encodeToString(TSignTransactionBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/stamp_login` (operationId: PublicApiService_StampLogin)
   */
  public suspend fun stampLogin(input: TStampLoginBody): TStampLoginResponse {
    val url = "$apiBaseUrl/public/v1/submit/stamp_login"
    val bodyJson = json.encodeToString(TStampLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/stamp_login: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TStampLoginResponse.serializer(), text)
    }
  }

  public suspend fun stampStampLogin(input: TStampLoginBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/stamp_login"
    val bodyJson = json.encodeToString(TStampLoginBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_oauth2_credential` (operationId: PublicApiService_UpdateOauth2Credential)
   */
  public suspend fun updateOauth2Credential(input: TUpdateOauth2CredentialBody): TUpdateOauth2CredentialResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_oauth2_credential"
    val bodyJson = json.encodeToString(TUpdateOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_oauth2_credential: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateOauth2CredentialResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateOauth2Credential(input: TUpdateOauth2CredentialBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_oauth2_credential"
    val bodyJson = json.encodeToString(TUpdateOauth2CredentialBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_policy` (operationId: PublicApiService_UpdatePolicy)
   */
  public suspend fun updatePolicy(input: TUpdatePolicyBody): TUpdatePolicyResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_policy"
    val bodyJson = json.encodeToString(TUpdatePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_policy: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdatePolicyResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdatePolicy(input: TUpdatePolicyBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_policy"
    val bodyJson = json.encodeToString(TUpdatePolicyBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_private_key_tag` (operationId: PublicApiService_UpdatePrivateKeyTag)
   */
  public suspend fun updatePrivateKeyTag(input: TUpdatePrivateKeyTagBody): TUpdatePrivateKeyTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_private_key_tag"
    val bodyJson = json.encodeToString(TUpdatePrivateKeyTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_private_key_tag: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdatePrivateKeyTagResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdatePrivateKeyTag(input: TUpdatePrivateKeyTagBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_private_key_tag"
    val bodyJson = json.encodeToString(TUpdatePrivateKeyTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_root_quorum` (operationId: PublicApiService_UpdateRootQuorum)
   */
  public suspend fun updateRootQuorum(input: TUpdateRootQuorumBody): TUpdateRootQuorumResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_root_quorum"
    val bodyJson = json.encodeToString(TUpdateRootQuorumBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_root_quorum: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateRootQuorumResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateRootQuorum(input: TUpdateRootQuorumBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_root_quorum"
    val bodyJson = json.encodeToString(TUpdateRootQuorumBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user` (operationId: PublicApiService_UpdateUser)
   */
  public suspend fun updateUser(input: TUpdateUserBody): TUpdateUserResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user"
    val bodyJson = json.encodeToString(TUpdateUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateUserResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateUser(input: TUpdateUserBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_user"
    val bodyJson = json.encodeToString(TUpdateUserBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_email` (operationId: PublicApiService_UpdateUserEmail)
   */
  public suspend fun updateUserEmail(input: TUpdateUserEmailBody): TUpdateUserEmailResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_email"
    val bodyJson = json.encodeToString(TUpdateUserEmailBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_email: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateUserEmailResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateUserEmail(input: TUpdateUserEmailBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_user_email"
    val bodyJson = json.encodeToString(TUpdateUserEmailBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_name` (operationId: PublicApiService_UpdateUserName)
   */
  public suspend fun updateUserName(input: TUpdateUserNameBody): TUpdateUserNameResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_name"
    val bodyJson = json.encodeToString(TUpdateUserNameBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_name: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateUserNameResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateUserName(input: TUpdateUserNameBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_user_name"
    val bodyJson = json.encodeToString(TUpdateUserNameBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_phone_number` (operationId: PublicApiService_UpdateUserPhoneNumber)
   */
  public suspend fun updateUserPhoneNumber(input: TUpdateUserPhoneNumberBody): TUpdateUserPhoneNumberResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_phone_number"
    val bodyJson = json.encodeToString(TUpdateUserPhoneNumberBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_phone_number: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateUserPhoneNumberResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateUserPhoneNumber(input: TUpdateUserPhoneNumberBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_user_phone_number"
    val bodyJson = json.encodeToString(TUpdateUserPhoneNumberBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_user_tag` (operationId: PublicApiService_UpdateUserTag)
   */
  public suspend fun updateUserTag(input: TUpdateUserTagBody): TUpdateUserTagResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_user_tag"
    val bodyJson = json.encodeToString(TUpdateUserTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_user_tag: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateUserTagResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateUserTag(input: TUpdateUserTagBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_user_tag"
    val bodyJson = json.encodeToString(TUpdateUserTagBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/update_wallet` (operationId: PublicApiService_UpdateWallet)
   */
  public suspend fun updateWallet(input: TUpdateWalletBody): TUpdateWalletResponse {
    val url = "$apiBaseUrl/public/v1/submit/update_wallet"
    val bodyJson = json.encodeToString(TUpdateWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/update_wallet: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TUpdateWalletResponse.serializer(), text)
    }
  }

  public suspend fun stampUpdateWallet(input: TUpdateWalletBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/update_wallet"
    val bodyJson = json.encodeToString(TUpdateWalletBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/public/v1/submit/verify_otp` (operationId: PublicApiService_VerifyOtp)
   */
  public suspend fun verifyOtp(input: TVerifyOtpBody): TVerifyOtpResponse {
    val url = "$apiBaseUrl/public/v1/submit/verify_otp"
    val bodyJson = json.encodeToString(TVerifyOtpBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /public/v1/submit/verify_otp: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TVerifyOtpResponse.serializer(), text)
    }
  }

  public suspend fun stampVerifyOtp(input: TVerifyOtpBody): TSignedRequest {
    val url = "$apiBaseUrl/public/v1/submit/verify_otp"
    val bodyJson = json.encodeToString(TVerifyOtpBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/tkhq/api/v1/noop-codegen-anchor` (operationId: PublicApiService_NOOPCodegenAnchor)
   */
  public suspend fun nOOPCodegenAnchor(): TNOOPCodegenAnchorResponse {
    val url = "$apiBaseUrl/tkhq/api/v1/noop-codegen-anchor"
    val bodyJson = "{}"
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /tkhq/api/v1/noop-codegen-anchor: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TNOOPCodegenAnchorResponse.serializer(), text)
    }
  }

  public suspend fun stampNOOPCodegenAnchor(): TSignedRequest {
    val url = "$apiBaseUrl/tkhq/api/v1/noop-codegen-anchor"
    val bodyJson = "{}"
    val (hName, hValue) = stamper.stamp(bodyJson)
    val stamp = TStamp(stampHeaderName = hName, stampHeaderValue = hValue)
    return TSignedRequest(body = bodyJson, stamp = stamp, url = url)
  }

  /**
   * POST `/tkhq/api/v1/test_rate_limits` (operationId: PublicApiService_TestRateLimits)
   */
  public suspend fun testRateLimits(input: TTestRateLimitsBody): TTestRateLimitsResponse {
    val url = "$apiBaseUrl/tkhq/api/v1/test_rate_limits"
    val bodyJson = json.encodeToString(TTestRateLimitsBody.serializer(), input)
    val (hName, hValue) = stamper.stamp(bodyJson)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header(hName, hValue).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /tkhq/api/v1/test_rate_limits: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(TTestRateLimitsResponse.serializer(), text)
    }
  }

  public suspend fun stampTestRateLimits(input: TTestRateLimitsBody): TSignedRequest {
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
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTGetAccountBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/account: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTGetAccountResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/oauth2_authenticate` (operationId: AuthProxyService_OAuth2Authenticate)
   */
  public suspend fun proxyOAuth2Authenticate(input: ProxyTOAuth2AuthenticateBody): ProxyTOAuth2AuthenticateResponse {
    val url = "$authProxyUrl/v1/oauth2_authenticate"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOAuth2AuthenticateBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/oauth2_authenticate: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTOAuth2AuthenticateResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/oauth_login` (operationId: AuthProxyService_OAuthLogin)
   */
  public suspend fun proxyOAuthLogin(input: ProxyTOAuthLoginBody): ProxyTOAuthLoginResponse {
    val url = "$authProxyUrl/v1/oauth_login"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOAuthLoginBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/oauth_login: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTOAuthLoginResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_init` (operationId: AuthProxyService_InitOtp)
   */
  public suspend fun proxyInitOtp(input: ProxyTInitOtpBody): ProxyTInitOtpResponse {
    val url = "$authProxyUrl/v1/otp_init"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTInitOtpBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/otp_init: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTInitOtpResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_login` (operationId: AuthProxyService_OtpLogin)
   */
  public suspend fun proxyOtpLogin(input: ProxyTOtpLoginBody): ProxyTOtpLoginResponse {
    val url = "$authProxyUrl/v1/otp_login"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTOtpLoginBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/otp_login: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTOtpLoginResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/otp_verify` (operationId: AuthProxyService_VerifyOtp)
   */
  public suspend fun proxyVerifyOtp(input: ProxyTVerifyOtpBody): ProxyTVerifyOtpResponse {
    val url = "$authProxyUrl/v1/otp_verify"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTVerifyOtpBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/otp_verify: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTVerifyOtpResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/signup` (operationId: AuthProxyService_Signup)
   */
  public suspend fun proxySignup(input: ProxyTSignupBody): ProxyTSignupResponse {
    val url = "$authProxyUrl/v1/signup"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTSignupBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/signup: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTSignupResponse.serializer(), text)
    }
  }

  /**
   * POST `/v1/wallet_kit_config` (operationId: AuthProxyService_GetWalletKitConfig)
   */
  public suspend fun proxyGetWalletKitConfig(input: ProxyTGetWalletKitConfigBody): ProxyTGetWalletKitConfigResponse {
    val url = "$authProxyUrl/v1/wallet_kit_config"
    if (authProxyConfigId.isNullOrBlank()) throw TurnkeyAuthProxyErrors.MissingAuthProxyConfigId
    val bodyJson = json.encodeToString(ProxyTGetWalletKitConfigBody.serializer(), input)
    val req = Request.Builder().url(url).post(bodyJson.toRequestBody("application/json".toMediaType())).header("X-Auth-Proxy-Config-ID", authProxyConfigId).header("X-Client-Version", "kotlin-sdk/0.1.0").build()
    val call = http.newCall(req)
    call.execute().use { resp ->
      if (!resp.isSuccessful) {
        throw RuntimeException("""HTTP error from /v1/wallet_kit_config: """ + resp.code)
      }
      val text = resp.body.string()
      return json.decodeFromString(ProxyTGetWalletKitConfigResponse.serializer(), text)
    }
  }
}
