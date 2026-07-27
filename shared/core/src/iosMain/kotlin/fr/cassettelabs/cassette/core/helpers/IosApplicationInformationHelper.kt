package fr.cassettelabs.cassette.core.helpers

import platform.Foundation.NSBundle

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
class IosApplicationInformationHelper : ApplicationInformationHelper {
    private val bundle: NSBundle = NSBundle.mainBundle

    override val versionName: String
        get() = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: ""

    override val versionCode: String
        get() = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: ""

    override val isDebugBuild: Boolean = Platform.isDebugBinary
}
