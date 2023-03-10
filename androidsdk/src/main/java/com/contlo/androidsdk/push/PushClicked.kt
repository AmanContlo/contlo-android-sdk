package com.contlo.androidsdk.push

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.contlo.androidsdk.api.HttpClient
import org.json.JSONObject

class PushClicked : Service() {

    //API Key
    private var apiKey: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        apiKey = intent?.getStringExtra("apikey")

        // Make the API call here
        val url = "https://api.contlo.com/v1/event/mobile_push_click"

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("internal_id","")

        Thread {

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendRequest(url, headers, params, "POST")

            println(response)

        }.start()

        // Stop the service
        stopSelf()

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}