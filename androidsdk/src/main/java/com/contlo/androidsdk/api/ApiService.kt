package com.contlo.androidsdk.api

import com.contlo.androidsdk.main.ContloApp
import com.contlo.androidsdk.model.Event
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.contlo.contlosdk.R
import com.google.gson.Gson

class ApiService {
    private val httpClient: HttpClient = HttpClient()

    fun sendUserData(data: String): Resource<String> {
        val url = ContloApp.appContext.getString(R.string.identify_url)
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
companion object {
    fun sendEvent(event: String, email: String?, phone: String?, eventProperty: HashMap<String, String>?, profileProperty: HashMap<String, String>?): Resource<String> {
        val url = ContloApp.appContext.getString(R.string.track_url)
        val httpClient = HttpClient()
        try {
            val eventData = ContloUtils.retrieveEventData()

            eventProperty?.let { eventData.putAll(it) }
            var event = Event(event = event,
                fcmToken = ContloPreference.getInstance(ContloApp.appContext).getFcmKey(),
                phoneNumber = phone,
                property = eventData,
                pushConsent = ContloPreference.getInstance(ContloApp.appContext).getPushConsent(),
                email = email,
                profileProperty = profileProperty
            )

            val jsonData = Gson().toJson(event, Event::class.java)
            val response = httpClient.sendPOSTRequest(url, jsonData)
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

    fun sendAdvertisingId(data: String): Resource<String> {
        val url = ContloApp.appContext.getString(R.string.identify_url)
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