package com.mgacreative.mgaglobal

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getNowMillis(): Long = (platform.Foundation.NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun openUrl(url: String) {
    NSURL.URLWithString(url)?.let { UIApplication.sharedApplication.openURL(it) }
}

