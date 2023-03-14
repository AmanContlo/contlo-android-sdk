package com.contlo.androidsdk.push

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.api.HttpClient
import org.json.JSONObject

class PushClicked : Service() {

//    init {
//        getAPIKey()
//    }

    //API Key
    private var apiKey: String? = "7338bff309ed018db33167470bfe8e13"

    private var internalID: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val handler = Handler(Looper.getMainLooper())

        internalID = intent?.getStringExtra("internal_id")

        // Make the API call here
        val url = "https://api.contlo.com/v1/event/mobile_push_click"

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("internal_id",internalID)
        Log.d("pushclick",params.toString())
        Toast.makeText(this, "Params: $params", Toast.LENGTH_SHORT).show()

        Thread {

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendRequest(url, headers, params, "POST")

            println(response)
            handler.post {
                Toast.makeText(this, "Mobile Click Registered?: $response", Toast.LENGTH_SHORT).show()
            }

        }.start()

        // Stop the service
        stopSelf()

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


//    fun getAPIKey(){
//
//        try {
//            val appInfo = applicationContext.packageManager.getApplicationInfo(
//                applicationContext.packageName, PackageManager.GET_META_DATA
//            )
//            val metaData = appInfo.metaData
//            apiKey = metaData?.getString("contlo_api_key")
//        } catch (e: PackageManager.NameNotFoundException) {
//            // Handle the exception
//        }
//
//    }


}