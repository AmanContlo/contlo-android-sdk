package com.contlo.androidsdk.main

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.contlo.androidsdk.api.ContloAPI
import com.contlo.androidsdk.api.HttpClient
import com.contlo.androidsdk.permissions.ContloPermissions
import com.contlo.contlosdk.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.*
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class ContloSDK {

    //Context
    private lateinit var context: Context

    //Shared Preference
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var editor: Editor

    //Mandatory Attributes
    private var FCM_TOKEN: String? = null
    private var API_KEY: String? = null
    private var AD_ID: String? = null
    private var PACKAGE_NAME: String? = null
    private var APP_NAME: String? = null
    private var APP_VERSION: String? = null
    private var OS_VERSION: String? = null
    private var MANUFACTURER: String? = null
    private var MODEL_NAME: String? = null
    private var API_LEVEL: String? = null
    private var ANDROID_SDK_VERSION: String? = null
    private var NETWORK_TYPE: String? = null

    //Main INIT Function
    fun init(context1: Context) {

        context = context1
        Log.d("Contlo-Init", "Triggered")

        sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val contloAPI = ContloAPI(context)

        //Get API KEY
        getAPIKey()

        // Register user if new install
        if (!sharedPreferences.contains("NEW_APP_INSTALL")) {

            //Generate and Register FCM
            generateAndRegisterFCM(onSuccess = {

                Log.d("Contlo-Init", "NEW APP INSTALL")

                if(sharedPreferences.contains("AD_ID_FCM_NOT_FOUND")){

                    Log.d("Contlo-Init","Sending AD_ID on success")
                    sendAdId(context)
                }

                if(sharedPreferences.contains("PUSH_CONSENT_FCM_NOT_FOUND")){

                    val contloPermissions = ContloPermissions()
                    val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)
                    Log.d("Contlo-Permissions","Changing consent from onSuccess111 - $mobilePushConsent")
                    contloPermissions.changeMPConsent(context,mobilePushConsent,FCM_TOKEN,0)

                }

                Log.d("Contlo-Init","Putting new install flag")
                editor.putString("NEW_APP_INSTALL", "1")
                editor.apply()


            }, onError = {

                Log.d("Contlo-Init", "Unable to Fetch FCM")

            })

        }

        //Check App Update
        val oldAppVersion = sharedPreferences.getString("APP_VERSION", null)

        //Retrieve Mandatory Attributes
        retrieveMandatoryParams()

        //Check app update and send event but not on install
        if (APP_VERSION != oldAppVersion && (sharedPreferences.contains("NEW_APP_INSTALL"))) {

            Log.d("Contlo-Init", "App Updated")
            val prop = JSONObject()
            contloAPI.sendEvent("mobile_app_updated",prop,null)

        }

        //Store Mandatory Params in Shared Preference
        putParamstoSP()

    }

    fun trackAdId(context: Context, consent: Boolean) {

        if (consent) {
            val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val fcm = sharedPreferences.getString("FCM_TOKEN",null)

            Log.d("Contlo-TrackAdId", "Tracking AD-ID")

            // Retrieve the advertising ID in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    AD_ID = adInfo.id
                    if (AD_ID != null) {
                        Log.d("Contlo-TrackAdId", "Fetched AD_ID")
                        editor.putString("AD_ID", AD_ID)
                        editor.apply()
                        if (fcm==null){
                            editor.putBoolean("AD_ID_FCM_NOT_FOUND",true)
                            editor.apply()
                        }
                        else {
                            Log.d("Contlo-Init","Sending AD_ID directly")
                            sendAdId(context)
                        }
                    }
                } catch (e: IOException) {
                    // Error retrieving advertising ID
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendAdId(context: Context){

        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("API_KEY",null)

        val url = context.getString(R.string.identify_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val customProps = JSONObject()
        customProps.put("Advertising ID", AD_ID)

        val params = JSONObject()
        params.put("fcm_token", sharedPreferences.getString("FCM_TOKEN",null))
        params.put("custom_properties",customProps)

        val mobilePushConsent =
            sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT", false)

        if (mobilePushConsent)
            params.put("mobile_push_consent", "TRUE")
        else
            params.put("mobile_push_consent", "FALSE")

        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-TrackAdId", "Sending AD-ID to Contlo")

            val httpPostRequest = HttpClient()
            val response =
                httpPostRequest.sendPOSTRequest(url, headers, params)

            if(!sharedPreferences.contains("NEW_APP_INSTALL")){
                delay(1000)
            }

            Log.d("Contlo-TrackAdId", "Send AD-ID - $response")

        }
    }

    private fun getAPIKey() {

        Log.d("Contlo-Init", "Fetching API-KEY")

        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val metaData = appInfo.metaData
            API_KEY = metaData?.getString("contlo_api_key")
        } catch (e: PackageManager.NameNotFoundException) {
            return
        }

    }

    private fun retrieveMandatoryParams() {

        PACKAGE_NAME = context.packageName //1
        val packageManager = context.packageManager
        val applicationInfo = context.applicationInfo
        APP_NAME = packageManager.getApplicationLabel(applicationInfo).toString() //2
        APP_VERSION = packageManager.getPackageInfo(PACKAGE_NAME.toString(), 0).versionName //3
        OS_VERSION = Build.VERSION.RELEASE //4
        API_LEVEL = Build.VERSION.SDK_INT.toString() //5
        MODEL_NAME = Build.MODEL //6
        MANUFACTURER = Build.MANUFACTURER //7
        ANDROID_SDK_VERSION = Build.VERSION.SDK //8
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        NETWORK_TYPE = //9
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                "WiFi"
            } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                "Mobile data"
            } else {
                "Unknown"
            }
    }

    private fun putParamstoSP() {

        editor.putString("API_KEY", API_KEY)
        editor.putString("PACKAGE_NAME", PACKAGE_NAME)
        editor.putString("APP_NAME", APP_NAME)
        editor.putString("APP_VERSION", APP_VERSION)
        editor.putString("OS_VERSION", OS_VERSION)
        editor.putString("MANUFACTURER", MANUFACTURER)
        editor.putString("MODEL_NAME", MODEL_NAME)
        editor.putString("API_LEVEL", API_LEVEL)
        editor.putString("ANDROID_SDK_VERSION", ANDROID_SDK_VERSION)
        editor.putString("NETWORK_TYPE", NETWORK_TYPE)
        editor.apply()

    }

    private fun generateAndRegisterFCM(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
        retryCount: Int = 0
    ) {

        Log.d("Contlo-Init", "Generating FCM")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

            // Retry Mechanism
            if (!task.isSuccessful) {

                Log.d("Contlo-Init", "FCM Task Unsuccessful")

                if (retryCount < 2) {

                    // Retry FCM registration up to 2 times
                    Log.d("Contlo-Init", "Retrying to generate FCM")

                    Handler(Looper.getMainLooper()).postDelayed({
                        generateAndRegisterFCM(onSuccess, onError, retryCount + 1)
                    }, 5000)
                } else {
                    onError(task.exception ?: Exception("Unknown error"))
                }
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            FCM_TOKEN = task.result

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU){

                editor.putBoolean("MOBILE_PUSH_CONSENT",true)
                editor.apply()

            }

            //Store FCM in Shared Preference
            sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
            editor.putString("FCM_TOKEN", FCM_TOKEN)
            editor.apply()

            Log.d("Contlo-fcm",FCM_TOKEN.toString())

            // Sending Mobile App Installed Event -> Makes an Anonymous profile
            CoroutineScope(Dispatchers.IO).launch {

                Log.d("Contlo-Init", "Sending Install Event")

                val contloAPI = ContloAPI(context)
                val prop = JSONObject()
                val profileProperties = JSONObject()
                profileProperties.put("source","Android SDK v0.0.1")
                val response = contloAPI.sendEvent("mobile_app_installed",prop,profileProperties)
                val jsonObject = response?.let { JSONObject(it) }

                val externalId: String?
                if (jsonObject != null) {

                    // Store External Id
                    if (jsonObject.has("external_id")) {

                        externalId = jsonObject.getString("external_id")
                        editor.putString("Contlo External ID", externalId)
                        editor.apply()
                        onSuccess()

                    }
                }
            }
        }
    }
}