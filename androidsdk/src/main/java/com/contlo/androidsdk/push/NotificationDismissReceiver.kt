package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.contlo.androidsdk.api.ContloAPI
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.utils.ContloUtils

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        ContloUtils.printLog(Contlo.getContext(), "Contlo-Notification", "Push dismissed")

        val internalID = intent?.getStringExtra("internal_id")
        val contloAPI = ContloAPI(context)

        if (!internalID.isNullOrBlank()) {
            contloAPI.sendPushCallbacks("dismissed", internalID)
        }

    }
}