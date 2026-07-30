package fr.cassettelabs.cassette.core.helpers

interface CipherHelper {
    fun encrypt(value: String): String

    fun decrypt(value: String): String
}
