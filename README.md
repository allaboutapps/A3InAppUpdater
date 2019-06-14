# A3InAppUpdater

![all about apps](https://www.allaboutapps.at/wp-content/uploads/2017/06/aaa-Logo-black-646x165.png "all about apps")

###### Looking for a job as android developer located in vienna? [Just contact us](https://www.allaboutapps.at/jobs/android-app-entwickler/)


This library aims to simplify Android [In App Updates](https://developer.android.com/guide/app-bundle/in-app-updates). You can force your users to update to latest app version with just a few lines of code. 

### DEMO VIDEOS
[(YOUTUBE) Demo type FLEXIBLE](https://youtu.be/c4IGhj5E7eI)

[(YOUTUBE) Demo type IMMEDIATE](https://youtu.be/mr5SGbXs3ec)

# Usage

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


### How to setup the demo app

In App Updates needs a newer version of the app available in Play Store.

1. Add a SigningConfig to build.gradle
2. Set versionCode to 9000
3. Set a unique application id
4. Generate APK / AAB
5. Upload APK / AAB to your play store account
6. Do not use App signing ( Google will resign your app, so you cannot test it with local installations)
7. Release app to play store (app must be published and available)
8. Set versionCode to a lower number
9. Run app 


### Any Troubles?

* In-app updates are available only to user accounts that own the app. So, make sure the account you’re using has downloaded your app from Google Play at least once before using the account to test in-app updates.
* Make sure that the app that you are testing in-app updates with has the same application ID and is signed with the same signing key as the one available from Google Play.