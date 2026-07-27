package fr.cassettelabs.cassette.core.helpers

class DesktopApplicationInformationHelper : ApplicationInformationHelper {
    override val versionName: String = System.getProperty("cassette.versionName", "")
    override val versionCode: String = System.getProperty("cassette.versionCode", "")
    override val isDebugBuild: Boolean = System.getProperty("cassette.debug", "true").toBoolean()
}
