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
    private var USER_FIRST_NAME: String? = null
    private var USER_LAST_NAME: String? = null
    private var USER_CITY: String? = null
    private var USER_COUNTRY: String? = null
    private var USER_ZIP: String? = null
    private var USER_EMAIL: String? = null
    private var USER_PHONE: String? = null
    private var CUSTOM_PROPERTIES = JSONObject()


    fun setUserFirstName(fname: String?){

        editor.putString("user_first_name",fname)
        editor.apply()

    }

    fun setUserLastName(lname: String?){

        editor.putString("user_last_name",lname)
        editor.apply()

    }

    fun setUserCity(city: String?){

        editor.putString("user_city",city)
        editor.apply()

    }

    fun setUserCountry(country: String?){

        editor.putString("user_country",country)
        editor.apply()

    }

    fun setUserZip(zip: String?){

        editor.putString("user_zip",zip)
        editor.apply()

    }

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

    fun sendUserDatatoContlo(isUpdate: Boolean): String {

        val fcm = sharedPreferences.getString("FCM_TOKEN", null)
        val apiKey = sharedPreferences.getString("API_KEY", null)

        val url = context.getString(R.string.identify_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"

        val params = JSONObject()
        params.put("fcm_token", fcm)

        USER_FIRST_NAME = sharedPreferences.getString("user_first_name",null)
        USER_LAST_NAME = sharedPreferences.getString("user_last_name",null)
        USER_EMAIL = sharedPreferences.getString("user_email",null)
        USER_PHONE = sharedPreferences.getString("user_phone_number",null)
        USER_CITY = sharedPreferences.getString("user_city",null)
        USER_COUNTRY = sharedPreferences.getString("user_country",null)
        USER_ZIP = sharedPreferences.getString("user_zip",null)

        for ((key, value) in sharedPreferences.all) {
            if (key.startsWith("custom_property_")) {
                val customKey = key.substring("custom_property_".length)
                CUSTOM_PROPERTIES.put(customKey, value)
            }
        }

        USER_FIRST_NAME?.let { params.put("first_name", it) }
        USER_LAST_NAME?.let { params.put("last_name", it) }
        USER_EMAIL?.let { params.put("email", it) }
        USER_PHONE?.let { params.put("phone_number", it) }
        USER_CITY?.let { params.put("city", it) }
        USER_COUNTRY?.let { params.put("country", it) }
        USER_ZIP?.let { params.put("zip", it) }
        CUSTOM_PROPERTIES.let { params.put("custom_properties", it) }

        val mobilePushConsent = sharedPreferences.getBoolean("MOBILE_PUSH_CONSENT",false)

        val checkMobilePushConsent = if (mobilePushConsent) "TRUE" else "FALSE"
        params.put("mobile_push_consent", checkMobilePushConsent)

        val checkUpdate = if (isUpdate) "TRUE" else "FALSE"
        params.put("is_profile_update", checkUpdate)

        Log.d("Contlo-Audience", "Send User Data Params: $params")

        var response: String? = null

        CoroutineScope(Dispatchers.IO).async {

            val httpPostRequest = HttpClient()
            response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Audience", "Send User Data Response: $response")

            return@async response
        }
        return response.toString()
    }
}
