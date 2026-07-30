package fr.cassettelabs.cassette.data.remote.ktor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

@OptIn(ExperimentalForeignApi::class)
internal actual fun currentTimeMillis(): Long =
    memScoped {
        val time = alloc<timeval>()
        gettimeofday(time.ptr, null)
        time.tv_sec * 1000L + time.tv_usec / 1000L
    }
