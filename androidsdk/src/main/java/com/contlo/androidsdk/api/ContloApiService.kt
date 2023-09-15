package com.contlo.androidsdk.api

import android.content.Context
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.model.Event
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import com.contlo.contlosdk.BuildConfig
import com.contlo.contlosdk.R
import com.google.gson.Gson
import org.json.JSONObject

/**
 * @author Aman
 * This class contains all the API calls for staging and production both
 * For changing base url, this is the priority:
 * 1. Check for String url ( for clients to configure, if required in future)
 * 2. Changing base url (Staging and Prod) by adding CONTLO_BASE_URL in Android Manifest (for clients and developers)
 * 3. BASE_URL in build.gradle as a fallback
**/
class ContloApiService {
    companion object {
    private const val TAG = "ApiService"
    private const val BASE_URL = "CONTLO_BASE_URL"
    private const val IDENTIFY_V2 = "/v2/identify"
    private const val EVENTS_V2 = "/v2/track"

    //Checks whether AndroidManifest contains "CONTLO_BASE_URL" key for Staging/Prod env
    private fun getBaseUrl(): String {
        var baseUrl = ContloUtils.getMetaDataValue(Contlo.getContext(), BASE_URL)
        if(baseUrl.isNullOrBlank()) {
            baseUrl = BuildConfig.BASE_URL
        }
        return baseUrl
    }
    private fun getIdentifyUrl(): String {
        val identifyString = Contlo.getContext().getString(R.string.identify_url)
        if(!identifyString.isNullOrBlank()) {
            return identifyString
        }
        val baseUrl = getBaseUrl()
        return baseUrl + IDENTIFY_V2
    }
    private fun getEventTrackUrl(): String {
        val trackString = Contlo.getContext().getString(R.string.track_url)
        if(!trackString.isNullOrBlank()) {
            return trackString
        }
        val baseUrl = getBaseUrl()
        return baseUrl + EVENTS_V2
    }

    fun sendUserData(context: Context, data: String): Resource<String> {
        val url = getIdentifyUrl()
        val httpClient = HttpClient()

        try {
            val response = httpClient.sendPOSTRequest(context, url, data)
            val responseData = JSONObject(response)
            return if(responseData.has("success") && responseData.getString("success").equals("true")) {
                ContloUtils.printLog(context, TAG, "User data successfully sent: $data , Response: $response")

                Resource.Success(response)
            } else {
                ContloUtils.printLog(context, TAG, "Failed to send user Data $responseData")
                Resource.Error(Throwable("Some error occured"), response)
            }
        } catch (e: Exception) {
            ContloUtils.printLog(context, TAG, "Failed to send user Data ${e.localizedMessage}")
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
        val url = getEventTrackUrl()
        val httpClient = HttpClient()
        try {
            val eventData = ContloUtils.retrieveEventData()

            if(email.isNullOrBlank() && phone.isNullOrBlank() && ContloPreference.getInstance(Contlo.getContext()).getFcmKey().isNullOrBlank()) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "All identifiers are empty, cannot send event")
                return Resource.Error(Throwable("All identifiers are empty, cannot send Event"))
            }

            eventProperty?.let { eventData.putAll(it) }
            val event = Event(event = eventName.trim().replace(' ', '_'),
                fcmToken = ContloPreference.getInstance(Contlo.getContext()).getFcmKey(),
                phoneNumber = if(phone.isNullOrBlank()) null else phone,
                property = eventData,
                pushConsent = ContloUtils.isNotificationPermissionGiven(),
                email = if(email.isNullOrBlank()) null else email,
                profileProperty = profileProperty
            )

            val jsonData = Gson().toJson(event.processEvent(), Event::class.java)
            val response = httpClient.sendPOSTRequest(url, jsonData)
            val responseData = JSONObject(response)
            return if(responseData.has("success") && responseData.getString("success").equals("true")) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "Event successfully sent: $jsonData , Response: $response")
                Resource.Success(response)
            } else {
                Resource.Error(Throwable("Some error occured"), response)
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