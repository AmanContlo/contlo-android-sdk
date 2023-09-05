package com.contlo.androidsdk.permissions

import android.content.Context
import android.util.Log
import com.contlo.androidsdk.UserProfile.ContloAudi
import com.contlo.androidsdk.api.ApiService
import com.contlo.androidsdk.api.HttpClient
import com.contlo.androidsdk.api.Resource
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.contlo.contlosdk.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.HashMap


class ContloPermissions {
    fun sendPushConsent(context: Context,consent : Boolean) {

        ContloUtils.printLog(Contlo.getContext(), "Contlo-Permission", "Sending Push Consent")
        val previousConsent = ContloPreference.getInstance(Contlo.getContext()).getPushConsent()
        if(consent && !previousConsent) {
            changeMPConsent(context,true,null)
        } else if(!consent && previousConsent) {
            changeMPConsent(context,false,null)
        }
        ContloPreference.getInstance(Contlo.getContext()).setPushConsent(consent)
   }

    internal fun changeMPConsent(context: Context,mobilePushConsent: Boolean, fcmToken: String?){
        val data = ContloUtils.retrieveCurrentUser()
        data.isMobilePushConsent = mobilePushConsent

        val jsonData = Gson().toJson(data, ContloAudi::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            ContloUtils.printLog(Contlo.getContext(), "Contlo-Permission", "Changing Mobile Push Consent to $mobilePushConsent")
            val response = ApiService.sendUserData(jsonData)
            when(response) {
                is Resource.Error -> {
                    ContloUtils.printLog(Contlo.getContext(), "Contlo-Permission", "Failed to change Push Consent ${response.error?.localizedMessage}")
                }
                is Resource.Success -> {

                    ContloUtils.printLog(Contlo.getContext(), "Contlo-Permission", "Response Changing Mobile Push Consent: $response")
                }
            }

        }

    }

}
