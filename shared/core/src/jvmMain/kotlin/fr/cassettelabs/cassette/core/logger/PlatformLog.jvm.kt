package fr.cassettelabs.cassette.core.logger

internal actual fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
) {
    val consoleMessage = "[${level.label}][$tag] $message"
    when (level) {
        LogLevel.Warning,
        LogLevel.Error,
        -> System.err.println(consoleMessage)
        LogLevel.Debug,
        LogLevel.Info,
        -> println(consoleMessage)
    }
}
