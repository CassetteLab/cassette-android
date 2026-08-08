package fr.cassettelabs.cassette.core.helpers

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

class AndroidLogFilesHelper(
    private val context: Context,
) : LogFilesHelper {
    private val logDirectory: File
        get() = File(context.filesDir, "logs")

    override suspend fun getLogFiles(): List<LogFileInfo> =
        logDirectory
            .listFiles { file -> file.isFile && file.name.startsWith(LOG_FILE_NAME) && file.extension == LOG_EXTENSION }
            .orEmpty()
            .sortedBy { file -> file.name }
            .map { file -> LogFileInfo(name = file.name, sizeBytes = file.length()) }

    override suspend fun exportLogDirectory(): Boolean {
        val files = getLogFiles().map { logFile -> File(logDirectory, logFile.name) }
        if (files.isEmpty()) return false

        val uris =
            files.map { file ->
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
            }

        val intent =
            Intent(Intent.ACTION_SEND_MULTIPLE)
                .setType("text/plain")
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooser = Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
        return true
    }

    private companion object {
        const val LOG_FILE_NAME = "cassette"
        const val LOG_EXTENSION = "log"
    }
}
