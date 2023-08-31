package com.contlo.androidsdk.permissions

import android.content.Context
import android.util.Log
import com.contlo.androidsdk.api.HttpClient
import com.contlo.contlosdk.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.HashMap


class ContloPermissions() {

    private var fcm: String? = null
    private var apiKey: String? = null

    fun sendPushConsent(context: Context,consent : Boolean){

        Log.d("Contlo-Permission", "Sending Push Consent")

        val sharedPreferences  = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("MOBILE_PUSH_CONSENT",consent)
        editor.apply()

        if(consent){

            if(sharedPreferences.contains("Already Subscribed")){

                //Do Nothing

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                editor.putString("Already Subscribed","1")
                editor.remove("Already Unsubscribed")
                editor.apply()
                changeMPConsent(context,true,null)

            }

            else{

                editor.putString("Already Subscribed","1")
                editor.apply()
                changeMPConsent(context,true,null)

            }

        }

        else if(!consent){

            if(sharedPreferences.contains("Already Subscribed")){

                editor.putString("Already Unsubscribed","1")
                editor.remove("Already Subscribed")
                editor.apply()
                changeMPConsent(context,false,null)

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                //Do Nothing

            }

            else{

                editor.putString("Already Unsubscribed","1")
                editor.apply()
                changeMPConsent(context,false,null)

            }

        }

   }

    internal fun changeMPConsent(context: Context,mobilePushConsent: Boolean, fcmToken: String?){

        //Retrieve fcm and api key
        val sharedPreferences = context.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(fcmToken != null){

            Log.d("Contlo-Permissions","Changing consent Directly")
            fcm = sharedPreferences.getString("FCM_TOKEN",null)
            if (fcm == null){
                editor.putBoolean("PUSH_CONSENT_FCM_NOT_FOUND",true)
                Log.d("Contlo-Permissions","PUSH_CONSENT_FCM_NOT_FOUND")
                editor.apply()
                return
            }
        }
        else{

            Log.d("Contlo-Permissions","Changing consent from onSuccess")
            fcm = fcmToken
            editor.remove("PUSH_CONSENT_FCM_NOT_FOUND")
            editor.apply()
        }

        apiKey = sharedPreferences.getString("API_KEY", null)

        //Put FCM and consent in params
        val params = JSONObject()
        params.put("fcm_token", fcm)

        params.put("mobile_push_consent",mobilePushConsent)

        //Make API Request
        val url = context.getString(R.string.identify_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"


        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-Permission", "Changing Mobile Push Consent to $mobilePushConsent")

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Permission", "Response Changing Mobile Push Consent: $response")

        }

    }

}
