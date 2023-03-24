package com.contlo.androidsdk.api

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.contlo.androidsdk.ContloSDK
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TrackAPI() {


    //API Key
    private var apiKey: String? = null


    fun sendEvent(event: String, email: String, phone: String ) {

        val handler = Handler(Looper.getMainLooper())

        val contloSDK = ContloSDK()
        apiKey = contloSDK.API_KEY


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            println("Value of Token = $token")


            val url = "https://api.contlo.com/v1/track"

            val headers = HashMap<String, String>()
            headers["accept"] = "application/json"
            headers["X-API-KEY"] = "$apiKey"
            headers["content-type"] = "application/json"


            val propString = "{\"key1111111\":\"value1111111\",\"key2\":\"value2\"}"
            val propString1 = "{\"key000000\":\"value000000\",\"key2\":\"value2\"}"

            val prop = JSONObject(propString)
            val prop1 = JSONObject(propString1)


            val params = JSONObject()
            params.put("event", event)
            params.put("email", email)
            params.put("properties", prop)
            params.put("profile_properties",prop1)
            params.put("phone_number", phone)
            params.put("fcm_token", token)


            println(params.toString())

            CoroutineScope(Dispatchers.IO).launch {

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                println(response)


            }


            })


    }

}