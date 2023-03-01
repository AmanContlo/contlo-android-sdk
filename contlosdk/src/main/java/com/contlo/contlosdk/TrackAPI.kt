package com.contlo.contlosdk

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response


class TrackAPI(private val context: Context) {

    private val queue = Volley.newRequestQueue(context)

    fun sendRequest(event: String, email: String, phone: String ): Int {

        var flag=0

        val params = JSONObject()
        params.put("event", event)
        params.put("email", email)
        params.put("phone_number", phone)


        println(params.toString())


        val request = object : JsonObjectRequest(
            Method.POST,
            "https://api.contlo.com/v1/track",
            params,
            Response.Listener { response ->
                // Handle successful response.
                Log.d("TAG", "Response body: $response")
                flag = 1
            },
            Response.ErrorListener { error ->
                // Handle error.
                Log.e("TAG", "Error code: ${error.message}")
                flag = -1
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

        return flag


    }

}