# A3InAppUpdater

[![Build Status](https://travis-ci.org/allaboutapps/InAppUpdater.svg?branch=master)](https://travis-ci.org/allaboutapps/InAppUpdater)
[ ![Download](https://api.bintray.com/packages/allaboutapps/A3-Android/at.allaboutapps.inappupdater.a3inappupdater/images/download.svg) ](https://bintray.com/allaboutapps/A3-Android/at.allaboutapps.inappupdater.a3inappupdater/_latestVersion)


![all about apps](https://www.allaboutapps.at/wp-content/uploads/2017/06/aaa-Logo-black-646x165.png "all about apps")

###### Looking for a job as android developer located in vienna? [Just contact us](https://www.allaboutapps.at/jobs/android-app-entwickler/)


This library aims to simplify Android [In App Updates](https://developer.android.com/guide/app-bundle/in-app-updates). You can force your users to update to latest app version with just a few lines of code. 



### DEMO VIDEOS

![Flexible update](assets/flexible_update.gif)

[(YOUTUBE) Demo type FLEXIBLE](https://youtu.be/c4IGhj5E7eI)

[(YOUTUBE) Demo type IMMEDIATE](https://youtu.be/mr5SGbXs3ec)

[(YOUTUBE) Demo Forced update](https://youtu.be/9R7vMctW87I)

# Usage


## Add to project

```Gradle
    implementation 'at.allaboutapps.inappupdater:inappupdater:{latest_version}'
```

## Initialize the InAppUpdateManager
```Kotlin
    inAppUpdateManager = InAppUpdateManager(activity)
```

## Subscribe to updates 
```Kotlin
    inAppUpdateStatusDisposable = inAppUpdateManager.observeInAppUpdateStatus()
        .subscribe { currentStatus ->
            if (currentStatus.isUpdatePending()) { //just start the update if there is one in progress
                inAppUpdateManager.startUpdate()
            }
            updateUI(currentStatus) // update the ui when a new status arrives
        }
```

## Update Mode
There are two modes
* Flexible *(default)* - User can use the app during update download, installation and restart needs to be triggered by user

* Immediate - User will be blocked until download and installation is finished, restart is triggered automatically


#### Start the update process
```Kotlin
    inAppUpdateManager.startUpdate(updateType)
```


## Forced updates
There are some reasons when an update is mandatory. For this case you can implement a provider interface to decide if an update is a forced update

```Kotlin
   class DemoForceUpdateProvider : ForceUpdateProvider {

    override fun requestUpdateShouldBeImmediate(availableVersionCode: Int, doUpdate: () -> Unit) {

        // place your logic here

        // if a forced update is needed, just call doUpdate
        doUpdate()

    }
```

Just provide to the InAppUpdateManager a second, optional parameter
```Kotlin
    inAppUpdateManager = InAppUpdateManager(this, DemoForceUpdateProvider())
```

The force update activity screen provided by Google Play Core library can be closed through the back button. So we need to override onActivityResult to force the update again
```Kotlin
      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        inAppUpdateManager.onActivityResult(requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
    }
```


### How to setup the demo app

In App Updates needs a newer version of the app available in Play Store.

1. Add a SigningConfig to build.gradle
2. Set versionCode to 9000 // or any other high number
3. Set a unique application id
4. Generate APK / AAB
5. Upload APK / AAB to your play store account
6. Do not use App signing ( Google will resign your app, so you cannot test it with local installations)
7. Release app to play store (app must be published and available)
8. Set versionCode to a lower number
9. Run app 


### Any Troubles?

* In-app updates are available only to user accounts that own the app. So, make sure the account you’re using has downloaded your app from Google Play *at least once before* using the account to test in-app updates.
* Make sure that the app that you are testing in-app updates with has the same application ID and is signed with the same signing key as the one available from Google Play.