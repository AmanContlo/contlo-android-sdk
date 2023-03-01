package com.contlo.contlosdk

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response


class IdentifyAPI(private val context: Context) {

    //New Volley Request queue
    private val queue = Volley.newRequestQueue(context)

    fun sendRequest(firstName: String, lastName: String, email: String, phone: String, city: String, country: String, zip: String) {

        //Putting data in an JSONObject
        val params = JSONObject()
        params.put("first_name", firstName)
        params.put("last_name", lastName)
        params.put("email", email)
        params.put("phone_number", phone)
        params.put("city", city)
        params.put("country", country)
        params.put("zip", zip)

        println(params.toString())

        //Creating the Request with success and error listeners
        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "https://api.contlo.com/v1/identify",
            params,
            Response.Listener { response ->
                // Handle successful response.
                Log.d("TAG", "Response body: $response")

            },
            Response.ErrorListener { error ->
                // Handle error.
                Log.e("TAG", "Error code: ${error.message}")

            }
        ) {
            //Adding headers to the request
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["accept"] = "application/json"
                headers["X-API-KEY"] = "7338bff309ed018db33167470bfe8e13"
                headers["content-type"] = "application/json"
                return headers
            }
        }


        queue.add(request)



    }

}
