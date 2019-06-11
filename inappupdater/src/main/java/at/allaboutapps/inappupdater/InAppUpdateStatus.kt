package at.allaboutapps.inappupdater

import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * This class is just a wrapper for AppupdateInfo and InstallState
 * Used by InAppUpdateManager
 */
class InAppUpdateStatus {
    var appUpdateInfo: AppUpdateInfo? = null
    var appUpdateState: InstallState? = null


    val availableVersionCode: Int
        get() = appUpdateInfo?.availableVersionCode() ?: -1
    /**
     * Checks if an update is in progress
     */
    val isUpdateInProgress: Boolean
        get() = appUpdateState != null


    val isDownloading: Boolean
        get() = appUpdateState?.installStatus() == InstallStatus.DOWNLOADING

    val isDownloaded: Boolean
        get() = appUpdateState?.installStatus() == InstallStatus.DOWNLOADED

    /**
     * Checks if an update is available
     */
    fun isUpdateAvailable() = appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

    /**
     * Checks if an update is pending -> downloading/downloaded but not installed
     */
    fun isUpdatePending() =
        appUpdateInfo?.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}