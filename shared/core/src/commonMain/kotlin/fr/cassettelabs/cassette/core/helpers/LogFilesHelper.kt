package fr.cassettelabs.cassette.core.helpers

interface LogFilesHelper {
    suspend fun getLogFiles(): List<LogFileInfo>

    suspend fun exportLogDirectory(): Boolean
}
