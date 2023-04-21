package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.api.ContloAPI

class NotificationDeleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val internalID = intent?.getStringExtra("internal_id")

        Log.d("Contlo-Notification", "Push dismissed")

        val x = ContloAPI(context)
        if (internalID != null) {
            x.sendPushCallbacks("dismissed", internalID)
        }


    }
}