package com.contlo.androidsdk

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

            val sharedPreferences = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)

            if(sharedPreferences.contains("NEW_APP_INSTALL")){
                Handler(Looper.getMainLooper()).postDelayed({

                    sendAppLaunch()

               }, 5000)
            }


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

    fun sendAppLaunch(){

        val contloAPI = ContloAPI(context)
        val prop = JSONObject()

        contloAPI.sendEvent("mobile_app_launched",prop)

    }
}

