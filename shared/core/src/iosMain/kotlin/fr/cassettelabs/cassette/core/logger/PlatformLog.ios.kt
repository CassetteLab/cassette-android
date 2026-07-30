package fr.cassettelabs.cassette.core.logger

import platform.Foundation.NSLog

internal actual fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
) {
    NSLog("[${level.label}][$tag] $message")
}
