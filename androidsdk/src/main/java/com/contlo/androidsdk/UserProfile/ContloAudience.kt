package com.contlo.androidsdk.UserProfile


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.contlo.androidsdk.ContloSDK
import com.contlo.androidsdk.api.HttpClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ContloAudience(val context: Context ) {

//    lateinit var context: Context


    var apiKey: String? = null

    private val PREF_NAME = "contloAudiencePref"
    private val USER_ID_KEY = "contloUserId"
    private val REF_ID_KEY = "contloRefId"
    private var contloExternalID = ""
    private var c: Int = 1
    private lateinit var c1: String

    //User Attributes
    private var USER_FIRST_NAME: String? = null
    private var USER_LAST_NAME: String? = null
    private var USER_CITY: String? = null
    private var USER_COUNTRY: String? = null
    private var USER_ZIP: String? = null
    private var USER_EMAIL: String? = null
    private var USER_PHONE: String? = null
    private var CUSTOM_PROPERTIES: JSONObject? = null

    val handler = Handler(Looper.getMainLooper())



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



    fun printparams(){

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val FCM_TOKEN = sharedPreferences.getString("FCM_TOKEN", null)
        val API_KEY = sharedPreferences.getString("API_KEY", null)
        val AD_ID = sharedPreferences.getString("AD_ID", null)
        val PACKAGE_NAME = sharedPreferences.getString("PACKAGE_NAME", null)
        val APP_NAME = sharedPreferences.getString("APP_NAME", null)
        val APP_VERSION = sharedPreferences.getString("APP_VERSION", null)
        val OS_VERSION = sharedPreferences.getString("OS_VERSION", null)
        val MANUFACTURER = sharedPreferences.getString("MANUFACTURER", null)
        val MODEL_NAME = sharedPreferences.getString("MODEL_NAME", null)
        val API_LEVEL = sharedPreferences.getString("API_LEVEL", null)
        val ANDROID_SDK_VERSION = sharedPreferences.getString("ANDROID_SDK_VERSION", null)
        val TIMEZONE = sharedPreferences.getString("TIMEZONE", null)
        val LATITUDE = sharedPreferences.getString("LATITUDE", null)
        val LONGITUDE = sharedPreferences.getString("LONGITUDE", null)
        val CARRIER_INFO = sharedPreferences.getString("CARRIER_INFO", null)
        val NETWORK_TYPE = sharedPreferences.getString("NETWORK_TYPE", null)
        val EXTERNAL_ID = sharedPreferences.getString("Contlo External ID", null)

        FCM_TOKEN?.let { Log.d("*** Mandatory Attributes 1", it) }
        API_KEY?.let { Log.d("Mandatory Attributes 2", it) }
        AD_ID?.let { Log.d("Mandatory Attributes 3", it) }
        PACKAGE_NAME?.let { Log.d("Mandatory Attributes 4", it) }
        APP_NAME?.let { Log.d("Mandatory Attributes 5", it) }
        APP_VERSION?.let { Log.d("Mandatory Attributes 6", it) }
        OS_VERSION?.let { Log.d("Mandatory Attributes 7", it) }
        MANUFACTURER?.let { Log.d("Mandatory Attributes 8", it) }
        MODEL_NAME?.let { Log.d("Mandatory Attributes 9", it) }
        API_LEVEL?.let { Log.d("Mandatory Attributes 10", it) }
        ANDROID_SDK_VERSION?.let { Log.d("Mandatory Attributes 11", it) }
        TIMEZONE?.let { Log.d("Mandatory Attributes 12", it) }
        LATITUDE?.let { Log.d("Mandatory Attributes 13", it) }
        LONGITUDE?.let { Log.d("Mandatory Attributes 14", it) }
        CARRIER_INFO?.let { Log.d("Mandatory Attributes 15", it) }
        NETWORK_TYPE?.let { Log.d("Mandatory Attributes 16", it) }
        EXTERNAL_ID?.let { Log.d("Mandatory Attributes 17", it) }
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

            println(params.toString())


            CoroutineScope(Dispatchers.IO).launch {

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                println(" Send user data to contlo: $response")

            }



    }





    companion object {
        private const val TAG = "FCMToken - SDK Side"
    }


}
