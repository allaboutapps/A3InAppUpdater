package at.allaboutapps.inappupdaterdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.allaboutapps.inappupdater.InAppUpdateManager
import at.allaboutapps.inappupdater.InAppUpdateStatus
import io.reactivex.rxjava3.disposables.Disposable

class ForceUpdateDemoActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, ForceUpdateDemoActivity::class.java)
    }

    private lateinit var inAppUpdateManager: InAppUpdateManager
    private var inAppUpdateStatusDisposable = Disposable.empty()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_force_update)
        initInAppUpdate()
    }

    private fun initInAppUpdate() {
        inAppUpdateManager = InAppUpdateManager(this, DemoForceUpdateProvider())
        inAppUpdateStatusDisposable = inAppUpdateManager.observeInAppUpdateStatus()
            .subscribe { currentStatus ->
                if (currentStatus.isUpdatePending()) {
                    inAppUpdateManager.startUpdate()
                }
                updateUI(currentStatus)
            }
    }

    fun updateUI(currentStatus: InAppUpdateStatus) {
        //Do nothing -> just a demo for forced update
    }

    /**
     * Immediate update type page is not uncloseable
     * Just recall startUpdate at onActivityResult
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        inAppUpdateManager.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        if (!inAppUpdateStatusDisposable.isDisposed)
            inAppUpdateStatusDisposable.dispose()
        super.onDestroy()
    }
}