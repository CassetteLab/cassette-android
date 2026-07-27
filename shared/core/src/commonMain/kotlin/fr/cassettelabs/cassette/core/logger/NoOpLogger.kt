package fr.cassettelabs.cassette.core.logger

class NoOpLogger : Logger {
    override fun init(tag: String) = Unit
    override fun d(message: String) = Unit
    override fun i(message: String) = Unit
    override fun w(message: String) = Unit
    override fun e(message: String) = Unit
}
