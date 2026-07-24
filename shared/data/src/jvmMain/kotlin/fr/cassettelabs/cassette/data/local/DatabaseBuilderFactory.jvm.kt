package fr.cassettelabs.cassette.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseBuilderFactory {
    internal actual fun create(): RoomDatabase.Builder<CassetteDatabase> {
        val dbFile = File(System.getProperty("user.home"), ".cassette/cassette.db")
        dbFile.parentFile.mkdirs()
        return Room.databaseBuilder<CassetteDatabase>(name = dbFile.absolutePath)
    }
}
