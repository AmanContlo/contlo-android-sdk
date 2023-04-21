package com.contlo.androidsdk.push

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.api.ContloAPI
import com.contlo.androidsdk.api.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PushClicked : Service() {

    private var apiKey: String? = null

    private var internalID: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("Contlo-Notification", "Push Clicked")

        internalID = intent?.getStringExtra("internal_id")

        val x = ContloAPI(applicationContext)
        internalID?.let { x.sendPushCallbacks("clicked", it) }


        // Stop the service
        stopSelf()

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }





}