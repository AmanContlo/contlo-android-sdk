package com.contlo.androidsdk.permissions

import android.content.Context
import com.contlo.androidsdk.model.ContloAudience
import com.contlo.androidsdk.api.ContloApiService
import com.contlo.androidsdk.api.Resource
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ContloPermissions {
    const val TAG = "ContloPermissions"
    fun sendPushConsent(context: Context,consent : Boolean) {

        ContloUtils.printLog(context, TAG, "Sending Push Consent")
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

        val jsonData = Gson().toJson(data.processAudience(), ContloAudience::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            ContloUtils.printLog(context, TAG, "Changing Mobile Push Consent to $mobilePushConsent")
            val response = ContloApiService.sendUserData(context, jsonData)
            when(response) {
                is Resource.Error -> {
                    ContloUtils.printLog(context, TAG, "Failed to change Push Consent ${response.error?.localizedMessage}")
                }
                is Resource.Success -> {
                    ContloUtils.printLog(context, TAG, "Response Changing Mobile Push Consent: $response")
                }
            }
        }
    }
}
