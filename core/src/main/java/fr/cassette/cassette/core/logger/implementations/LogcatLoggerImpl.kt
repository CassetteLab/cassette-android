package fr.cassette.cassette.core.logger.implementations

import android.util.Log
import fr.cassette.cassette.core.logger.Logger

internal class LogcatLoggerImpl : Logger {
    private var tag: String = "Unknow tag"

    override fun init(tag: String) {
        this.tag = tag
    }

    override fun d(message: String) {
        Log.d(tag, message)
    }

    override fun i(message: String) {
        Log.i(tag, message)
    }

    override fun w(message: String) {
        Log.w(tag, message)
    }

    override fun w(
        message: String,
        throwable: Throwable,
    ) {
        Log.w(tag, message, throwable)
    }

    override fun w(throwable: Throwable) {
        Log.w(tag, throwable)
    }

    override fun e(message: String) {
        Log.e(tag, message)
    }

    override fun e(
        message: String,
        throwable: Throwable,
    ) {
        Log.e(tag, message, throwable)
    }

    override fun e(throwable: Throwable) {
        Log.e(tag, throwable.message, throwable)
    }
}
