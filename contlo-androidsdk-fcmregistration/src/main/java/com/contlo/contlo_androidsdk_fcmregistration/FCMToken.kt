package com.contlo.contlo_androidsdk_fcmregistration

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import android.widget.Toast
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response


class FCMToken(private val context: Context) {

    private val queue = Volley.newRequestQueue(context)
    
    fun getFCMRegistrationToken(listener: OnCompleteListener<String>) {

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


            val request = object : JsonObjectRequest(
                Request.Method.POST,
                "https://api.contlo.com/v1/register_mobile_push",
                params,
                Response.Listener { response ->
                    // Handle successful response.
                    Log.d("TAG", "Response body: $response")
                    Toast.makeText(context, "FCM Registration Successful, Response: $response", Toast.LENGTH_SHORT).show()


                },
                Response.ErrorListener { error ->
                    // Handle error.
                    Log.e("TAG", "Error code: ${error.message}")
                    Toast.makeText(context, "FCM Registration Failed", Toast.LENGTH_SHORT).show()

                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["accept"] = "application/json"
                    headers["X-API-KEY"] = "7338bff309ed018db33167470bfe8e13"
                    headers["content-type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        })
    }

    companion object {
        private const val TAG = "FCMToken - SDK Side"
    }
}
