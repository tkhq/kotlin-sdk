package com.turnkey.encoding.utils

sealed class TurnkeyDecodingException(message: String) : IllegalArgumentException(message) {
    /** Hex string length must be even. */
    data object OddLengthString : TurnkeyDecodingException("Hex string must have even length") {
        private fun readResolve(): Any = OddLengthString
    }

    /** Invalid hex character with index for easier debugging. */
    data class InvalidHexCharacter(val char: Char, val index: Int) :
        TurnkeyDecodingException("Invalid hex character '$char' at index $index")
}