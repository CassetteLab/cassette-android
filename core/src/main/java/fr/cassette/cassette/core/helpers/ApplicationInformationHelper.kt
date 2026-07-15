package fr.cassette.cassette.core.helpers

interface ApplicationInformationHelper {
    val versionName: String
    val versionCode: String
    val isDebugBuild: Boolean
}