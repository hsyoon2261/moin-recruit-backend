package com.themoin.recruit.backend.common.util

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

private const val ALGORITHM = "AES/ECB/PKCS5Padding"
private const val SECRET_KEY_ALGORITHM = "AES"

object Crypt {

    fun encrypt(target: String, email: String): String {
        val secretKey = email.toSecretKey()
        val cipher = Cipher.getInstance(ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, secretKey)
        }
        val encryptedBytes = cipher.doFinal(target.toByteArray(UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedTarget: String, email: String): String {
        val secretKey = email.toSecretKey()
        val cipher = Cipher.getInstance(ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, secretKey)
        }
        val decodedBytes = Base64.getDecoder().decode(encryptedTarget)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, UTF_8)
    }

    private fun String.toSecretKey(): SecretKey {
        val keyBytes = this.toByteArray(UTF_8)
        val sha = MessageDigest.getInstance("SHA-256")
        val hashedKey = sha.digest(keyBytes)
        return SecretKeySpec(hashedKey.copyOf(16), SECRET_KEY_ALGORITHM)
    }
}
