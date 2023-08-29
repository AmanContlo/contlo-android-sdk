package com.contlo.androidsdk.api

import android.content.Context
import android.util.Log
import com.contlo.contlosdk.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ContloAPI(context1: Context) {

    //API Key
    private var apiKey: String? = null
    private val context = context1
    private var currentTime: String = Date().toString()
    private var currentTimeZone: String = TimeZone.getDefault().id.toString()
    private lateinit var utcEpoch: String

    internal fun sendPushCallbacks(event: String,internalID: String){

        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        apiKey = sharedPreferences.getString("API_KEY", null)

        val url: String = when (event) {
            "received" -> context.getString(R.string.received_callback)
            "clicked" -> context.getString(R.string.clicked_callback)
            "dismissed" -> context.getString(R.string.dismissed_callback)

            else -> return
        }

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("internal_id", internalID)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                Log.d("Contlo-PushCallback", "$event - $response")
            }
            catch (e: Exception) {
                Log.e("Contlo-PushCallback", "Error sending $event callback", e)
            }
        }
    }

    fun sendEvent(event: String, email: String?, phoneNumber: String?, eventProperties: JSONObject?, profileProperties: JSONObject?) {

        //Retrieve mandatory params
        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        apiKey = sharedPreferences.getString("API_KEY", null)

        val preferenceKeys = listOf(
            "FCM_TOKEN", "PACKAGE_NAME", "APP_NAME",
            "APP_VERSION", "OS_VERSION", "MODEL_NAME", "MANUFACTURER", "API_LEVEL",
            "NETWORK_TYPE", "EXTERNAL_ID", "OS_TYPE", "SOURCE", "SDK_VERSION")

        val preferencesMap = mutableMapOf<String, String?>()

        //Put mandatory params in a hashmap
        for (key in preferenceKeys) {
            preferencesMap[key] = sharedPreferences.getString(key, null)
        }

        //Get current time in utc epoch milliseconds
        val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val date = formatter.parse(currentTime)
        utcEpoch = ((date?.time ?: 0) / 1000).toString()

        val mandatoryParams = JSONObject()
        mandatoryParams.put("app_name",preferencesMap["APP_NAME"])
        mandatoryParams.put("app_version",preferencesMap["APP_VERSION"])
        mandatoryParams.put("package_name",preferencesMap["PACKAGE_NAME"])
        mandatoryParams.put("os_version",preferencesMap["OS_VERSION"])
        mandatoryParams.put("model_name",preferencesMap["MODEL_NAME"])
        mandatoryParams.put("manufacturer",preferencesMap["MANUFACTURER"])
        mandatoryParams.put("api_level",preferencesMap["API_LEVEL"])
        mandatoryParams.put("network_type",preferencesMap["NETWORK_TYPE"])
        mandatoryParams.put("os_type",preferencesMap["OS_TYPE"])
        mandatoryParams.put("source",preferencesMap["SOURCE"])
        mandatoryParams.put("sdk_version",preferencesMap["SDK_VERSION"])
        mandatoryParams.put("device_event_time",utcEpoch)
        mandatoryParams.put("timezone",currentTimeZone)

        val url = context.getString(R.string.track_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()

        //Event key and identifiers
        params.put("event", event)
        params.put("fcm_token", preferencesMap["FCM_TOKEN"])

        if(!email.isNullOrBlank()){
            params.put("email", email)
        }

        if(!phoneNumber.isNullOrBlank()){
            params.put("phone_number",phoneNumber)
        }

        //Attach mandatory params to event properties, else send them if received none from user
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

        //Profile Properties
        params.put("profile_properties",profileProperties)

        //Get latest mobile push consent
        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)
        val checkMobilePushConsent = if (mobilePushConsent) "TRUE" else "FALSE"
        params.put("mobile_push_consent", checkMobilePushConsent)

        //Send the event on a coroutine
        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-Events", "Sending Event $event with params $params")

            try {
                val httpPostRequest = HttpClient()
                val response = httpPostRequest.sendPOSTRequest(url, headers, params)

                Log.d("Contlo-Events", "$event response: $response")

                val jsonObject = JSONObject(response)
                if (jsonObject.has("external_id")) {
                    editor.putString("Contlo External ID", jsonObject.getString("external_id"))
                    editor.apply()
                }
            }
            catch (e: Exception) {
                Log.e("Contlo-Events", "Error sending $event", e)
            }
        }
    }
}