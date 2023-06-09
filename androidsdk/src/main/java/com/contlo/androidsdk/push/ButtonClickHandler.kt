package com.contlo.androidsdk.push

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.contlo.androidsdk.api.ContloAPI

class ButtonClickHandler : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Log.d("Contlo-Notification", "Button Clicked")

        val deepLink = intent.getStringExtra("deep_link")
        val internalID = intent.getStringExtra("internal_id")

        val x = ContloAPI(context)
        internalID?.let { x.sendPushCallbacks("clicked", it) }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(0)

        val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent1)



    }
}