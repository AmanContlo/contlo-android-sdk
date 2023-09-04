package com.contlo.androidsdk.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.contlo.androidsdk.utils.Constants.AD_ID
import com.contlo.androidsdk.utils.Constants.AD_ID_FCM_FOUND
import com.contlo.androidsdk.utils.Constants.AD_ID_FCM_NOT_FOUND
import com.contlo.androidsdk.utils.Constants.API_KEY
import com.contlo.androidsdk.utils.Constants.APP_NAME
import com.contlo.androidsdk.utils.Constants.EMAIL
import com.contlo.androidsdk.utils.Constants.FCM_TOKEN
import com.contlo.androidsdk.utils.Constants.MOBILE_PUSH_CONSENT
import com.contlo.androidsdk.utils.Constants.NEW_APP_INSTALL
import com.contlo.androidsdk.utils.Constants.PACKAGE_NAME
import com.contlo.androidsdk.utils.Constants.PHONE_NUMBER
import com.contlo.androidsdk.utils.Constants.PREFERENCE_NAME
import com.contlo.androidsdk.utils.Constants.PUSH_CONSENT_FCM_FOUND
import com.contlo.androidsdk.utils.Constants.PUSH_CONSENT_FCM_NOT_FOUND

class ContloPreference() {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private var sInstance: ContloPreference? = null
        fun getInstance(context: Context): ContloPreference {
            if (sInstance == null) {
                sInstance = ContloPreference(context.applicationContext)
            }
            return sInstance as ContloPreference
        }
    }

    constructor(context: Context) : this() {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
    }

    fun getApiKey() = sharedPreferences.getString(API_KEY, "")
    fun setApiKey(apiKey: String) = sharedPreferences.edit()?.putString(API_KEY, apiKey)?.apply()

    fun getFcmKey() = sharedPreferences.getString(FCM_TOKEN, "")
    fun setFcmKey(fcmKey: String) = sharedPreferences.edit()?.putString(FCM_TOKEN, fcmKey)?.apply()

    fun getPushConsent() = sharedPreferences.getBoolean(MOBILE_PUSH_CONSENT, false)
    fun setPushConsent(consent: Boolean) = sharedPreferences.edit()?.putBoolean(MOBILE_PUSH_CONSENT, consent)?.apply()

    //const val PACKAGE_NAME = "PACKAGE_NAME"
    //    const val APP_NAME = "APP_NAME"
    //    const val APP_VERSION = "APP_VERSION"
    //    const val MODEL_NAME = "MODEL_NAME"
    //    const val MANUFACTURER = "MANUFACTURER"
    //    const val NETWORK_TYPE = "NETWORK_TYPE"
    //    const val EXTERNAL_ID = "EXTERNAL_ID"
    //    const val API_LEVEL = "API_LEVEL"
    //    const val OS_TYPE = "OS_TYPE"
    //    const val SOURCE = "SOURCE"
    //    const val SDK_VERSION = "SDK_VERSION"

    fun getPackageName() = sharedPreferences.getString(PACKAGE_NAME, "")
    fun setPackageName(packageName: String) = sharedPreferences.edit()?.putString(PACKAGE_NAME, packageName)?.apply()

    fun getAppName() = sharedPreferences.getString(APP_NAME, "")
    fun setAppName(appName: String) = sharedPreferences.edit()?.putString(APP_NAME, appName)?.apply()

    fun getEmail() = sharedPreferences.getString(EMAIL, "")
    fun setEmail(appName: String) = sharedPreferences.edit()?.putString(EMAIL, appName)?.apply()

    fun getPhoneNumber() = sharedPreferences.getString(PHONE_NUMBER, "")
    fun setPhoneNumber(appName: String) = sharedPreferences.edit()?.putString(PHONE_NUMBER, appName)?.apply()
    fun isNewAppInstall() = sharedPreferences.getBoolean(NEW_APP_INSTALL, false)
    fun setNewAppInstall() = sharedPreferences.edit()?.putBoolean(NEW_APP_INSTALL, true)?.apply()
    fun isFcmFound() = sharedPreferences.getBoolean(AD_ID_FCM_FOUND, false)
    fun setFcmFound() = sharedPreferences.edit()?.putBoolean(AD_ID_FCM_FOUND, true)?.apply()
    fun isPushConsentFound() = sharedPreferences.getBoolean(PUSH_CONSENT_FCM_FOUND, false)
    fun setPushConsent() = sharedPreferences.edit()?.putBoolean(PUSH_CONSENT_FCM_FOUND, true)?.apply()

//    fun getAppVersion() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
//
//    fun getAdvertisingId() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
//
//    fun getAdvertisingId() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
//
//    fun getAdvertisingId() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
//
//    fun getAdvertisingId() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
//
//    fun getAdvertisingId() = sharedPreferences.getString(AD_ID, "")
//    fun setAdvertisingId(adId: String) = sharedPreferences.edit()?.putString(AD_ID, adId)?.apply()
}