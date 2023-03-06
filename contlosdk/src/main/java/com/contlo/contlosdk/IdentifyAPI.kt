package com.contlo.contlosdk

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject



class IdentifyAPI(private val context: Context) {

    init {
        getAPIKey()
    }


    //API Key
    private var apiKey: String? = null


    fun sendRequest(firstName: String, lastName: String, email: String, phone: String, city: String, country: String, zip: String, cKey: String, cValue: String) {

        val handler = Handler(Looper.getMainLooper())

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            println("Value of Token = $token")
            Toast.makeText(context, "Value of Token = $token", Toast.LENGTH_SHORT).show()

            val propString1 = "{\"$cKey\":\"$cValue\"}"
            val prop1 = JSONObject(propString1)

            val url = "https://api.contlo.com/v1/identify"

            val headers = HashMap<String, String>()
            headers["accept"] = "application/json"
            headers["X-API-KEY"] = "$apiKey"
            headers["content-type"] = "application/json"

            val params = JSONObject()
            params.put("first_name", firstName)
            params.put("last_name", lastName)
            params.put("email", email)
            params.put("phone_number", phone)
            params.put("city", city)
            params.put("country", country)
            params.put("zip", zip)
            params.put("custom_properties", prop1)
            params.put("fcm_token", token)

            println(params.toString())


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

//
//    fun setFirstName(fname: String){
//
//        val params = JSONObject()
//        params.put("first_name", fname)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setLastName(lname: String){
//
//        val params = JSONObject()
//        params.put("last_name", lname)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setCity(city: String){
//
//        val params = JSONObject()
//        params.put("city", city)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setCountry(country: String){
//
//        val params = JSONObject()
//        params.put("country", country)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setZip(zip: String){
//
//        val params = JSONObject()
//        params.put("zip", zip)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setPhoneNumber(phone: String){
//
//        val params = JSONObject()
//        params.put("phone_number", phone)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setGender(gender: String){
//
//        val propString1 = "{\"Gender\":\"$gender\"}"
//        val prop1 = JSONObject(propString1)
//
//        val params = JSONObject()
//        params.put("custom_properties",prop1)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }
//
//    fun setUserAttribute(key: String, value: String){
//
//        val propString1 = "{\"$key\":\"$value\"}"
//        val prop1 = JSONObject(propString1)
//
//        val params = JSONObject()
//        params.put("custom_properties",prop1)
//        val request = object : JsonObjectRequest(
//            Request.Method.POST,
//            "https://api.contlo.com/v1/identify",
//            params,
//            Response.Listener { response ->
//                // Handle successful response.
//                Log.d("TAG", "Response body: $response")
//                Toast.makeText(context, "Response body: $response", Toast.LENGTH_SHORT ).show()
//
//            },
//            Response.ErrorListener { error ->
//                // Handle error.
//                Log.e("TAG", "Error code: ${error.message}")
//                Toast.makeText(context, "Error code: ${error.message}",  Toast.LENGTH_SHORT ).show()
//
//            }
//        ) {
//            //Adding headers to the request
//            override fun getHeaders(): MutableMap<String, String> {
//                val headers = HashMap<String, String>()
//                headers["accept"] = "application/json"
//                headers["X-API-KEY"] = "$apiKey"
//                headers["content-type"] = "application/json"
//                return headers
//            }
//        }
//
//    }

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
