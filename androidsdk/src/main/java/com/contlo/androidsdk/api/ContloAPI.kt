package com.contlo.androidsdk.api

import android.content.Context
import android.util.Log
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class ContloAPI(context1: Context) {

    //API Key
    private var apiKey: String? = null

    private val context = context1

    private var currentTime: String = Date().toString()
    private var currentTimeZone: String = TimeZone.getDefault().id.toString()

    private var FCM_TOKEN: String? = null
    private var API_KEY: String? = null
    private var PACKAGE_NAME: String? = null
    private var APP_NAME: String? = null
    private var APP_VERSION: String? = null
    private var OS_VERSION: String? = null
    private var MANUFACTURER: String? = null
    private var MODEL_NAME: String? = null
    private var API_LEVEL: String? = null
    private var ANDROID_SDK_VERSION: String? = null
    private var NETWORK_TYPE: String? = null

    internal fun sendPushCallbacks(event: String,internalID: String){

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiKey = sharedPreferences.getString("API_KEY", null)


        val url: String = when (event) {
            "received" -> "https://callback-service.contlo.com/mobilepush_webhooks/mobilepush_receive"
            "clicked" -> "https://callback-service.contlo.com/mobilepush_webhooks/mobilepush_click"
            "dismissed" -> "https://callback-service.contlo.com/mobilepush_webhooks/mobilepush_dismiss"

            else -> ""
        }

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"


        val params = JSONObject()
        params.put("internal_id", internalID)

        Log.d("Contlo-PushCallback", "Notification Internal ID - $internalID")

        CoroutineScope(Dispatchers.IO).launch {

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-PushCallback", "$event - $response")


        }


    }


    fun sendEvent(event: String, prop: JSONObject){


        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        FCM_TOKEN = sharedPreferences.getString("FCM_TOKEN", null)
        API_KEY = sharedPreferences.getString("API_KEY", null)
        PACKAGE_NAME = sharedPreferences.getString("PACKAGE_NAME", null)
        APP_NAME = sharedPreferences.getString("APP_NAME", null)
        APP_VERSION = sharedPreferences.getString("APP_VERSION", null)
        OS_VERSION = sharedPreferences.getString("OS_VERSION", null)
        MODEL_NAME = sharedPreferences.getString("MODEL_NAME", null)
        MANUFACTURER = sharedPreferences.getString("MANUFACTURER", null)
        API_LEVEL = sharedPreferences.getString("API_LEVEL", null)
        ANDROID_SDK_VERSION = sharedPreferences.getString("ANDROID_SDK_VERSION", null)
        NETWORK_TYPE = sharedPreferences.getString("NETWORK_TYPE", null)

        prop.put("app_name",APP_NAME)
        prop.put("app_version",APP_VERSION)
        prop.put("package_name",PACKAGE_NAME)
        prop.put("os_version",OS_VERSION)
        prop.put("model_name",MODEL_NAME)
        prop.put("manufacturer",MANUFACTURER)
        prop.put("api_level",API_LEVEL)
        prop.put("android_sdk_version",ANDROID_SDK_VERSION)
        prop.put("network_type",NETWORK_TYPE)
        prop.put("created_at",currentTime)
        prop.put("timezone",currentTimeZone)

        val url = "https://staging2.contlo.in/v1/track"

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$API_KEY"
        headers["content-type"] = "application/json"


        val params = JSONObject()
        params.put("event", event)
        params.put("properties",prop)
        params.put("fcm_token", FCM_TOKEN)

        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

        if(mobilePushConsent)
            params.put("mobile_push_consent", "TRUE")
        else
            params.put("mobile_push_consent", "FALSE")


        Log.d("Contlo-Events","Params: $params")

        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-Events", "Sending Event - $event")

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Events","$event response: $response")

        }

    }



}