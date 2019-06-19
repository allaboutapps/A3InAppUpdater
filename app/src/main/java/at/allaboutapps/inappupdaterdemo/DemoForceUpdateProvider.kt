package at.allaboutapps.inappupdaterdemo

import at.allaboutapps.inappupdater.ForceUpdateProvider

/**
 * Implementation of ForceUpdateProvider to start a forced update
 */
class DemoForceUpdateProvider : ForceUpdateProvider {

    override fun requestUpdateShouldBeImmediate(availableVersionCode: Int, doUpdate: () -> Unit) {

        // place your logic here

        // if a forced update is needed, just call doUpdate
        doUpdate()

    }
}