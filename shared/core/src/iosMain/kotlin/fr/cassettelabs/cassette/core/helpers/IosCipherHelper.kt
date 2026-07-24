package fr.cassettelabs.cassette.core.helpers

import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

class IosCipherHelper : CipherHelper {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun encrypt(value: String): String {
        val token = NSUUID.UUID().UUIDString
        userDefaults.setObject(value, keyForToken(token))
        return "$VERSION$SEPARATOR$token"
    }

    override fun decrypt(value: String): String {
        val parts = value.split(SEPARATOR)
        require(parts.size == ENCRYPTED_VALUE_PARTS_COUNT && parts.first() == VERSION) {
            "Unsupported encrypted value format"
        }

        return userDefaults.stringForKey(keyForToken(parts[TOKEN_INDEX])).orEmpty()
    }

    private fun keyForToken(token: String): String = "$KEY_PREFIX$token"

    private companion object {
        const val VERSION = "v1"
        const val SEPARATOR = ":"
        const val KEY_PREFIX = "cassette_sensitive_data_"
        const val ENCRYPTED_VALUE_PARTS_COUNT = 2
        const val TOKEN_INDEX = 1
    }
}
