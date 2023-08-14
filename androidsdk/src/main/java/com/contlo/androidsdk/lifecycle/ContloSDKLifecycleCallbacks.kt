package com.contlo.androidsdk.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.contlo.androidsdk.api.ContloAPI
import org.json.JSONObject

class ContloSDKLifecycleCallbacks(private val context: Context) : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground state from any start state (background killed, warm or cold start)
            Log.d("Contlo-AppState", "App is in foreground")

            val sharedPreferences = context.getSharedPreferences("contlosdk",Context.MODE_PRIVATE)
            if(sharedPreferences.contains("NEW_APP_INSTALL")){

                sendAppEvent("mobile_app_launched")
            }

        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {

        Log.d("Contlo-AppState","App Backgrounded")

    }
    override fun onActivityDestroyed(activity: Activity) {

        Log.d("Contlo-AppState","App Killed")

    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    fun sendAppEvent(event: String){

        val contloAPI = ContloAPI(context)
        val prop = JSONObject()

        contloAPI.sendEvent(event,prop,null)

    }

}

