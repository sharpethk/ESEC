package com.esec.examprep.data.crypto

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.crypto.KeyGenerator

class AesEncryptionTest {

    private fun generateTestKey() = KeyGenerator.getInstance("AES")
        .also { it.init(256) }.generateKey()

    @Test
    fun `encrypt then decrypt returns original plaintext`() {
        val key       = generateTestKey()
        val plaintext = "Hello, ESEC exam bank!".toByteArray()
        val cipher    = AesEncryption.encrypt(plaintext, key)
        val result    = AesEncryption.decrypt(cipher, key)
        assertArrayEquals(plaintext, result)
    }

    @Test
    fun `each encryption produces different ciphertext due to random IV`() {
        val key       = generateTestKey()
        val plaintext = "same message".toByteArray()
        val cipher1   = AesEncryption.encrypt(plaintext, key)
        val cipher2   = AesEncryption.encrypt(plaintext, key)
        assertNotEquals(cipher1.toList(), cipher2.toList())
    }

    @Test(expected = Exception::class)
    fun `decrypt with wrong key throws`() {
        val key1      = generateTestKey()
        val key2      = generateTestKey()
        val cipher    = AesEncryption.encrypt("secret".toByteArray(), key1)
        AesEncryption.decrypt(cipher, key2)
    }
}
