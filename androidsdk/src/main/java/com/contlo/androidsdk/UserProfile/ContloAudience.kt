package com.contlo.androidsdk.UserProfile


import android.content.Context
import android.util.Log
import com.contlo.androidsdk.api.HttpClient
import org.json.JSONObject
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ContloAudience(val context: Context ) {


    private var apiKey: String? = null

    //User Attributes
    private var USER_FIRST_NAME: String? = null
    private var USER_LAST_NAME: String? = null
    private var USER_CITY: String? = null
    private var USER_COUNTRY: String? = null
    private var USER_ZIP: String? = null
    private var USER_EMAIL: String? = null
    private var USER_PHONE: String? = null
    private var CUSTOM_PROPERTIES: JSONObject? = null



    fun setUserFirstName(fname: String?){

        USER_FIRST_NAME = fname

    }

    fun setUserLastName(lname: String?){

        USER_LAST_NAME = lname

    }

    fun setUserCity(city: String?){

        USER_CITY = city

    }

    fun setUserCountry(country: String?){

        USER_COUNTRY = country

    }

    fun setUserZip(zip: String?){

        USER_ZIP = zip

    }

    fun setUserEmail(email: String?){

        USER_EMAIL = email

    }

    fun setUserPhone(phone: String?){

        USER_PHONE = phone

    }

    fun setUserAttribute(key: String?, value: String?){

        val propString1 = "{\"$key\":\"$value\"}"
        CUSTOM_PROPERTIES = JSONObject(propString1)

    }


    fun sendUserDatatoContlo(){

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val fcm = sharedPreferences.getString("FCM_TOKEN", null)
        apiKey = sharedPreferences.getString("API_KEY", null)


            val url = "https://staging2.contlo.in/v1/identify"

            val headers = HashMap<String, String>()
            headers["accept"] = "application/json"
            headers["X-API-KEY"] = "$apiKey"
            headers["content-type"] = "application/json"

            val params = JSONObject()
            params.put("first_name", USER_FIRST_NAME)
            params.put("last_name", USER_LAST_NAME)
            params.put("email", USER_EMAIL)
            params.put("phone_number", USER_PHONE)
            params.put("city", USER_CITY)
            params.put("country", USER_COUNTRY)
            params.put("zip", USER_ZIP)
            params.put("custom_properties", CUSTOM_PROPERTIES)
            params.put("fcm_token", fcm)


        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

        if(mobilePushConsent)
            params.put("mobile_push_consent", "TRUE")
        else
            params.put("mobile_push_consent", "FALSE")


        Log.d("Contlo-Audience", "Send User Data Params: $params")


            CoroutineScope(Dispatchers.IO).launch {

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                Log.d("Contlo-Audience", "Send User Data Response: $response")

            }



    }

}
