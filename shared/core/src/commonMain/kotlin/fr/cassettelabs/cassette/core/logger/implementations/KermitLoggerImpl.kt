package fr.cassettelabs.cassette.core.logger.implementations

import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.platformLogWriter
import fr.cassettelabs.cassette.core.logger.Logger

internal class KermitLoggerImpl : Logger {

    companion object {
        private var isConfigured = false
    }

    init {
        if (!isConfigured) {
            co.touchlab.kermit.Logger.setLogWriters(
                listOfNotNull(
                    platformLogWriter(DefaultFormatter),
                    platformFileLogWriter(),
                ),
            )
            isConfigured = true
        }
    }

    private var tag: String = ""

    override fun init(tag: String) {
        this.tag = tag
    }

    override fun d(message: String) {
        co.touchlab.kermit.Logger.d(messageString = message, tag = tag)
    }

    override fun i(message: String) {
        co.touchlab.kermit.Logger.i(messageString = message, tag = tag)
    }

    override fun w(message: String) {
        co.touchlab.kermit.Logger.w(messageString = message, tag = tag)
    }

    override fun e(message: String) {
        co.touchlab.kermit.Logger.e(messageString = message, tag = tag)
    }
}

internal expect fun platformFileLogWriter(): LogWriter?
