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

        Log.d("Notification", "Notification dismissed")

        val x = ContloAPI()
        if (internalID != null) {
            x.sendPushCallbacks(context,"dismissed", internalID)
        }


    }
}