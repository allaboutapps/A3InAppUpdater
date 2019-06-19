package at.allaboutapps.inappupdaterdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import at.allaboutapps.inappupdater.InAppUpdateManager
import at.allaboutapps.inappupdater.InAppUpdateStatus
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_update_type.*

class UpdateTypeDemoActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, UpdateTypeDemoActivity::class.java)
    }

    private lateinit var inAppUpdateManager: InAppUpdateManager
    private var inAppUpdateStatusDisposable = Disposables.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_type)

        tvPackageName.text = packageName
        tvCurrentVersionCode.text = BuildConfig.VERSION_CODE.toString()

        initInAppUpdate()

        btUpdate.setOnClickListener {
            inAppUpdateManager.startUpdate(getSelectedUpdateType())
        }

        btRestart.setOnClickListener {
            inAppUpdateManager.completeUpdate()
        }
    }

    @InAppUpdateManager.InAppUpdateType
    private fun getSelectedUpdateType() =
        if (updateTypeGroup.checkedButtonId == R.id.btImmediate) InAppUpdateManager.UPDATE_TYPE_IMMEDIATE else InAppUpdateManager.UPDATE_TYPE_FLEXIBLE

    private fun initInAppUpdate() {
        inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateStatusDisposable = inAppUpdateManager.observeInAppUpdateStatus()
            .subscribe { currentStatus ->
                if (currentStatus.isUpdatePending()) {
                    inAppUpdateManager.startUpdate()
                }
                updateUI(currentStatus)
            }
    }

    private fun updateUI(currentStatus: InAppUpdateStatus) {
        if (currentStatus.isUpdateInProgress) {
            showUpdateInProgressState(currentStatus)
        } else {
            tvAvailableAppVersion.text = if (currentStatus.availableVersionCode == 0) {
                "App not published in Play Store yet!"
            } else {
                currentStatus.availableVersionCode.toString()
            }
            if (currentStatus.isUpdateAvailable()) {
                tvUpdateAvailable.text = "YES"
                showUpdateAvailableState()
            } else {
                tvUpdateAvailable.text = "NO"
            }
        }
    }

    private fun showUpdateAvailableState() {
        btUpdate.visibility = View.VISIBLE
        vgUpdateInProgress.visibility = View.GONE
        vgUpdateAvailable.visibility = View.VISIBLE
        vgUpdateFinished.visibility = View.GONE
    }

    private fun showUpdateInProgressState(currentStatus: InAppUpdateStatus) {
        if (currentStatus.isDownloading) {
            vgUpdateInProgress.visibility = View.VISIBLE
            vgUpdateAvailable.visibility = View.GONE
            vgUpdateFinished.visibility = View.GONE
        } else if (currentStatus.isDownloaded) {
            vgUpdateInProgress.visibility = View.GONE
            vgUpdateAvailable.visibility = View.GONE
            vgUpdateFinished.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        if (!inAppUpdateStatusDisposable.isDisposed)
            inAppUpdateStatusDisposable.dispose()
        super.onDestroy()
    }


}
