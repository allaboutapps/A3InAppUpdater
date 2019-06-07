package at.allaboutapps.inappupdater

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import io.reactivex.Observable

/**
 * InAppUpdateManager handles the in app update process
 *
 * Usage:
 * TBD
 */
class InAppUpdateManager(val activity: Activity) {

    companion object {
        const val REQUEST_CODE_IN_APP_UPDATE = 1230
    }

    var updateType = AppUpdateType.FLEXIBLE

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private val currentInAppUpdateStatus = InAppUpdateStatus()

    /**
     * Observe the status of an in app update process
     * currentInAppUpdateStatus.appUpdateInfo  ... contains info about availability of an app update
     * currentInAppUpdateStatus.appUpdateState ... contains info about the current update process
     */
    fun observeInAppUpdateStatus(): Observable<InAppUpdateStatus> {
        return Observable.create { emitter ->

            val updateStateListener = InstallStateUpdatedListener { state ->
                if (currentInAppUpdateStatus.appUpdateState?.installStatus() != state.installStatus()) {
                    currentInAppUpdateStatus.appUpdateState = state
                    emitter.onNext(currentInAppUpdateStatus)
                }
            }

            appUpdateManager.unregisterListener(updateStateListener)
            appUpdateManager.registerListener(updateStateListener)

            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                currentInAppUpdateStatus.appUpdateInfo = appUpdateInfo

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    //set state if app gets reopened with an update in progress
                    currentInAppUpdateStatus.appUpdateState =
                        InstallState(appUpdateInfo.installStatus(), 0, activity.packageName)
                }
                emitter.onNext(currentInAppUpdateStatus)
            }
        }
    }

    /**
     * Starts an in app update process
     */
    fun startUpdate() {
        //to be save
        //refetch the update status before starting the update process
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            currentInAppUpdateStatus.appUpdateInfo = appUpdateInfo

            appUpdateManager.startUpdateFlowForResult(
                currentInAppUpdateStatus.appUpdateInfo,
                updateType,
                activity,
                REQUEST_CODE_IN_APP_UPDATE
            )
        }
    }

    /**
     * If a download is complete, start the installation with this method
     */
    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

}