package fr.cassettelabs.cassette.core.helpers

data class LogFileInfo(
    val name: String,
    val sizeBytes: Long,
) {
    val formattedSize: String
        get() = when {
            sizeBytes >= BYTES_IN_MEGABYTE -> "${sizeBytes / BYTES_IN_MEGABYTE} MB"
            sizeBytes >= BYTES_IN_KILOBYTE -> "${sizeBytes / BYTES_IN_KILOBYTE} KB"
            else -> "$sizeBytes B"
        }

    private companion object {
        const val BYTES_IN_KILOBYTE = 1024L
        const val BYTES_IN_MEGABYTE = BYTES_IN_KILOBYTE * 1024L
    }
}
