package com.contlo.androidsdk.utils

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.model.ContloAudience
import com.contlo.androidsdk.utils.Constants.API_LEVEL
import com.contlo.androidsdk.utils.Constants.APP_NAME
import com.contlo.androidsdk.utils.Constants.APP_VERSION
import com.contlo.androidsdk.utils.Constants.DEVICE_EVENT_TIME
import com.contlo.androidsdk.utils.Constants.MANUFACTURER
import com.contlo.androidsdk.utils.Constants.MODEL_NAME
import com.contlo.androidsdk.utils.Constants.NETWORK_TYPE
import com.contlo.androidsdk.utils.Constants.OS_TYPE
import com.contlo.androidsdk.utils.Constants.OS_VERSION
import com.contlo.androidsdk.utils.Constants.PACKAGE_NAME
import com.contlo.androidsdk.utils.Constants.SDK_VERSION
import com.contlo.androidsdk.utils.Constants.SOURCE
import com.contlo.androidsdk.utils.Constants.TIMEZONE
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object ContloUtils {
    const val TAG = "ContloUtils"

    fun isDebugMode(context: Context): Boolean {
        return 0 != context.getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE
    }

    fun printLog(context: Context, TAG: String, data: String) {
        if(isDebugMode(context)) {
            Log.i(TAG, data)
        }
    }

    fun printLog(TAG: String, data: String) {
        Log.i(TAG, data)
    }

    fun retrieveCurrentUser(): ContloAudience =
        ContloAudience(
            userEmail = ContloPreference.getInstance(Contlo.getContext()).getEmail(),
            userPhone = ContloPreference.getInstance(Contlo.getContext()).getPhoneNumber(),
            firebaseToken = ContloPreference.getInstance(Contlo.getContext()).getFcmKey(),
            contloApiKey = ContloPreference.getInstance(Contlo.getContext()).getApiKey()
        )

     fun retrieveEventData(): HashMap<String, String> {

        val packageManager = Contlo.getContext().packageManager
        val applicationInfo = Contlo.getContext().applicationInfo
        val connectivityManager = Contlo.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val packageName = Contlo.getContext().packageName
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val appVersion = packageManager.getPackageInfo(packageName.toString(), 0).versionName
        val osVersion = Build.VERSION.RELEASE
        val apiLevel = Build.VERSION.SDK_INT.toString()
        val modelName = Build.MODEL
        val manufacturer = Build.MANUFACTURER
         val source = ContloPreference.getInstance(Contlo.getContext()).getSource()

        val networkType =
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                "WiFi"
            } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                "Mobile data"
            } else {
                "Unknown"
            }
        val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val date = formatter.parse(Date().toString())
         val map = HashMap<String, String>()

         map.apply {
             put(APP_NAME, appName)
             put(APP_VERSION, appVersion)
             put(PACKAGE_NAME, packageName)
             put(OS_VERSION, osVersion)
             put(API_LEVEL, apiLevel)
             put(MODEL_NAME, modelName)
             put(MANUFACTURER, manufacturer)
             put(NETWORK_TYPE, networkType)
             put(SOURCE, source?:"Android Native")
             put(SDK_VERSION, "1.0.0")
             put(DEVICE_EVENT_TIME, ((date?.time ?: 0) / 1000).toString())
             put(TIMEZONE, TimeZone.getDefault().id.toString())
             put(OS_TYPE, "Android")
         }
         return map
    }

     fun getAPIKey(apiKey: String?): String? {
        ContloUtils.printLog(Contlo.getContext(), TAG, "Fetching API-KEY")
        if(!apiKey.isNullOrEmpty()) {
            return apiKey
        }
         return try {
             val appInfo = Contlo.getContext().packageManager.getApplicationInfo(Contlo.getContext().packageName, PackageManager.GET_META_DATA)
             val metaData = appInfo.metaData
             metaData?.getString("contlo_api_key")
         } catch (e: PackageManager.NameNotFoundException) {
             null
         }
    }

    fun getMetaDataValue(context: Context, metaDataName: String?): String? {
        try {
            val packageManager: PackageManager =
                context.packageManager
            val ai = packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            if (ai.metaData != null) {
                return ai.metaData.getString(metaDataName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    fun isNotificationPermissionGiven(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(Contlo.getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    fun generateFCM(
        onSuccess: (token: String) -> Unit,
        onError: (Exception) -> Unit,
        retryCount: Int = 0
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

            // Retry Mechanism
            if (!task.isSuccessful) {
                ContloUtils.printLog(Contlo.getContext(), TAG, "FCM Task Unsuccessful")
                if (retryCount < 2) {
                    // Retry FCM registration up to 2 times
                    ContloUtils.printLog(Contlo.getContext(), TAG, "Retrying to generate FCM")
                } else {
                    onError(task.exception ?: Exception("Unknown error"))
                }
                return@addOnCompleteListener
            }
            onSuccess(task.result)
        }
    }
}