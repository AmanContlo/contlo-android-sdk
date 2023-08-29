package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.contlo.androidsdk.api.ContloAPI

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("Contlo-Notification", "Push dismissed")

        val internalID = intent?.getStringExtra("internal_id")
        val contloAPI = ContloAPI(context)

        if (!internalID.isNullOrBlank()) {
            contloAPI.sendPushCallbacks("dismissed", internalID)
        }

    }
}