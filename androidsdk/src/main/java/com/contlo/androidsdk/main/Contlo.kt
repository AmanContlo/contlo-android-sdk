package com.contlo.androidsdk.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.contlo.androidsdk.model.ContloAudience
import com.contlo.androidsdk.api.ContloApiService
import com.contlo.androidsdk.api.Resource
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
        @SuppressLint("StaticFieldLeak")
        private lateinit var application: Context
        @SuppressLint("StaticFieldLeak")
        private var contloInstance: Contlo? = null
        private const val TAG = "ContloSDK"
        fun init(context: Context, appKey: String): Contlo {

            if (contloInstance == null) {
                contloInstance = Contlo()
            }
            application = if (context is Application) {
                context
            } else {
                context.applicationContext
            }
            initialize(appKey, null)
            return contloInstance as Contlo
        }
        
        internal fun getContext(): Context{
            if(!this::application.isInitialized) {
                throw UninitializedPropertyAccessException(Throwable("Contlo SDK has not been initialized"))
            }
           return application
        }

        internal fun getContext(context: Context): Context {
            if(this::application.isInitialized) {
                return application
            } else {
                application = if (context is Application) {
                    context
                } else {
                    context.applicationContext
                }
            }
            return application
        }

        fun init(context: Context, apiKey: String, callback: ContloCallback?) {
            initialize(apiKey, callback)
        }

        private fun initialize(apiKey: String?, callback: ContloCallback?) {
            val apiKey = ContloUtils.getAPIKey(apiKey)
            val preference = ContloPreference.getInstance(getContext())
            if (apiKey.isNullOrEmpty() && callback != null) {
                callback.onError(Exception("Invalid App ID"))
            }
            preference.setApiKey(apiKey!!)
            if (preference.isNewAppInstall()) {
                ContloUtils.generateFCM(
                    onSuccess = { token ->
                        preference.setNewAppInstall(false)
                        preference.setFcmKey(token)
                        sendAppEvent("mobile_app_installed", null, null)
                    }, onError = { error ->
                        sendAdvertisingId(false)
                        ContloUtils.printLog(getContext(), TAG, error.localizedMessage)
                    }
                )
            }
            val oldAppVersion = preference.getAppVersion()
            val newAppVersion = getContext().packageManager.getPackageInfo(
                getContext().packageName.toString(),
                0
            ).versionName
            if (!oldAppVersion.isNullOrBlank() && !oldAppVersion.equals(newAppVersion)) {
                ContloUtils.printLog(getContext(), TAG, "App has been updated")
                ContloPreference.getInstance(getContext()).setAppVersion(newAppVersion)
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
                ContloApiService.sendEvent(event, email, phone, eventProperty, profileProperty)
                callback?.onSuccess()
            }
        }

        fun sendAppEvent(
            event: String,
            eventProperty: HashMap<String, String>?,
            profileProperty: HashMap<String, String>?
        ) {
            try {
                getContext()
            } catch (e: UninitializedPropertyAccessException) {
                ContloUtils.printLog(TAG, e.localizedMessage?: "SDK has not been initialized, failed to send event")
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                val eventData = ContloApiService.sendEvent(event, eventProperty, profileProperty)
                when(eventData) {
                    is Resource.Error -> {
                        ContloUtils.printLog(getContext(), TAG, eventData.error?.localizedMessage?: "Some error occured")
                    }
                    is Resource.Success -> {
                        ContloUtils.printLog(getContext(), TAG, "Successfully sent App event: ${eventData.data}")
                    }
                }
            }
        }

        fun sendAdvertisingId(consent: Boolean) {
            var advertisingId: String?
            ContloUtils.printLog(Contlo.getContext(), TAG, "Tracking AD-ID")

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
                        audience.advertisingId = advertisingId
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

        fun sendUserData(audience: ContloAudience, isUpdate: Boolean) {
            audience.firebaseToken = ContloPreference.getInstance(getContext()).getFcmKey()
            audience.contloApiKey = ContloPreference.getInstance(getContext()).getApiKey()
            audience.isProfileUpdate = isUpdate

            audience.isMobilePushConsent =
                ContloPreference.getInstance(getContext()).getPushConsent()

            val params = Gson().toJson(audience.processAudience(), ContloAudience::class.java)
            ContloUtils.printLog(Contlo.getContext(), TAG, "Send User Data Params: $params")

            CoroutineScope(Dispatchers.IO).launch {

                val res = ContloApiService.sendUserData(getContext(), params)
                when (res) {
                    is Resource.Error -> {
                        ContloUtils.printLog(Contlo.getContext(),
                            TAG,
                            "Send User Data Response: ${res.error?.localizedMessage}"
                        )
                    }
                    is Resource.Success -> {
                        audience.userEmail?.let {
                            ContloPreference.getInstance(getContext())
                                .setEmail(it)
                        }
                        audience.userPhone?.let {
                            ContloPreference.getInstance(getContext())
                                .setPhoneNumber(it)
                        }
                        ContloUtils.printLog(Contlo.getContext(), TAG, "Send User Data Response: ${res.data}")
                    }
                }
            }
        }

        fun logoutUser() {
            ContloPreference.getInstance(getContext()).clearData()
        }
    }
}