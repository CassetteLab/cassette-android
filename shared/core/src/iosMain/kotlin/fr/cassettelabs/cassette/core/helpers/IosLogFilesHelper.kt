package fr.cassettelabs.cassette.core.helpers

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class IosLogFilesHelper : LogFilesHelper {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getLogFiles(): List<LogFileInfo> {
        val logDirectoryPath =
            NSFileManager.defaultManager
                .URLForDirectory(
                    directory = NSApplicationSupportDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
                ?.path
                ?: return emptyList()

        return NSFileManager.defaultManager
            .contentsOfDirectoryAtPath(logDirectoryPath, error = null)
            .orEmpty()
            .filterIsInstance<String>()
            .filter { fileName -> fileName.startsWith(LOG_FILE_NAME) && fileName.endsWith(".$LOG_EXTENSION") }
            .sorted()
            .map { fileName ->
                val filePath = "$logDirectoryPath/$fileName"
                val attributes = NSFileManager.defaultManager.attributesOfItemAtPath(filePath, error = null)
                val sizeBytes = (attributes?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
                LogFileInfo(name = fileName, sizeBytes = sizeBytes)
            }
    }

    override suspend fun exportLogDirectory(): Boolean {
        val logFileUrls = getLogFilePaths().map { filePath -> NSURL.fileURLWithPath(filePath) }
        if (logFileUrls.isEmpty()) return false

        val presentingViewController = topViewController(UIApplication.sharedApplication.keyWindow?.rootViewController)
            ?: return false

        dispatch_async(dispatch_get_main_queue()) {
            val activityViewController = UIActivityViewController(
                activityItems = logFileUrls,
                applicationActivities = null,
            )

            presentingViewController.presentViewController(
                viewControllerToPresent = activityViewController,
                animated = true,
                completion = null,
            )
        }

        return true
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getLogFilePaths(): List<String> {
        val logDirectoryPath = logDirectoryPath() ?: return emptyList()

        return NSFileManager.defaultManager
            .contentsOfDirectoryAtPath(logDirectoryPath, error = null)
            .orEmpty()
            .filterIsInstance<String>()
            .filter { fileName -> fileName.startsWith(LOG_FILE_NAME) && fileName.endsWith(".$LOG_EXTENSION") }
            .sorted()
            .map { fileName -> "$logDirectoryPath/$fileName" }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun logDirectoryPath(): String? =
        NSFileManager.defaultManager
            .URLForDirectory(
                directory = NSApplicationSupportDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            ?.path

    private fun topViewController(viewController: UIViewController?): UIViewController? {
        val presentedViewController = viewController?.presentedViewController
        if (presentedViewController != null) {
            return topViewController(presentedViewController)
        }

        return when (viewController) {
            is UINavigationController -> topViewController(viewController.visibleViewController)
            is UITabBarController -> topViewController(viewController.selectedViewController)
            else -> viewController
        }
    }

    private companion object {
        const val LOG_FILE_NAME = "cassette"
        const val LOG_EXTENSION = "log"
    }
}
