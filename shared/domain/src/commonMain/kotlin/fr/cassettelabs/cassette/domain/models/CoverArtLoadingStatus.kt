package fr.cassettelabs.cassette.domain.models

sealed interface CoverArtLoadingStatus {
    data object Loading : CoverArtLoadingStatus

    data class Loaded(val filePath: String) : CoverArtLoadingStatus

    data class Error(val throwable: Throwable) : CoverArtLoadingStatus
}
