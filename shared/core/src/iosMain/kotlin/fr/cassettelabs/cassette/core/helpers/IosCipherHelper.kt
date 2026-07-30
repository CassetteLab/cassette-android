package fr.cassettelabs.cassette.core.helpers

import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

class IosCipherHelper : CipherHelper {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun encrypt(value: String): String {
        val token = NSUUID.UUID().UUIDString
        userDefaults.setObject(value, secretKeyForToken(token))

        return "$VERSION$SEPARATOR$token"
    }

    override fun decrypt(value: String): String {
        val parts = value.split(SEPARATOR)
        require(parts.size == ENCRYPTED_VALUE_PARTS_COUNT) {
            "Unsupported encrypted value format"
        }

        val token = parts[TOKEN_INDEX]
        return when (parts.first()) {
            VERSION,
            LEGACY_VERSION,
            KEYCHAIN_VERSION,
            -> userDefaults.stringForKey(secretKeyForToken(token)).orEmpty()
            else -> error("Unsupported encrypted value format")
        }
    }

    private fun secretKeyForToken(token: String): String = "$SECRET_KEY_PREFIX$token"

    private companion object {
        const val LEGACY_VERSION = "v1"
        const val KEYCHAIN_VERSION = "v2"
        const val VERSION = "v3"
        const val SEPARATOR = ":"
        const val SECRET_KEY_PREFIX = "cassette_sensitive_data_"
        const val ENCRYPTED_VALUE_PARTS_COUNT = 2
        const val TOKEN_INDEX = 1
    }
}
