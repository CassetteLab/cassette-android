package fr.cassettelabs.cassette

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform