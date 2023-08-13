package com.contlo.androidsdk.push

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.contlo.androidsdk.api.ContloAPI

class NotificationClickHandler : Service() {


    private var internalID: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("Contlo-Notification", "Push Clicked")

        internalID = intent?.getStringExtra("internal_id")
        val contloAPI = ContloAPI(applicationContext)
        internalID?.let { contloAPI.sendPushCallbacks("clicked", it) }

        val deepLink = intent?.getStringExtra("deeplink")
        val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent1)

        // Stop the service
        stopSelf()

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }





}