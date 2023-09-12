package com.contlo.androidsdk.api

import android.content.Context
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.model.Event
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.contlo.contlosdk.R
import com.google.gson.Gson
import org.json.JSONObject

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
    fun sendEvent(event: String, eventProperty: HashMap<String, String>?, profileProperty: HashMap<String, String>?): Resource<String> {
        return sendEvent(event, ContloPreference.getInstance(Contlo.getContext()).getEmail(),
            ContloPreference.getInstance(Contlo.getContext()).getPhoneNumber(),
            eventProperty,
            profileProperty
            )
    }
    fun sendEvent(eventName: String, email: String?, phone: String?, eventProperty: HashMap<String, String>?, profileProperty: HashMap<String, String>?): Resource<String> {
        val url = Contlo.getContext().getString(R.string.track_url)
        val httpClient = HttpClient()
        try {
            val eventData = ContloUtils.retrieveEventData()

            if(email.isNullOrBlank() && phone.isNullOrBlank() && ContloPreference.getInstance(Contlo.getContext()).getFcmKey().isNullOrBlank()) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "All identifiers are empty, cannot send event")
                return Resource.Error(Throwable("All identifiers are empty, cannot send Event"))
            }

            eventProperty?.let { eventData.putAll(it) }
            var event = Event(event = eventName.trim().replace(' ', '_'),
                fcmToken = ContloPreference.getInstance(Contlo.getContext()).getFcmKey(),
                phoneNumber = phone,
                property = eventData,
                pushConsent = ContloUtils.isNotificationPermissionGiven(),
//                pushConsent = ContloPreference.getInstance(Contlo.getContext()).getPushConsent(),
                email = email,
                profileProperty = profileProperty
            )

            val jsonData = Gson().toJson(event.removeEmptyValues(), Event::class.java)
            val response = httpClient.sendPOSTRequest(url, jsonData)
            if(response.contains("success")) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "Event successfully sent: $jsonData , Response: $response")
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

    fun sendDismissCallback(context: Context, internalId: String): Resource<String> {
        val url = context.getString(R.string.dismissed_callback)
        val httpClient = HttpClient()
        try {
            val params = JSONObject()
            params.put("internal_id", internalId)
            val response = httpClient.sendPOSTRequest(context, url, params.toString())
            ContloUtils.printLog(TAG, response)
            if(response.contains("success")) {
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    fun sendClickCallback(context: Context, internalId: String): Resource<String> {
        val url = context.getString(R.string.clicked_callback)
        val httpClient = HttpClient()
        try {
            val params = JSONObject()
            params.put("internal_id", internalId)
            val response = httpClient.sendPOSTRequest(context, url, params.toString())
            if(response.contains("success")) {
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    fun sendReceivedCallback(context: Context, internalId: String): Resource<String> {
        val url = context.getString(R.string.received_callback)
        val httpClient = HttpClient()
        try {
            val params = JSONObject()
            params.put("internal_id", internalId)
            val response = httpClient.sendPOSTRequest(context, url, params.toString())
            if(response.contains("success")) {
                return Resource.Success(response)
            } else {
                return Resource.Error(Throwable("Some error occured"), response)
            }
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

}


}