package com.esec.examprep.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEYSTORE_PROVIDER  = "AndroidKeyStore"
private const val KEY_ALIAS          = "esec_question_key"
private const val TRANSFORMATION      = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH     = 128
private const val GCM_IV_SIZE        = 12

/**
 * AES-256-GCM encryption backed by the Android Keystore.
 * The secret key never leaves secure hardware; ciphertext is stored in assets.
 *
 * For bundled assets encrypted at build time, use [decryptBytes] with a key
 * derived via PBKDF2 (see [AssetKeyDerivation]) instead of the Keystore key,
 * since the Keystore key is device-specific and cannot be shared with the APK.
 */
object AesEncryption {

    fun encrypt(plaintext: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext)
        // Prefix IV to ciphertext: [IV(12)] + [ciphertext]
        return iv + encrypted
    }

    fun decrypt(ciphertext: ByteArray, key: SecretKey): ByteArray {
        require(ciphertext.size > GCM_IV_SIZE) { "Ciphertext too short" }
        val iv = ciphertext.copyOfRange(0, GCM_IV_SIZE)
        val data = ciphertext.copyOfRange(GCM_IV_SIZE, ciphertext.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(data)
    }

    fun getOrCreateKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            .also { it.init(spec) }
            .generateKey()
    }
}
