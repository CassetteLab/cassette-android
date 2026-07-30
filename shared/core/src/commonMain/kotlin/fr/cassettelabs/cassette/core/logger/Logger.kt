package fr.cassettelabs.cassette.core.logger

interface Logger {
    fun init(tag: String)
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String)
}
