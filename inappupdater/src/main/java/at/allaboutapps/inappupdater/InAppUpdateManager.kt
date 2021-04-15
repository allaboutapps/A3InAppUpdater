package at.allaboutapps.inappupdater

import android.app.Activity
import androidx.annotation.IntDef
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * InAppUpdateManager handles the in app update process
 *
 * Usage:
 * TBD
 */
class InAppUpdateManager(
    private val activity: Activity,
    private val forceUpdateProvider: ForceUpdateProvider? = null
) {

    companion object {
        const val REQUEST_CODE_IN_APP_UPDATE = 1230
        const val UPDATE_TYPE_FLEXIBLE = AppUpdateType.FLEXIBLE
        const val UPDATE_TYPE_IMMEDIATE = AppUpdateType.IMMEDIATE
    }

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private var currentInAppUpdateStatus = InAppUpdateStatus()

    /**
     * Observe the status of an in app update process
     * currentInAppUpdateStatus.appUpdateInfo  ... contains info about availability of an app update
     * currentInAppUpdateStatus.appUpdateState ... contains info about the current update process
     */
    fun observeInAppUpdateStatus(): Observable<InAppUpdateStatus> {
        return Observable.create { emitter ->
            val updateStateListener = InstallStateUpdatedListener { state ->
                if (currentInAppUpdateStatus.appUpdateState?.installStatus() != state.installStatus()) {
                    currentInAppUpdateStatus = currentInAppUpdateStatus.copy(appUpdateState = state)
                    emitter.onNext(currentInAppUpdateStatus)

                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        emitter.onComplete()
                    }
                }
            }

            // register listener
            appUpdateManager.registerListener(updateStateListener)

            // unregister listener on dispose
            emitter.setDisposable(Disposable.fromAction {
                appUpdateManager.unregisterListener(
                    updateStateListener
                )
            })

            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                currentInAppUpdateStatus =
                    currentInAppUpdateStatus.copy(appUpdateInfo = appUpdateInfo)

                // handle a forced update
                forceUpdateProvider?.requestUpdateShouldBeImmediate(currentInAppUpdateStatus.availableVersionCode) {
                    startUpdate(UPDATE_TYPE_IMMEDIATE)
                }

                // if there already is an update progress in progress we just setup it to resume
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    //set state if app gets reopened with an update in progress
                    currentInAppUpdateStatus = currentInAppUpdateStatus.copy(
                        appUpdateState = InstallState.a(
                            appUpdateInfo.installStatus(), // installStatus
                            appUpdateInfo.bytesDownloaded(), // bytesDownloaded
                            appUpdateInfo.totalBytesToDownload(), // totalBytesToDownload
                            InstallErrorCode.NO_ERROR, // installErrorCode
                            activity.packageName // packageName
                        )
                    )
                }
                emitter.onNext(currentInAppUpdateStatus)
            }
        }
    }


    /**
     * Starts an in app update process
     *
     * @param updateType set the type of the in app update
     */
    fun startUpdate(@InAppUpdateType updateType: Int = UPDATE_TYPE_FLEXIBLE) {
        // to be saved
        // refetch the update status before starting the update process
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            currentInAppUpdateStatus = currentInAppUpdateStatus.copy(appUpdateInfo = appUpdateInfo)

            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
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

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_IN_APP_UPDATE && resultCode == Activity.RESULT_CANCELED) {
            startUpdate(UPDATE_TYPE_IMMEDIATE)
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(UPDATE_TYPE_FLEXIBLE, UPDATE_TYPE_IMMEDIATE)
    annotation class InAppUpdateType
}
