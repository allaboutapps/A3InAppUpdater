package at.allaboutapps.inappupdater

import android.app.Activity
import androidx.annotation.IntDef
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.reactivex.Observable
import io.reactivex.disposables.Disposables

/**
 * InAppUpdateManager handles the in app update process
 *
 * Usage:
 * TBD
 */
class InAppUpdateManager(private val activity: Activity) {

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
            emitter.setDisposable(Disposables.fromAction { appUpdateManager.unregisterListener(updateStateListener) })


            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                currentInAppUpdateStatus = currentInAppUpdateStatus.copy(appUpdateInfo = appUpdateInfo)

                // if there already is an update progress in progress we just setup it to resume
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    //set state if app gets reopened with an update in progress
                    currentInAppUpdateStatus = currentInAppUpdateStatus.copy(
                        appUpdateState = InstallState(
                            appUpdateInfo.installStatus(),
                            0,
                            activity.packageName
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
    fun startUpdate(@InAppUpdateType updateType: Int = AppUpdateType.FLEXIBLE) {
        //to be save
        //refetch the update status before starting the update process
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            currentInAppUpdateStatus = currentInAppUpdateStatus.copy(appUpdateInfo = appUpdateInfo)

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


    @Retention(AnnotationRetention.SOURCE)
    @IntDef(UPDATE_TYPE_FLEXIBLE, UPDATE_TYPE_IMMEDIATE)
    annotation class InAppUpdateType
}