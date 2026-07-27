package fr.cassettelabs.cassette.core.logger

import android.util.Log

internal actual fun platformLog(
    level: LogLevel,
    tag: String,
    message: String,
) {
    when (level) {
        LogLevel.Debug -> Log.d(tag, message)
        LogLevel.Info -> Log.i(tag, message)
        LogLevel.Warning -> Log.w(tag, message)
        LogLevel.Error -> Log.e(tag, message)
    }
}
