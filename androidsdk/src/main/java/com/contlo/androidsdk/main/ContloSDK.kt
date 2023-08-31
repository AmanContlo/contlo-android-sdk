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
    private var fcmToken: String? = null
    private var apiKey: String? = null
    private var advertisingId: String? = null
    private var packageName: String? = null
    private var appName: String? = null
    private var appVersion: String? = null
    private var osVersion: String? = null
    private var manufacturer: String? = null
    private var modelName: String? = null
    private var apiLevel: String? = null
    private var networkType: String? = null


    //Main INIT Function
    fun init(applicationContext: Context) {

        context = applicationContext
        sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val contloAPI = ContloAPI(context)

        //Get API KEY
        getAPIKey()

        // Register user if new install
        if (!sharedPreferences.contains("NEW_APP_INSTALL")) {

            Log.d("Contlo-Init","Inside new install")

            //Generate and Register FCM
            generateAndRegisterFCM(onSuccess = {

                Log.d("Contlo-Init", "NEW APP INSTALL")

                editor.putString("NEW_APP_INSTALL", "New Install Registered")
                editor.apply()
                sharedPreferences.getString("NEW_APP_INSTALL",null)?.let { Log.d("Contlo-Init", it) }

                if(sharedPreferences.contains("AD_ID_FCM_NOT_FOUND")){

                    Log.d("Contlo-Init","Sending AD_ID on success")
                    sendAdId(context)
                    editor.remove("AD_ID_FCM_NOT_FOUND")
                    editor.apply()
                }

                if(sharedPreferences.contains("PUSH_CONSENT_FCM_NOT_FOUND")){

                    val contloPermissions = ContloPermissions()
                    val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)
                    Log.d("Contlo-Permissions","Changing consent from onSuccess - $mobilePushConsent")
                    contloPermissions.changeMPConsent(context,mobilePushConsent,fcmToken)
                }

            }, onError = {

                Log.d("Contlo-Init", "Unable to Fetch FCM")

            })

        }

        //Retrieve last ap version
        val oldAppVersion = sharedPreferences.getString("APP_VERSION", null)

        //Retrieve Mandatory Attributes
        retrieveMandatoryParams()

        //Check app update and send event but not on install
        if (!oldAppVersion.isNullOrBlank() && appVersion != oldAppVersion) {

            Log.d("Contlo-Init", "App Updated")
            val prop = JSONObject()
            contloAPI.sendEvent("mobile_app_updated",null,null,prop,null)

        }

        //Store Mandatory Params in Shared Preference
        putParamstoSP()

    }

    fun trackAdId(context: Context, consent: Boolean) {

        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val fcm = sharedPreferences.getString("FCM_TOKEN",null)

        Log.d("Contlo-TrackAdId", "Tracking AD-ID")

        // Retrieve the advertising ID in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                advertisingId = if(consent) {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    adInfo.id
                } else {
                    null
                }

                Log.d("Contlo-TrackAdId", "Fetched AD_ID")
                editor.putString("AD_ID", advertisingId)
                editor.apply()

                if(fcm.isNullOrBlank()){
                    editor.putBoolean("AD_ID_FCM_NOT_FOUND",true)
                    editor.apply()
                }

                else{
                    Log.d("Contlo-Init","Sending AD_ID directly")
                    sendAdId(context)
                }
            }
            catch (e: IOException) {
                // Error retrieving advertising ID
                e.printStackTrace()
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
        customProps.put("advertising_id", advertisingId)

        val params = JSONObject()
        params.put("fcm_token", sharedPreferences.getString("FCM_TOKEN",null))
        params.put("custom_properties",customProps)

        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT", false)
        params.put("mobile_push_consent", mobilePushConsent)

        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-TrackAdId", "Sending AD-ID to Contlo")

            val httpPostRequest = HttpClient()

            if(!sharedPreferences.contains("NEW_APP_INSTALL")){
                delay(1000)
            }

            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-TrackAdId", "Send AD-ID - $response")

        }
    }

    private fun getAPIKey() {
        Log.d("Contlo-Init", "Fetching API-KEY")

        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val metaData = appInfo.metaData
            apiKey = metaData?.getString("contlo_api_key")
        }
        catch (e: PackageManager.NameNotFoundException) {
            return
        }
    }

    private fun retrieveMandatoryParams() {

        val packageManager = context.packageManager
        val applicationInfo = context.applicationInfo
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        packageName = context.packageName
        appName = packageManager.getApplicationLabel(applicationInfo).toString()
        appVersion = packageManager.getPackageInfo(packageName.toString(), 0).versionName
        osVersion = Build.VERSION.RELEASE
        apiLevel = Build.VERSION.SDK_INT.toString()
        modelName = Build.MODEL
        manufacturer = Build.MANUFACTURER

        networkType =
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                "WiFi"
            } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                "Mobile data"
            } else {
                "Unknown"
            }
    }

    private fun putParamstoSP() {

        editor.putString("API_KEY", apiKey)
        editor.putString("PACKAGE_NAME", packageName)
        editor.putString("APP_NAME", appName)
        editor.putString("APP_VERSION", appVersion)
        editor.putString("OS_VERSION", osVersion)
        editor.putString("MANUFACTURER", manufacturer)
        editor.putString("MODEL_NAME", modelName)
        editor.putString("API_LEVEL", apiLevel)
        editor.putString("OS_TYPE", "ANDROID")
        editor.putString("SOURCE", "ANDROID SDK")
        editor.putString("SDK_VERSION", "1.0.0")
        editor.putString("NETWORK_TYPE", networkType)
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
            fcmToken = task.result

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { editor.putBoolean("MOBILE_PUSH_CONSENT",true).apply() }

            //Store FCM in Shared Preference
            sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
            editor.putString("FCM_TOKEN", fcmToken).apply()


            // Sending Mobile App Installed Event -> Makes an Anonymous profile
            CoroutineScope(Dispatchers.IO).launch {

                Log.d("Contlo-Init", "Sending Install Event")

                val contloAPI = ContloAPI(context)
                val prop = JSONObject()
                val profileProperties = JSONObject()
                profileProperties.put("source","ANDROID SDK")
                contloAPI.sendEvent("mobile_app_installed",null,null,prop,profileProperties)

                onSuccess()
            }
        }
    }
}