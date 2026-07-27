package fr.cassettelabs.cassette.core.logger

class PlatformLogger : Logger {
    private var tag: String = "Cassette"

    override fun init(tag: String) {
        this.tag = tag
    }

    override fun d(message: String) {
        log(LogLevel.Debug, message)
    }

    override fun i(message: String) {
        log(LogLevel.Info, message)
    }

    override fun w(message: String) {
        log(LogLevel.Warning, message)
    }

    override fun e(message: String) {
        log(LogLevel.Error, message)
    }

    private fun log(
        level: LogLevel,
        message: String,
    ) {
        platformLog(level, tag, message)
    }
}

internal expect fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
)
