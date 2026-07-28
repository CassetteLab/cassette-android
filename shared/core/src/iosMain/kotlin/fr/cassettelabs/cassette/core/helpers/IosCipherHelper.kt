package fr.cassettelabs.cassette.core.helpers

import cnames.structs.__CFDictionary
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVarOf
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.interpretObjCPointerOrNull
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.value
import kotlinx.cinterop.objcPtr
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSCopyingProtocol
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosCipherHelper : CipherHelper {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun encrypt(value: String): String {
        val token = NSUUID.UUID().UUIDString
        val status = SecItemAdd(keychainQuery(token, value.nsData()).cfDictionaryRef(), null)
        check(status == errSecSuccess) { "Unable to store secret in Keychain: $status" }

        return "$VERSION_2$SEPARATOR$token"
    }

    override fun decrypt(value: String): String {
        val parts = value.split(SEPARATOR)
        require(parts.size == ENCRYPTED_VALUE_PARTS_COUNT) {
            "Unsupported encrypted value format"
        }

        return when (parts.first()) {
            VERSION_2 -> readKeychainValue(parts[TOKEN_INDEX])
            LEGACY_VERSION -> userDefaults.stringForKey(legacyKeyForToken(parts[TOKEN_INDEX])).orEmpty()
            else -> error("Unsupported encrypted value format")
        }
    }

    private fun readKeychainValue(token: String): String = memScoped {
        val result = alloc<CPointerVarOf<CPointer<out CPointed>>>()
        val query = keychainQuery(token).apply {
            setKeychainObject(kCFBooleanTrue, kSecReturnData)
            setKeychainObject(kSecMatchLimitOne, kSecMatchLimit)
        }
        val status = SecItemCopyMatching(query.cfDictionaryRef(), result.ptr)

        if (status == errSecItemNotFound) {
            return@memScoped ""
        }
        check(status == errSecSuccess) { "Unable to read secret from Keychain: $status" }

        interpretObjCPointerOrNull<NSData>(result.value.rawValue)?.stringValue().orEmpty()
    }

    private fun keychainQuery(
        token: String,
        value: NSData? = null,
    ): NSMutableDictionary = NSMutableDictionary().apply {
        setKeychainObject(kSecClassGenericPassword, kSecClass)
        setKeychainObject(SERVICE, kSecAttrService)
        setKeychainObject(token, kSecAttrAccount)
        setKeychainObject(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly, kSecAttrAccessible)
        value?.let { setKeychainObject(it, kSecValueData) }
    }

    private fun NSMutableDictionary.setKeychainObject(value: Any?, key: CPointer<out CPointed>?) {
        val nsKey = interpretObjCPointerOrNull<NSCopyingProtocol>(key.rawValue) ?: return
        val nsValue = value?.keychainObject() ?: return
        setObject(nsValue, nsKey)
    }

    private fun NSMutableDictionary.cfDictionaryRef(): CFDictionaryRef = interpretCPointer<__CFDictionary>(objcPtr())!!

    private fun Any.keychainObject(): Any? =
        if (this is CPointer<*>) {
            interpretObjCPointerOrNull<Any>(rawValue)
        } else {
            this
        }

    private fun String.nsData(): NSData {
        val bytes = encodeToByteArray()
        return bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
    }

    private fun NSData.stringValue(): String = bytes?.readBytes(length.toInt())?.decodeToString().orEmpty()

    private fun legacyKeyForToken(token: String): String = "$LEGACY_KEY_PREFIX$token"

    private companion object {
        const val LEGACY_VERSION = "v1"
        const val VERSION_2 = "v2"
        const val SEPARATOR = ":"
        const val SERVICE = "fr.cassettelabs.cassette.sensitive-data"
        const val LEGACY_KEY_PREFIX = "cassette_sensitive_data_"
        const val ENCRYPTED_VALUE_PARTS_COUNT = 2
        const val TOKEN_INDEX = 1
    }
}
