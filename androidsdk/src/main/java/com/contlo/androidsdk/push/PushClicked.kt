package com.contlo.androidsdk.push

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.ContloSDK
import com.contlo.androidsdk.api.HttpClient
import com.contlo.androidsdk.api.TrackAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PushClicked : Service() {

    private var apiKey: String? = null

    private var internalID: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val app_name = sharedPreferences.getString("APP_NAME", null)
        val api_level = sharedPreferences.getString("API_LEVEL", null)
        val os_version = sharedPreferences.getString("OS_VERSION", null)

        val x = TrackAPI()
        x.sendMobileEvents(this,"mobile_push_notification_clicked",app_name, api_level, os_version)

        val contloSDK  = ContloSDK()
        apiKey = contloSDK.API_KEY

        internalID = intent?.getStringExtra("internal_id")

        // Make the API call here
        val url = "https://staging2.contlo.in/v1/event/mobile_push_click"

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("internal_id",internalID)
        Log.d("pushclick",params.toString())
        Toast.makeText(this, "Params: $params", Toast.LENGTH_SHORT).show()


        CoroutineScope(Dispatchers.IO).launch {

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            println(" Push Clicked Response: $response")

        }

        // Stop the service
        stopSelf()

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }





}