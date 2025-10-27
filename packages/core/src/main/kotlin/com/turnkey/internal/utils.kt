package com.turnkey.internal

import com.turnkey.encoding.decodeBase64Url
import com.turnkey.http.TGetWalletAccountsBody
import com.turnkey.http.TurnkeyClient
import com.turnkey.http.V1Pagination
import com.turnkey.http.V1Wallet
import com.turnkey.http.V1WalletAccount
import com.turnkey.models.SessionUser
import com.turnkey.models.StorageError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

object JwtDecoder {
    /**
     * Decode a JWT's payload (2nd segment) as type [T].
     *
     * - Validates the 3-part structure.
     * - Base64URL-decodes the payload.
     * - Parses JSON into [T] (unknown fields are ignored by default).
     *
     * @throws StorageError.InvalidJWT if structure or base64 is invalid
     * @throws StorageError.DecodingFailed if JSON parsing fails
     */
    @Throws(StorageError::class)
    inline fun <reified T> decode(
        jwt: String,
        json: Json = Json { ignoreUnknownKeys = true }
    ): T {
        val parts = jwt.split('.')
        if (parts.size != 3) throw StorageError.InvalidJWT

        val payloadB64Url = parts[1]
        val bytes = try {
            decodeBase64Url(payloadB64Url)
        } catch (_: Throwable) {
            throw StorageError.InvalidJWT
        }

        return try {
            json.decodeFromString<T>(bytes.toString(Charsets.UTF_8))
        } catch (e: Throwable) {
            throw StorageError.DecodingFailed(e)
        }
    }
}

object Helpers {
    suspend fun fetchAllWalletAccountsWithCursor(client: TurnkeyClient, organizationId: String): MutableList<V1WalletAccount> =
        coroutineScope {
            var hasMore: Boolean = true
            var cursor: String? = null
            val limit: Int = 100
            val accounts: MutableList<V1WalletAccount> = mutableListOf()

            while (hasMore) {
                val walletAccountsDeferred = async {
                    client.getWalletAccounts(TGetWalletAccountsBody(
                        organizationId,
                        includeWalletDetails = true,
                        paginationOptions = V1Pagination(
                            limit = limit.toString(),
                            after = cursor
                        )
                    ))
                }
                val walletAccounts = walletAccountsDeferred.await()
                accounts.addAll(walletAccounts.accounts)
                hasMore = walletAccounts.accounts.size == limit

                cursor = if (walletAccounts.accounts.isNotEmpty()) {
                    walletAccounts.accounts.last().walletId
                } else {
                    null
                }
            }
            return@coroutineScope accounts
        }

    fun mapAccountsToWallet(accounts: MutableList<V1WalletAccount>, wallets: List<V1Wallet>): MutableList<SessionUser.UserWallet> {
        val walletMap: MutableMap<String, SessionUser.UserWallet> =
            wallets.associateBy(
                keySelector = { it.walletId },
                valueTransform = { SessionUser.UserWallet(id = it.walletId, name = it.walletId, accounts = mutableListOf())}
            ).toMutableMap()


        for (a in accounts) {
            val account = SessionUser.UserWallet.WalletAccount(
                id = a.walletAccountId,
                curve = a.curve,
                pathFormat = a.pathFormat,
                path = a.path,
                addressFormat = a.addressFormat,
                address = a.address,
                createdAt = a.createdAt,
                updatedAt = a.updatedAt
            )
            if (walletMap.containsKey(a.walletId)) {
                val wallet = walletMap[a.walletId]
                wallet?.accounts?.add(account)
            } else {
                walletMap[a.walletDetails!!.walletId] = SessionUser.UserWallet(
                    id = a.walletId,
                    name = a.walletDetails!!.walletName,
                    accounts = mutableListOf(account)
                )
            }
        }
       return walletMap.values.toMutableList()
    }
}