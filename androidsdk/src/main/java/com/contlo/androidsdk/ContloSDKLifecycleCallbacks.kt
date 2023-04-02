package com.contlo.androidsdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class ContloSDKLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground state from any start state (background killed, warm or cold start)
            Log.d("YourSDK", "App is in foreground")
        }
    }

    // Other lifecycle methods, leave them empty if not needed
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

}

