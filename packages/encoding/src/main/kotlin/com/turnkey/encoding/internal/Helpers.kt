package com.turnkey.encoding.internal

import com.turnkey.encoding.utils.TurnkeyEncodingError
import java.security.SecureRandom

/** Thread-safe secure random instance. */
internal val SECURE_RANDOM = SecureRandom()

/** Convert a hex character to its numeric value (0-15), throwing on invalid characters. */
internal fun Char.hexNibbleOrThrow(index: Int): Int {
    val d = Character.digit(this, 16)
    if (d == -1) throw TurnkeyEncodingError.InvalidHexCharacter(this, index)
    return d
}
