package com.contlo.androidsdk.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.contlo.androidsdk.api.ContloApiService
import com.contlo.androidsdk.utils.ContloUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        ContloUtils.printLog(context, TAG, "Push dismissed")

        val internalID = intent?.getStringExtra("internal_id")
        if (!internalID.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                ContloApiService.sendDismissCallback(context, internalID)
            }
        }
    }
    companion object {
        const val TAG = "NotificationDismissReceiver"
    }
}