package com.localagent.privacy

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Base64

@Singleton
class EncryptionManager @Inject constructor() {

    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val KEY_ALIAS = "LocalAgentKey"
    private val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    init {
        createKeyIfNotExists()
    }

    private fun createKeyIfNotExists() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setRandomizedEncryptionRequired(true)
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun encrypt(data: String): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            // Combine IV and encrypted data: IV length (1 byte) + IV + Encrypted Data
            // GCM IV is typically 12 bytes.
            val combined = ByteArray(1 + iv.size + encryptedBytes.size)
            combined[0] = iv.size.toByte()
            System.arraycopy(iv, 0, combined, 1, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, 1 + iv.size, encryptedBytes.size)

            Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            e.printStackTrace()
            "" // Or throw/handle error
        }
    }

    fun decrypt(encryptedData: String): String {
        return try {
            val decoded = Base64.getDecoder().decode(encryptedData)
            val ivSize = decoded[0].toInt()
            val iv = ByteArray(ivSize)
            System.arraycopy(decoded, 1, iv, 0, ivSize)

            val encryptedBytesSize = decoded.size - 1 - ivSize
            val encryptedBytes = ByteArray(encryptedBytesSize)
            System.arraycopy(decoded, 1 + ivSize, encryptedBytes, 0, encryptedBytesSize)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
