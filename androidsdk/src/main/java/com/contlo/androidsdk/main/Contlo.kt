package com.contlo.androidsdk.main

import android.app.Application
import android.content.Context
import android.util.Log
import com.contlo.androidsdk.UserProfile.ContloAudi
import com.contlo.androidsdk.api.ApiService
import com.contlo.androidsdk.api.Resource
import com.contlo.androidsdk.permissions.ContloPermissions
import com.contlo.androidsdk.utils.ContloCallback
import com.contlo.androidsdk.utils.ContloPreference
import com.contlo.androidsdk.utils.ContloUtils
import java.util.*
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class Contlo {
    companion object {
        private lateinit var application: Application
        private var contloInstance: Contlo? = null
        private const val TAG = "ContloSDK"
        fun init(context: Application, appKey: String): Contlo {
            if (contloInstance == null) {
                contloInstance = Contlo()
            }
            application = context
            initialize(appKey, null)
            return contloInstance as Contlo
        }
        
        internal fun getContext(): Context {
           return application.applicationContext
        }

        fun init(context: Context, apiKey: String, callback: ContloCallback?) {
            initialize(apiKey, callback)
        }

        fun initialize(apiKey: String?, callback: ContloCallback?) {
            val apiKey = ContloUtils.getAPIKey(apiKey)
            val preference = ContloPreference.getInstance(getContext())
            if (apiKey.isNullOrEmpty() && callback != null) {
                callback.onError(Exception("Invalid App ID"))
            }
            preference.setApiKey(apiKey!!)
            if (preference.isNewAppInstall()) {
                ContloUtils.generateFCM(
                    onSuccess = { token ->
                        ContloPreference.getInstance(getContext()).setNewAppInstall()
                        preference.setFcmKey(token)
                        if (!ContloPreference.getInstance(getContext()).isFcmFound()) {
                            ContloPreference.getInstance(getContext()).setFcmFound(true)
                            sendAdvertisingId(false)
                            //send Ad ID

                        }
                        if (!ContloPreference.getInstance(getContext())
                                .isPushConsentFound()
                        ) {
                            preference.setPushConsentFound()
                            val contloPermission = ContloPermissions()
                            contloPermission.changeMPConsent(
                                getContext(),
                                preference.getPushConsent(),
                                token
                            )
                        }
                    }, onError = { error ->
                        ContloUtils.printLog(getContext(), TAG, error.localizedMessage)
                    }
                )
            }
            var oldAppVersion = preference.getAppVersion()
            val newAppVersion = getContext().packageManager.getPackageInfo(
                getContext().packageName.toString(),
                0
            ).versionName
            if (!oldAppVersion.isNullOrBlank() && oldAppVersion != newAppVersion) {
                ContloUtils.printLog(getContext(), TAG, "App has been updated")
                sendAppEvent("mobile_app_updated", null, null)
            }
            callback?.onSuccess()
        }
        fun sendEvent(
            event: String,
            email: String?,
            phone: String?,
            eventProperty: HashMap<String, String>,
            profileProperty: HashMap<String, String>?
        ) {
            sendEvent(event, email, phone, eventProperty, profileProperty, null)
        }
        fun sendEvent(
            event: String,
            email: String?,
            phone: String?,
            eventProperty: HashMap<String, String>,
            profileProperty: HashMap<String, String>?,
            callback: ContloCallback?
        ) {
            if(event.contains(" ")) {
                callback?.onError(Exception("Event name cannot contain blankspace"))
            }
            CoroutineScope(Dispatchers.IO).launch {
                ApiService.sendEvent(event, email, phone, eventProperty, profileProperty)
                callback?.onSuccess()
            }
        }

        fun sendAppEvent(
            event: String,
            eventProperty: HashMap<String, String>?,
            profileProperty: HashMap<String, String>?
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val email = ContloPreference.getInstance(getContext()).getEmail()
                val phone = ContloPreference.getInstance(getContext()).getPhoneNumber()
                ApiService.sendEvent(event, email, phone, eventProperty, profileProperty)
            }
        }

        fun sendAdvertisingId(consent: Boolean) {
            var advertisingId: String?
            ContloUtils.printLog(Contlo.getContext(), "Contlo-TrackAdId", "Tracking AD-ID")

            // Retrieve the advertising ID in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    advertisingId = if (consent) {
                        val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext())
                        adInfo.id
                    } else {
                        ContloPreference.getInstance(getContext()).getAdvertisingId()
                    }
                    ContloPreference.getInstance(getContext()).setAdvertisingId(
                        advertisingId.toString()
                    )

                    if (!ContloPreference.getInstance(getContext()).getFcmKey()
                            .isNullOrBlank()
                    ) {
                        val audience = ContloUtils.retrieveCurrentUser()
                        audience.adverisingId = advertisingId
                        sendUserData(audience, false)
                    } else {
                        ContloPreference.getInstance(getContext()).setFcmFound(false)
                    }

                } catch (e: IOException) {
                    // Error retrieving advertising ID
                    e.printStackTrace()
                }
            }
        }

        fun sendUserData(audience: ContloAudi, isUpdate: Boolean) {
            audience.firebaseToken = ContloPreference.getInstance(getContext()).getFcmKey()
            audience.contloApiKey = ContloPreference.getInstance(getContext()).getApiKey()
            audience.isProfileUpdate = isUpdate

            audience.isMobilePushConsent =
                ContloPreference.getInstance(getContext()).getPushConsent()
            val params = Gson().toJson(audience, ContloAudi::class.java)
            ContloUtils.printLog(Contlo.getContext(), "Contlo-Audience", "Send User Data Params: $params")

            CoroutineScope(Dispatchers.IO).launch {

                val res = ApiService.sendUserData(params)
                when (res) {
                    is Resource.Error -> {
                        ContloUtils.printLog(Contlo.getContext(),
                            "Contlo-Audience",
                            "Send User Data Response: ${res.error?.localizedMessage}"
                        )
                    }
                    is Resource.Success -> {
                        ContloPreference.getInstance(getContext())
                            .setEmail(audience.userEmail.toString())
                        ContloPreference.getInstance(getContext())
                            .setPhoneNumber(audience.userPhone.toString())
                        ContloUtils.printLog(Contlo.getContext(), "Contlo-Audience", "Send User Data Response: ${res.data}")
                    }
                }
            }
        }
    }
}