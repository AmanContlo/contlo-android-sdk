package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotificationDeleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("Notification", "Notification dismissed")

        Toast.makeText(context,"Notification Dismissed", Toast.LENGTH_SHORT ).show()

    }
}