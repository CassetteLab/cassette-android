package fr.cassettelabs.cassette.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilderFactory {
    @OptIn(ExperimentalForeignApi::class)
    internal actual fun create(): RoomDatabase.Builder<CassetteDatabase> {
        val documentDirectory =
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            ) ?: error("Could not resolve document directory")
        val dbPath = documentDirectory.path + "/cassette.db"
        return Room.databaseBuilder<CassetteDatabase>(name = dbPath)
    }
}
