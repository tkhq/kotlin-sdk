# com.turnkey.encoding

Tiny Kotlin/JVM utilities for fast hex, Base64url (RFC 4648, URL-safe, no padding), and secure random bytes.

* Hex: encode (ByteArray.toHexString()), decode (strict + nullable helpers)
* Base64url: encode (ByteArray.toBase64Url()), decode (lenient to padding)
* Secure randomness: randomBytes(count)

> Targets Kotlin/JVM and Android.

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:encoding:<version>")
}
```

Maven:
```xml
<dependency>
    <groupId>com.turnkey</groupId>
    <artifactId>encoding</artifactId>
    <version><!-- version --></version>
</dependency>
```

## Usage

### Hex:
```kotlin
val bytes = byteArrayOf(0x01, 0xAB.toByte(), 0xFF.toByte())
val hex = bytes.toHexString()                  // "01abff"

val back = decodeHex("01abff")                 // byte[]
val maybe = "zz".hexToBytesOrNull()            // null (invalid)
```

*Notes*
* `decodeHex` requires even-length, lowercase/uppercase both accepted.
* Throws `TurnkeyDecodingException` on:
  * odd length (OddLengthString)
  * invalid char (reports char + index)

### Base64url (URL-safe, no padding)

```kotlin
val tokenBytes = randomBytes(32)
val token = tokenBytes.toBase64Url()           // URL-safe, no '=' padding

val decoded1 = decodeBase64Url(token)          // works without padding
val decoded2 = decodeBase64Url(token + "==")   // also accepted
val nullable = "not_base64url".base64UrlToBytesOrNull() // null
```

## Android/JVM compatibility

* Uses java.util.Base64 (API 26+ on Android). For older API levels, use desugaring or supply a backport.
* All other APIs are standard JDK/JCA.