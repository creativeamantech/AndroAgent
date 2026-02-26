package com.localagent.privacy

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor() {

    private val KEY_ALIAS = "LocalAgentKey"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val IV_LENGTH = 12 // GCM recommended IV length is 12 bytes
    private val TAG_LENGTH = 128

    init {
        createKey()
    }

    private fun createKey() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        // In a real app, handle KeyStoreException, UnrecoverableKeyException, etc.
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encryption = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))

        // Return IV + Encrypted Data as Base64 string
        // We need the IV to decrypt, so prepend it to the cipher text
        val combined = ByteArray(iv.size + encryption.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryption, 0, combined, iv.size, encryption.size)

        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(encryptedData: String): String {
        val combined = Base64.getDecoder().decode(encryptedData)

        // Extract IV
        val iv = ByteArray(IV_LENGTH)
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH)

        // Extract CipherText
        val cipherText = ByteArray(combined.size - IV_LENGTH)
        System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec) // Initialize with key and IV

        val decoded = cipher.doFinal(cipherText)

        return String(decoded, StandardCharsets.UTF_8)
    }
}
