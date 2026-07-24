package fr.cassettelabs.cassette.core.helpers

class NoOpCipherHelper : CipherHelper {
    override fun encrypt(value: String): String = value

    override fun decrypt(value: String): String = value
}
