package fr.cassette.cassette.core.helpers.implementations

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import fr.cassette.cassette.core.helpers.ApplicationInformationHelper

internal class AndroidApplicationInformationHelperImpl(
    private val context: Context,
) : ApplicationInformationHelper {
    private val packageInfo: PackageInfo
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }

    override val versionName: String
        get() = packageInfo.versionName.orEmpty()

    override val versionCode: String
        get() = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

    override val isDebugBuild: Boolean
        get() = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
