package fr.cassettelabs.cassette.core.logger.implementations

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.io.RollingFileLogWriter
import co.touchlab.kermit.io.RollingFileLogWriterConfig
import java.io.File
import kotlinx.io.files.Path

internal actual fun platformFileLogWriter(): LogWriter? {
    val logDirectory = File(System.getProperty("user.home"), ".cassette/logs")
    logDirectory.mkdirs()

    return RollingFileLogWriter(
        config =
            RollingFileLogWriterConfig(
                logFileName = "cassette",
                logFilePath = Path(logDirectory.path),
            ),
    )
}
