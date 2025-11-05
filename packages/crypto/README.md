# com.turnkey.crypto

This package provides cryptographic utilities used across our applications, specifically for key generation, encryption, and decryption.

It includes support for:

* Generate raw P-256 keypairs (hex, compressed/uncompressed)
* Decrypt credential bundles into usable ECPublicKey/ECPrivateKey
* Decrypt export bundles (to mnemonic or hex, incl. Solana-friendly formats)
* Encrypt a wallet mnemonic into a Turnkey import bundle
* Verifying enclave signatures using ECDSA

These utilities are designed to work alongside Turnkey’s enclave-based infrastructure for secure key management and wallet recovery.

> Targets Kotlin/JVM and Android.

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:crypto:<version>")
}
```

Maven:
```xml
<dependency>
    <groupId>com.turnkey</groupId>
    <artifactId>crypto</artifactId>
    <version><!-- version --></version>
</dependency>
```

## Quick Start

1) Generate a P-256 keypair (raw hex + compressed/uncompressed)

```kotlin
val raw = generateP256KeyPair()
println("priv d (hex): ${raw.privateKey}")               // 32-byte hex
println("pub (compressed): ${raw.publicKeyCompressed}")  // 33-byte hex (0x02/0x03 + X)
println("pub (uncompressed): ${raw.publicKeyUncompressed}") // 65-byte hex (0x04 + X + Y)
```

## Requirements

* JVM 8+ (Android supported)
* EC (secp256r1 / P-256) via standard java.security (KeyFactory, KeyPairGenerator, AlgorithmParameters)

> Android note: If you run on older API levels or custom devices, ensure EC (secp256r1) is available and that your network/security policies allow any testing you do with local/dev endpoints.

## Security notes

* Treat private keys, mnemonics, and decrypted payloads as secrets.
* Never log raw keys in production.
* Prefer in-memory handling; wipe buffers when possible.