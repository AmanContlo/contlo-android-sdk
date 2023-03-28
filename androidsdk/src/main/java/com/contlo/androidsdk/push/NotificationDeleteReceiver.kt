package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.api.TrackAPI

class NotificationDeleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("Notification", "Notification dismissed")

        Toast.makeText(context,"Notification Dismissed", Toast.LENGTH_SHORT ).show()

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val app_name = sharedPreferences.getString("APP_NAME", null)
        val api_level = sharedPreferences.getString("API_LEVEL", null)
        val os_version = sharedPreferences.getString("OS_VERSION", null)

        val x = TrackAPI()
        x.sendMobileEvents(context,"mobile_push_notification_dismissed",app_name, api_level, os_version)


    }
}