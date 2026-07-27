package fr.cassettelabs.cassette.data.local

import androidx.room.RoomDatabase

expect class DatabaseBuilderFactory {
    internal fun create(): RoomDatabase.Builder<CassetteDatabase>
}
