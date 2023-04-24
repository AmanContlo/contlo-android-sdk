package com.contlo.androidsdk

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
import com.contlo.contlosdk.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.*
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        Log.d("Contlo-Init", "Triggered")

        //context
        context = context1
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val contloAPI = ContloAPI(context)

        //Get API KEY
        getAPIKey()

        if (!sharedPreferences.contains("NEW_APP_INSTALL")) {

            //Generate and Register FCM
            generateAndRegisterFCM(onSuccess = {

                Log.d("Contlo-Init", "NEW APP INSTALL")

                val prop = JSONObject()
                contloAPI.sendEvent("mobile_app_installed",prop)

                val editor = sharedPreferences.edit()
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


        //Check app update and fire event but not on install
        if (APP_VERSION != oldAppVersion && (sharedPreferences.contains("NEW_APP_INSTALL"))) {

            Log.d("Contlo-Init", "App Updated")

            val prop = JSONObject()
            contloAPI.sendEvent("mobile_app_installed",prop)

        }


        //Store Mandatory Params in Shared Preference
        putParamstoSP()


    }

    fun trackAdId(context: Context, consent: Boolean) {

        if (consent) {

            Log.d("Contlo-TrackAdId", "Tracking AD-ID")

        } else {

            Log.d("Contlo-TrackAdId", "Not Tracking AD-ID")

        }

        // Retrieve the advertising ID in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                var advertisingId = adInfo.id
                if (advertisingId != null) {
                    Log.d("Contlo-TrackAdId", "Fetched AD_ID")

                    if (!consent) {

                        advertisingId = null

                    }

                    val sharedPreferences =
                        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


                    editor = sharedPreferences.edit()
                    editor.putString("AD_ID", advertisingId)
                    editor.apply()

                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(
                                "Contlo-TrackAdId",
                                "Fetching FCM registration token failed",
                                task.exception
                            )
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        val url = "https://staging2.contlo.in/v1/identify"

                        val headers = HashMap<String, String>()
                        headers["accept"] = "application/json"
                        headers["X-API-KEY"] = "$API_KEY"
                        headers["content-type"] = "application/json"

                        val params = JSONObject()
                        params.put("fcm_token", token)
                        params.put("ad_id", advertisingId)

                        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

                        if(mobilePushConsent)
                            params.put("mobile_push_consent", "TRUE")
                        else
                            params.put("mobile_push_consent", "FALSE")


                        CoroutineScope(Dispatchers.IO).launch {

                            Log.d("Contlo-TrackAdId", "Sending AD-ID to Contlo")

                            val httpPostRequest = HttpClient()
                            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                            Log.d("Contlo-TrackAdId", "Send AD-ID - $response")

                        }

                    })


                }
            } catch (e: IOException) {
                // Error retrieving advertising ID
                e.printStackTrace()
            }
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
            // Handle the exception
        }

    }


    private fun retrieveMandatoryParams() {

//        Log.d("Contlo-Init", "Retrieving Details")

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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

//        Log.d("Contlo-Init", "Saving Details")

        editor = sharedPreferences.edit()
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
//            Log.d("FCM", FCM_TOKEN.toString())

            //Put FCM in params
            val params = JSONObject()
            params.put("fcm_token", FCM_TOKEN)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                params.put("mobile_push_consent","FALSE")

            else
                params.put("mobile_push_consent","TRUE")



            //Store FCM in Shared Preference
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            editor.putString("FCM_TOKEN", FCM_TOKEN)
            editor.apply()

            //Make API Request
            val url = "https://staging2.contlo.in/v1/register_mobile_push"

            val headers = HashMap<String, String>()
            headers["accept"] = "application/json"
            headers["X-API-KEY"] = "$API_KEY"
            headers["content-type"] = "application/json"

            CoroutineScope(Dispatchers.IO).launch {

                Log.d("Contlo-Init", "Registering FCM with Contlo")

                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                val jsonObject = JSONObject(response)
                var externalId: String? = null
                if (jsonObject.has("external_id")) {
                    externalId = jsonObject.getString("external_id")
                }

                editor = sharedPreferences.edit()
                editor.putString("Contlo External ID", externalId)
                editor.apply()

                Log.d("Contlo-Init", "Response FCM Generation: $response")
                onSuccess()
            }
        }


    }


}