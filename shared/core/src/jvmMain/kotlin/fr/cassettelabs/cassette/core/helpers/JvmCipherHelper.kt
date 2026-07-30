package fr.cassettelabs.cassette.core.helpers

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import java.util.prefs.Preferences
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class JvmCipherHelper : CipherHelper {
    private val preferences = Preferences.userNodeForPackage(JvmCipherHelper::class.java)
    private val random = SecureRandom()

    override fun encrypt(value: String): String {
        val iv = ByteArray(IV_SIZE_BYTES).also(random::nextBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

        val encryptedValue = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
        return listOf(
            VERSION,
            iv.encodeBase64(),
            encryptedValue.encodeBase64(),
        ).joinToString(separator = SEPARATOR)
    }

    override fun decrypt(value: String): String {
        val parts = value.split(SEPARATOR)
        require(parts.size == ENCRYPTED_VALUE_PARTS_COUNT && parts.first() == VERSION) {
            "Unsupported encrypted value format"
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_LENGTH_BITS, parts[IV_INDEX].decodeBase64()),
        )

        return String(cipher.doFinal(parts[ENCRYPTED_VALUE_INDEX].decodeBase64()), StandardCharsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val encodedKey = preferences.get(KEY_ALIAS, null)
        if (encodedKey != null) {
            return SecretKeySpec(encodedKey.decodeBase64(), KEY_ALGORITHM)
        }

        return KeyGenerator.getInstance(KEY_ALGORITHM).run {
            init(KEY_SIZE_BITS)
            generateKey().also { preferences.put(KEY_ALIAS, it.encoded.encodeBase64()) }
        }
    }

    private fun ByteArray.encodeBase64(): String = Base64.getEncoder().encodeToString(this)

    private fun String.decodeBase64(): ByteArray = Base64.getDecoder().decode(this)

    private companion object {
        const val KEY_ALIAS = "cassette_sensitive_data_key"
        const val KEY_ALGORITHM = "AES"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val VERSION = "v1"
        const val SEPARATOR = ":"
        const val KEY_SIZE_BITS = 256
        const val GCM_TAG_LENGTH_BITS = 128
        const val IV_SIZE_BYTES = 12
        const val ENCRYPTED_VALUE_PARTS_COUNT = 3
        const val IV_INDEX = 1
        const val ENCRYPTED_VALUE_INDEX = 2
    }
}
