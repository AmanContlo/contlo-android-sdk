package com.contlo.androidsdk

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.contlo.androidsdk.api.HttpClient
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
    lateinit var context: Context

    //Handler for toast
    val handler = Handler(Looper.getMainLooper())

    //Shared Preference
    lateinit var sharedPreferences: SharedPreferences

    //Mandatory Attributes
    var FCM_TOKEN: String? = null
    var API_KEY: String? = null
    var AD_ID: String? = null
    var PACKAGE_NAME: String? = null
    var APP_NAME: String? = null
    var APP_VERSION: String? = null
    var OS_VERSION: String? = null
    var MANUFACTURER: String? = null
    var MODEL_NAME: String? = null
    var API_LEVEL: String? = null
    var ANDROID_SDK_VERSION: String? = null
    var TIMEZONE: String? = null
    var LATITUDE: String? = null
    var LONGITUDE: String? = null
    var CARRIER_INFO: String? = null
    var NETWORK_TYPE: String? = null

    //Main INIT Function
    fun init(context1: Context, fcm: String?, api_key: String?) {

        //context
        context = context1

        //Fetch API-KEY
        if (api_key == null) {

            getAPIKey()

        } else {
            API_KEY = api_key
        }

        //Initialize Shared Preference
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        //Send App Installed Event
        if(!sharedPreferences.contains("APP_INSTALLED_NEW"))
        {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                println("Value of Token = $FCM_TOKEN")

                val url = "https://staging2.contlo.in/v1/track"

                val headers = HashMap<String, String>()
                headers["accept"] = "application/json"
                headers["X-API-KEY"] = "$API_KEY"
                headers["content-type"] = "application/json"

                val params = JSONObject()
                if(fcm==null){
                    params.put("fcm_token", token)
                }
                else{
                    params.put("fcm_token", fcm)
                }


                val propString = "{\"version\":\"$APP_VERSION\",\"platform\":\"android\",\"source\":\"-\"}"
                val prop = JSONObject(propString)

                params.put("event","mobile_app_installed")
                params.put("properties",prop)


                CoroutineScope(Dispatchers.IO).launch {

                    val httpPostRequest = HttpClient()
                    val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                    println("Response APP Install Event: $response")
                    handler.post {
                        Toast.makeText(context, "Response APP Install Event: $response", Toast.LENGTH_SHORT).show()
                    }

                }

            })

        }

        //Put Flag after App Install
        val editor = sharedPreferences.edit()
        editor.putString("APP_INSTALLED_NEW", "1")
        editor.apply()

        //Retrieve Mandatory Attributes
        PACKAGE_NAME = context.packageName
        val packageManager = context.packageManager
        val applicationInfo = context.applicationInfo
        APP_NAME = packageManager.getApplicationLabel(applicationInfo).toString()
        APP_VERSION = packageManager.getPackageInfo(PACKAGE_NAME.toString(), 0).versionName
        OS_VERSION = Build.VERSION.RELEASE
        API_LEVEL = Build.VERSION.SDK_INT.toString()
        MODEL_NAME = Build.MODEL
        MANUFACTURER = Build.MANUFACTURER
        ANDROID_SDK_VERSION = Build.VERSION.SDK
        TIMEZONE = TimeZone.getDefault().displayName
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        CARRIER_INFO = telephonyManager.networkOperatorName
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        NETWORK_TYPE =
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                "WiFi"
            } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                "Mobile data"
            } else {
                "Unknown"
            }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = LocationManager.NETWORK_PROVIDER
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = locationManager.getLastKnownLocation(provider)

            // Get the latitude and longitude
            val latitude = location?.latitude ?: 0.0
            val longitude = location?.longitude ?: 0.0

            LATITUDE = latitude.toString()
            LONGITUDE = longitude.toString()
        }



        //Generate FCM or assign
        if (fcm == null) {



            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                FCM_TOKEN = task.result
                println("Value of Token = $FCM_TOKEN")

                //Put FCM in params
                val params = JSONObject()
                params.put("fcm_token", FCM_TOKEN)

                //Store FCM in Shared Preference
                sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor1 = sharedPreferences.edit()
                editor1.putString("FCM_TOKEN", FCM_TOKEN)
                editor1.apply()
                println(params.toString())

                //Make API Request
                val url = "https://staging2.contlo.in/v1/register_mobile_push"

                val headers = HashMap<String, String>()
                headers["accept"] = "application/json"
                headers["X-API-KEY"] = "$API_KEY"
                headers["content-type"] = "application/json"

                Handler().postDelayed({


                CoroutineScope(Dispatchers.IO).launch {

                    val httpPostRequest = HttpClient()
                    val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                    val jsonObject = JSONObject(response)
                    var externalId: String? = null
                    if(jsonObject.has("external_id")){
                    externalId = jsonObject.getString("external_id")
                    }


                    val editor2 = sharedPreferences.edit()
                    editor2.putString("Contlo External ID",externalId)
                    editor2.apply()

                    println("Response FCM Registration: $response      External_Id: $externalId")
                    handler.post {
                        Toast.makeText(context, "Response FCM Registration 1: $response", Toast.LENGTH_SHORT).show()
                    }

                }

                }, 2000)

            })

        } else {
            FCM_TOKEN = fcm
        }




        //Retrieve Advertising ID and store in SP and make api call to send to profile using fcm
        getAdID()

        println(" FCM: $FCM_TOKEN    AD_ID: $AD_ID ")

        FCM_TOKEN?.let { Log.d("Mandatory Attributes 1", it) }
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


        editor.putString("API_KEY", API_KEY)
        editor.putString("PACKAGE_NAME", PACKAGE_NAME)
        editor.putString("APP_NAME", APP_NAME)
        editor.putString("APP_VERSION", APP_VERSION)
        editor.putString("OS_VERSION", OS_VERSION)
        editor.putString("MANUFACTURER", MANUFACTURER)
        editor.putString("MODEL_NAME", MODEL_NAME)
        editor.putString("API_LEVEL", API_LEVEL)
        editor.putString("ANDROID_SDK_VERSION", ANDROID_SDK_VERSION)
        editor.putString("TIMEZONE", TIMEZONE)
        editor.putString("LATITUDE", LATITUDE)
        editor.putString("LONGITUDE", LONGITUDE)
        editor.putString("CARRIER_INFO", CARRIER_INFO)
        editor.putString("NETWORK_TYPE", NETWORK_TYPE)
        editor.apply()



    }

    private fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(context,
            "com.google.android.gms.permission.AD_ID"
        )== PackageManager.PERMISSION_GRANTED
    }



    fun getAdID(){

            // Retrieve the advertising ID in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    val advertisingId = adInfo.id
                    if (advertisingId != null) {
                        Log.d("Advertising ID", advertisingId)
                        AD_ID = advertisingId

                        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("AD_ID", AD_ID)
                        editor.apply()

                        if(hasReadPhoneStatePermission()){

                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result
                                println("Value of Token = $FCM_TOKEN")

                                val url = "https://staging2.contlo.in/v1/identify"

                                val headers = HashMap<String, String>()
                                headers["accept"] = "application/json"
                                headers["X-API-KEY"] = "$API_KEY"
                                headers["content-type"] = "application/json"

                                val params = JSONObject()
                                params.put("fcm_token", token)
                                params.put("ad_id",AD_ID)

                                CoroutineScope(Dispatchers.IO).launch {

                                    val httpPostRequest = HttpClient()
                                    val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                                    println("Response Send AD-ID: $response")
                                    handler.post {
                                        Toast.makeText(context, "Response Send AD_ID: $response", Toast.LENGTH_SHORT).show()
                                    }

                                }

                            })

                        }



                    }
                } catch (e: IOException) {
                    // Error retrieving advertising ID
                    e.printStackTrace()
                }
            }
        }


    fun getAPIKey(){

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




}