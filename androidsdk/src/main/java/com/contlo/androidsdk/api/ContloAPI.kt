package com.contlo.androidsdk.api

import android.content.Context
import android.os.Build
import android.util.Log
import com.contlo.contlosdk.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ContloAPI(context1: Context) {

    //API Key
    private var apiKey: String? = null

    private val context = context1

    private var currentTime: String = Date().toString()
    private var currentTimeZone: String = TimeZone.getDefault().id.toString()
    private lateinit var utcEpoch: String

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
    private var EXTERNAL_ID: String? = null
    private var OS_TYPE: String? = null
    private var SOURCE: String? = null
    private var SDK_VERSION: String? = null

    internal fun sendPushCallbacks(event: String,internalID: String){

        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        apiKey = sharedPreferences.getString("API_KEY", null)


        val url: String = when (event) {
            "received" -> context.getString(R.string.received_callback)
            "clicked" -> context.getString(R.string.clicked_callback)
            "dismissed" -> context.getString(R.string.dismissed_callback)

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

    fun sendEvent(event: String, email: String?, phoneNumber: String?,eventProperties: JSONObject?, profileProperties: JSONObject?): String? {

        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
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
        EXTERNAL_ID = sharedPreferences.getString("EXTERNAL_ID",null)
        OS_TYPE = sharedPreferences.getString("OS_TYPE",null)
        SOURCE = sharedPreferences.getString("SOURCE",null)
        SDK_VERSION = sharedPreferences.getString("SDK_VERSION",null)


        utcEpoch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val zonedDateTime: ZonedDateTime = ZonedDateTime.parse(currentTime, formatter)
            val instant: Instant = zonedDateTime.toInstant()
            (instant.epochSecond).toString()

        } else {

            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val date = dateFormat.parse(currentTime)
            (date!!.time / 1000).toString()

        }

        Log.d("Contlo-API-Time", "time - $utcEpoch")

        val mandatoryParams = JSONObject()
        mandatoryParams.put("app_name",APP_NAME)
        mandatoryParams.put("app_version",APP_VERSION)
        mandatoryParams.put("package_name",PACKAGE_NAME)
        mandatoryParams.put("os_version",OS_VERSION)
        mandatoryParams.put("model_name",MODEL_NAME)
        mandatoryParams.put("manufacturer",MANUFACTURER)
        mandatoryParams.put("api_level",API_LEVEL)
        mandatoryParams.put("network_type",NETWORK_TYPE)
        mandatoryParams.put("device_event_time",utcEpoch)
        mandatoryParams.put("timezone",currentTimeZone)
        mandatoryParams.put("os_type",OS_TYPE)
        mandatoryParams.put("source",SOURCE)
        mandatoryParams.put("sdk_version",SDK_VERSION)

        val url = context.getString(R.string.track_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$API_KEY"
        headers["content-type"] = "application/json"

        val params = JSONObject()


        if(eventProperties==null)
            params.put("properties",mandatoryParams)
        else{
            val keysIterator = mandatoryParams.keys()
            while (keysIterator.hasNext()) {
                val key = keysIterator.next()
                val value = mandatoryParams[key]
                eventProperties.put(key, value)
            }
            params.put("properties",eventProperties)
        }


        params.put("event", event)
        params.put("fcm_token", FCM_TOKEN)
        if(!email.isNullOrBlank()){
            params.put("email", email)
        }
        if(!phoneNumber.isNullOrBlank()){
            params.put("phone_number",phoneNumber)
        }

        params.put("profile_properties",profileProperties)

        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

        val checkMobilePushConsent = if (mobilePushConsent) "TRUE" else "FALSE"
        params.put("mobile_push_consent", checkMobilePushConsent)

        Log.d("Contlo-Events","Params: $params")

        var response: String? = null

        CoroutineScope(Dispatchers.IO).async {

            Log.d("Contlo-Events", "Sending Event - $event")

            val httpPostRequest = HttpClient()
            response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Events","$event response: $response")

            return@async response

        }

        return response

    }



}