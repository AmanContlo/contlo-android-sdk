package com.contlo.androidsdk.api

import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.model.Event
import com.contlo.androidsdk.model.EventProperty
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.contlo.contlosdk.R
import com.google.gson.Gson

class ApiService {

companion object {
    private const val TAG = "ApiService"
    fun sendUserData(data: String): Resource<String> {
        val url = Contlo.getContext().getString(R.string.identify_url)
        val httpClient = HttpClient()
        try {
            val response = httpClient.sendPOSTRequest(url, data)
            if(response.contains("success")) {
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }
    fun sendEvent(event: String, eventProperty: HashMap<String, String>?, profileProperty: HashMap<String, String>?) {
        sendEvent(event, ContloPreference.getInstance(Contlo.getContext()).getEmail(),
            ContloPreference.getInstance(Contlo.getContext()).getPhoneNumber(),
            eventProperty,
            profileProperty
            )
    }
    fun sendEvent(event: String, email: String?, phone: String?, eventProperty: HashMap<String, String>?, profileProperty: HashMap<String, String>?): Resource<String> {
        val url = Contlo.getContext().getString(R.string.track_url)
        val httpClient = HttpClient()
        try {
            val eventData = ContloUtils.retrieveEventData()

            eventProperty?.let { eventData.putAll(it) }
            var event = Event(event = event,
                fcmToken = ContloPreference.getInstance(Contlo.getContext()).getFcmKey(),
                phoneNumber = phone,
                property = eventData,
                pushConsent = ContloPreference.getInstance(Contlo.getContext()).getPushConsent(),
                email = email,
                profileProperty = profileProperty
            )

            val jsonData = Gson().toJson(event, Event::class.java)
            val response = httpClient.sendPOSTRequest(url, jsonData)
            if(response.contains("success")) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "Event successfully sent: $event")
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some Error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }
    fun sendAdvertisingId(data: String): Resource<String> {
        val url = Contlo.getContext().getString(R.string.identify_url)
        val httpClient = HttpClient()
        try {
            val response = httpClient.sendPOSTRequest(url, data)
            if(response.contains("success")) {
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some Error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

}


}