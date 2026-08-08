package fr.cassettelabs.cassette.core.logger.implementations

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.io.RollingFileLogWriter
import co.touchlab.kermit.io.RollingFileLogWriterConfig
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
internal actual fun platformFileLogWriter(): LogWriter? {
    val applicationSupportPath =
        NSFileManager.defaultManager
            .URLForDirectory(
                directory = NSApplicationSupportDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null,
            )
            ?.path
            ?: return null

    return RollingFileLogWriter(
        config =
            RollingFileLogWriterConfig(
                logFileName = "cassette",
                logFilePath = Path(applicationSupportPath),
            ),
    )
}
