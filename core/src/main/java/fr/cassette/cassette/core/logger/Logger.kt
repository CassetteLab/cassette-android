package fr.cassette.cassette.core.logger

interface Logger {
    fun init(tag: String)

    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun w(message: String, throwable: Throwable)
    fun w(throwable: Throwable)
    fun e(message: String)
    fun e(message: String, throwable: Throwable)
    fun e(throwable: Throwable)
}