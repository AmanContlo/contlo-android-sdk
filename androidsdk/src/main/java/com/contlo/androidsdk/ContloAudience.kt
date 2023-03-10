package com.contlo.androidsdk

import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import android.widget.Toast
import org.json.JSONObject
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.contlo.androidsdk.api.HttpClient
import java.util.*

class ContloAudience(private val context: Context) {

    init {
        getAPIKey()
    }

    private var apiKey: String? = null

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


        fun initializeUser(context: Context) {

            val handler = Handler(Looper.getMainLooper())

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                println("Value of Token = $token")

                //Toast
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

                    val jsonResponse = JSONObject(response)
                    contloExternalID = jsonResponse.getString("external_id")

                    println(response)
                    handler.post {
                        Toast.makeText(context, "Response: $response", Toast.LENGTH_SHORT).show()
                    }

                }.start()


            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val userRefId: String = generateRandomId()
            sharedPreferences.edit().putString(USER_ID_KEY, contloExternalID).apply()
            sharedPreferences.edit().putString(REF_ID_KEY, userRefId).apply()

            })

        }


    fun sendUserDatatoContlo(){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            println("Value of Token = $token")
            Toast.makeText(context, "Value of Token = $token", Toast.LENGTH_SHORT).show()

            val url = "https://api.contlo.com/v1/identify"

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
            params.put("fcm_token", token)

            println(params.toString())


            Thread {

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendRequest(url, headers, params, "POST")

                println(response)
                handler.post {
                    Toast.makeText(context, "CA Response: $response", Toast.LENGTH_SHORT).show()
                }

            }.start()

        })

    }



        //Generate 8 digit random ID
        fun generateRandomId(): String {
            val random = Random()
            val sb = StringBuilder(8)
            for (i in 0 until 8) {
                sb.append(random.nextInt(10))
            }
            return sb.toString()
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

    companion object {
        private const val TAG = "FCMToken - SDK Side"
    }


}
