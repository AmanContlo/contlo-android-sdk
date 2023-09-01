package com.contlo.androidsdk.UserProfile


import android.content.Context
import android.util.Log
import com.contlo.androidsdk.api.HttpClient
import com.contlo.contlosdk.R
import org.json.JSONObject
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ContloAudience(val context: Context ) {

    private val sharedPreferences = context.getSharedPreferences("contlosdk",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    //User Attributes
    private var userFirstName: String? = null
    private var userLastName: String? = null
    private var userCity: String? = null
    private var userCountry: String? = null
    private var userZip: String? = null
    private var userEmail: String? = null
    private var userPhone: String? = null
    private var customProperties = JSONObject()


    fun setUserFirstName(fname: String?) { editor.putString("user_first_name",fname).apply() }

    fun setUserLastName(lname: String?) { editor.putString("user_last_name",lname).apply() }

    fun setUserCity(city: String?) { editor.putString("user_city",city).apply() }

    fun setUserCountry(country: String?) { editor.putString("user_country",country).apply() }

    fun setUserZip(zip: String?){ editor.putString("user_zip",zip).apply() }

    fun setUserEmail(email: String?){

        editor.putString("user_email",email)
        editor.apply()

    }

    fun setUserPhone(phone: String?){

        editor.putString("user_phone_number",phone)
        editor.apply()

    }

    fun setUserAttribute(customProperties: JSONObject){

        val keys = customProperties.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = customProperties.optString(key)
            editor.putString("custom_property_$key", value).apply()
        }

    }

    fun sendUserDatatoContlo(isUpdate: Boolean) {

        val fcm = sharedPreferences.getString("FCM_TOKEN", null)
        val apiKey = sharedPreferences.getString("API_KEY", null)

        val url = context.getString(R.string.identify_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("fcm_token", fcm)

        userFirstName = sharedPreferences.getString("user_first_name",null)
        userLastName = sharedPreferences.getString("user_last_name",null)
        userEmail = sharedPreferences.getString("user_email",null)
        userPhone = sharedPreferences.getString("user_phone_number",null)
        userCity = sharedPreferences.getString("user_city",null)
        userCountry = sharedPreferences.getString("user_country",null)
        userZip = sharedPreferences.getString("user_zip",null)

        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("custom_property_")) {
                val customKey = key.substring("custom_property_".length)
                customProperties.put(customKey, value)
            }
        }

        userFirstName?.let { params.put("first_name", it) }
        userLastName?.let { params.put("last_name", it) }
        userEmail?.let { params.put("email", it) }
        userPhone?.let { params.put("phone_number", it) }
        userCity?.let { params.put("city", it) }
        userCountry?.let { params.put("country", it) }
        userZip?.let { params.put("zip", it) }
        customProperties.let { params.put("custom_properties", it) }

        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

        params.put("mobile_push_consent", mobilePushConsent)

        params.put("is_profile_update", isUpdate)

        Log.d("Contlo-Audience", "Send User Data Params: $params")

        CoroutineScope(Dispatchers.IO).launch {

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Audience", "Send User Data Response: $response")


        }
    }
}
