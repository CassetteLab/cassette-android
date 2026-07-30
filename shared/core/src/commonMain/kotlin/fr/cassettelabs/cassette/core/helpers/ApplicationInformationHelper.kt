package fr.cassettelabs.cassette.core.helpers

interface ApplicationInformationHelper {
    val versionName: String
    val versionCode: String
    val isDebugBuild: Boolean
}
