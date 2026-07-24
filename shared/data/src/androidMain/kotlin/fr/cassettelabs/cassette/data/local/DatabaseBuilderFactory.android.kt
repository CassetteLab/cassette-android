package fr.cassettelabs.cassette.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseBuilderFactory(
    private val context: Context,
) {
    internal actual fun create(): RoomDatabase.Builder<CassetteDatabase> =
        Room.databaseBuilder(
            context = context.applicationContext,
            name = context.applicationContext.getDatabasePath(DATABASE_NAME).absolutePath,
        )
}

private const val DATABASE_NAME = "cassette.db"
