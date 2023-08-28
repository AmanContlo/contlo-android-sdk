package com.contlo.androidsdk.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.contlo.androidsdk.api.ContloAPI
import com.contlo.androidsdk.main.SDKApplication
import org.json.JSONObject

class ContloSDKLifecycleCallbacks(private val context: Context) : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var isAppinBackground = false
    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {

            Log.d("Contlo-AppState", "App is in foreground")

            val sharedPreferences = context.getSharedPreferences("contlosdk",Context.MODE_PRIVATE)

            if(sharedPreferences.contains("NEW_APP_INSTALL")){
                sendAppEvent("mobile_app_launched")
            }

        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        val app = activity.application as SDKApplication
        val extras = app.pendingIntentExtras

        if (extras != null && extras.getBoolean("notification_clicked")) {

            val internalID = extras.getString("internal_id",null)
            val contloAPI = ContloAPI(context)
            internalID?.let { contloAPI.sendPushCallbacks("clicked", it) }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(0)

            app.pendingIntentExtras = null
        }

    }
    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityResumed(activity: Activity) { isAppinBackground = false }

    override fun onActivityPaused(activity: Activity) {

        isAppinBackground = true
        Handler(Looper.getMainLooper()).postDelayed({
            if(isAppinBackground){
                Log.d("Contlo-AppState","App Backgrounded")
                sendAppEvent("mobile_app_backgrounded")
            }
        }, 500)

    }
    override fun onActivityDestroyed(activity: Activity) {
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    fun sendAppEvent(event: String){

        val contloAPI = ContloAPI(context)
        val prop = JSONObject()

        contloAPI.sendEvent(event,null,null,prop,null)

    }

}

