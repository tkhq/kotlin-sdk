package com.example.kotlin_demo_wallet.helpers

import com.example.kotlin_demo_wallet.R
import com.turnkey.types.V1AddressFormat

val addressFormatToReadable: Map<V1AddressFormat, String> = mapOf(
    V1AddressFormat.ADDRESS_FORMAT_ETHEREUM to "Ethereum",
    V1AddressFormat.ADDRESS_FORMAT_SOLANA   to "Solana",
)

val addressFormatToImageIcon: Map<V1AddressFormat, Int> = mapOf(
    V1AddressFormat.ADDRESS_FORMAT_ETHEREUM to R.drawable.ethereum_icon,
    V1AddressFormat.ADDRESS_FORMAT_SOLANA   to R.drawable.solana_icon,
)