package fr.cassettelabs.cassette.core.logger.implementations

import android.content.Context
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.io.RollingFileLogWriter
import co.touchlab.kermit.io.RollingFileLogWriterConfig
import kotlinx.io.files.Path
import org.koin.core.context.GlobalContext

internal actual fun platformFileLogWriter(): LogWriter? {
    val context = GlobalContext.get().get<Context>()

    return RollingFileLogWriter(
        config =
            RollingFileLogWriterConfig(
                logFileName = "cassette",
                logFilePath = Path(context.filesDir.path),
            ),
    )
}
