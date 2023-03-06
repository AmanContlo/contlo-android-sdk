package com.contlo.fcmregistration

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.contlo.contlosdk.HttpClient


class FCMToken(private val context: Context) {

    init {
        getAPIKey()
    }

    private var apiKey: String? = null
    
    fun getFCMRegistrationToken(listener: OnCompleteListener<String>) {

        val handler = Handler(Looper.getMainLooper())

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            println("Value of Token = $token")

            // Log and toast
            Log.d(TAG, "Your FCM Token: $token")
            Toast.makeText(context, "Your FCM Token: $token", Toast.LENGTH_SHORT).show()

            val params = JSONObject()
            params.put("fcm_token", token)

            println(params.toString())


            val url = "https://api.contlo.com/v1/register_mobile_push"

            val headers = HashMap<String, String>()
            headers["accept"] = "application/json"
            headers["X-API-KEY"] = "$apiKey"
            headers["content-type"] = "application/json"


            Thread {

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendRequest(url, headers, params, "POST")

                println(response)
                handler.post {
                    Toast.makeText(context, "Response: $response", Toast.LENGTH_SHORT).show()
                }

            }.start()
        })
    }

    companion object {
        private const val TAG = "FCMToken - SDK Side"
    }

    fun getAPIKey(){

        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val metaData = appInfo.metaData
            apiKey = metaData?.getString("contlo_api_key")
        } catch (e: PackageManager.NameNotFoundException) {
            // Handle the exception
        }

    }
}
