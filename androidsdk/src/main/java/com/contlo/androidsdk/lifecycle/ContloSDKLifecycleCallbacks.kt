package com.contlo.androidsdk.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationManagerCompat
import com.contlo.androidsdk.api.ApiService
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContloSDKLifecycleCallbacks(private val context: Context) : Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var isAppinBackground = false
    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {

            ContloUtils.printLog(Contlo.getContext(), "Contlo-AppState", "App is in foreground")
            if(!ContloPreference.getInstance(activity.application.applicationContext).isNewAppInstall()) {
                sendAppEvent("mobile_app_launched")
            }

        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        val extras = activity.intent
        if (extras != null && extras.getBooleanExtra("notification_clicked", false)) {

            val internalID = extras.getStringExtra("internal_id")
            if (internalID != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    ApiService.sendClickCallback(activity.applicationContext, internalID)
                }
            }
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(0)
        }
    }
    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityPreStopped(activity: Activity) {
        super.onActivityPreStopped(activity)
    }

    override fun onActivityResumed(activity: Activity) { isAppinBackground = false }

    override fun onActivityPaused(activity: Activity) {

        isAppinBackground = true
        Handler(Looper.getMainLooper()).postDelayed({
            if(isAppinBackground){
                ContloUtils.printLog(Contlo.getContext(), "Contlo-AppState","App Backgrounded")
                sendAppEvent("mobile_app_backgrounded")
            }
        }, 500)

    }
    override fun onActivityDestroyed(activity: Activity) {
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    fun sendAppEvent(event: String){
        Contlo.sendAppEvent(event, null, null)

    }

}

