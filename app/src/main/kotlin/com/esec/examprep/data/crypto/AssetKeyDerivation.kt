package com.esec.examprep.data.crypto

import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val PBKDF2_ALGORITHM  = "PBKDF2WithHmacSHA256"
private const val ITERATIONS        = 100_000
private const val KEY_LENGTH        = 256

/**
 * Derives an AES-256 key from the app's compile-time passphrase + a fixed salt.
 * The passphrase is obfuscated in the APK via BuildConfig + ProGuard string encryption.
 * Use this key ONLY to decrypt the bundled asset — never persist it.
 */
object AssetKeyDerivation {

    private val SALT = byteArrayOf(
        0x45, 0x53, 0x45, 0x43, 0x51, 0x42, 0x41, 0x4E,
        0x4B, 0x32, 0x30, 0x32, 0x35, 0x21, 0x40, 0x23,
    )

    fun derive(passphrase: CharArray): SecretKey {
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val spec = PBEKeySpec(passphrase, SALT, ITERATIONS, KEY_LENGTH)
        val raw = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(raw, "AES")
    }
}
