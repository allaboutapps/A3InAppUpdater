package at.allaboutapps.inappupdater

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallState

/**
 * This class is just a wrapper for AppupdateInfo and InstallState
 * Used by InAppUpdateManager
 */
class InAppUpdateStatus {
    var appUpdateInfo: AppUpdateInfo? = null
    var appUpdateState: InstallState? = null
}