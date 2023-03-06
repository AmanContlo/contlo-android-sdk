package com.contlo.contlosdk

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject


class TrackAPI(private val context: Context) {

    private val queue = Volley.newRequestQueue(context)
    private var apiKey: String? = null

    fun sendRequest(event: String, email: String, phone: String ) {

        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val metaData = appInfo.metaData
            apiKey = metaData?.getString("my_sdk_api_key")
        }

        catch (e: PackageManager.NameNotFoundException) {
            // Handle the exception
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            println("Value of Token = $token")
            Toast.makeText(context,"Value of Token = $token",Toast.LENGTH_SHORT).show()


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


        val request = object : JsonObjectRequest(
            Method.POST,
            "https://api.contlo.com/v1/track",
            params,
            Response.Listener { response ->
                // Handle successful response.
                Log.d("TAG", "Response body: $response")
                Toast.makeText(context,"Response body: $response",Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener { error ->
                // Handle error.
                Log.e("TAG", "Error code: ${error.message}")
                Toast.makeText(context,"Error code: ${error.message}",Toast.LENGTH_SHORT).show()

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["accept"] = "application/json"
                headers["X-API-KEY"] = "$apiKey"
                headers["content-type"] = "application/json"
                return headers
            }
        }

        queue.add(request)

        })


    }

}