package fr.cassettelabs.cassette.core.helpers

import java.awt.Desktop
import java.io.File

class JvmLogFilesHelper : LogFilesHelper {
    private val logDirectory: File
        get() = File(System.getProperty("user.home"), ".cassette/logs")

    override suspend fun getLogFiles(): List<LogFileInfo> =
        logDirectory
            .listFiles { file -> file.isFile && file.name.startsWith(LOG_FILE_NAME) && file.extension == LOG_EXTENSION }
            .orEmpty()
            .sortedBy { file -> file.name }
            .map { file -> LogFileInfo(name = file.name, sizeBytes = file.length()) }

    override suspend fun exportLogDirectory(): Boolean {
        if (!logDirectory.exists() || !Desktop.isDesktopSupported()) return false

        Desktop.getDesktop().open(logDirectory)
        return true
    }

    private companion object {
        const val LOG_FILE_NAME = "cassette"
        const val LOG_EXTENSION = "log"
    }
}
