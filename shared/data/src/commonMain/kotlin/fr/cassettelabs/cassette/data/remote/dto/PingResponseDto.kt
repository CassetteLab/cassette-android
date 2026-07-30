package fr.cassettelabs.cassette.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PingResponseDto(
    @SerialName("subsonic-response")
    val subsonicResponse: PingSubsonicResponseDto,
)

@Serializable
internal data class PingSubsonicResponseDto(
    val status: String,
)
