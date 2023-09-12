package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.contlo.androidsdk.api.ApiService
import com.contlo.androidsdk.utils.ContloUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        ContloUtils.printLog("Contlo-Notification", "Push dismissed")

        val internalID = intent?.getStringExtra("internal_id")
        if (!internalID.isNullOrBlank()) {
//            contloAPI.sendPushCallbacks("dismissed", internalID)
            CoroutineScope(Dispatchers.IO).launch {
                ApiService.sendDismissCallback(context, internalID)
            }
        }

    }
}