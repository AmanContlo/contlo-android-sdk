package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.contlo.androidsdk.api.ContloAPI

class ButtonClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Log.d("Contlo-Notification", "Button Clicked")

        val deepLink = intent.getStringExtra("deep_link")
        val internalID = intent.getStringExtra("internal_id")

        val contloAPI = ContloAPI(context)
        internalID?.let { contloAPI.sendPushCallbacks("clicked", it) }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(0)

        if(!deepLink.isNullOrBlank()){
            val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent1)
        }

    }
}