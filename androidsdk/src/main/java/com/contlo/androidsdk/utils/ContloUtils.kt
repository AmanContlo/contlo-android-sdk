package com.contlo.androidsdk.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.contlo.androidsdk.main.ContloApp
import com.contlo.androidsdk.model.EventProperty
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object ContloUtils {

    fun isDebugMode(context: Context): Boolean {
        return 0 != context.getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE
    }

    fun printLog(context: Context, TAG: String, data: String) {
        if(isDebugMode(context)) {
            Log.i(TAG, data)
        }
    }

     fun retrieveEventData(): HashMap<String, String> {

        val packageManager = ContloApp.appContext.packageManager
        val applicationInfo = ContloApp.appContext.applicationInfo
        val connectivityManager = ContloApp.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val packageName = ContloApp.appContext.packageName
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val appVersion = packageManager.getPackageInfo(packageName.toString(), 0).versionName
        val osVersion = Build.VERSION.RELEASE
        val apiLevel = Build.VERSION.SDK_INT.toString()
        val modelName = Build.MODEL
        val manufacturer = Build.MANUFACTURER

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
             put(SOURCE, "Android SDK")
             put(SDK_VERSION, "1.0.0")
             put(DEVICE_EVENT_TIME, ((date?.time ?: 0) / 1000).toString())
             put(TIMEZONE, TimeZone.getDefault().id.toString())
             put(OS_TYPE, "Android")
         }
         return map
    }

     fun getAPIKey(apiKey: String?): String? {
        Log.d("Contlo-Init", "Fetching API-KEY")
        if(apiKey.isNotBlank()) {
            return apiKey
        }
        try {
            val appInfo = ContloApp.appContext.packageManager.getApplicationInfo(ContloApp.appContext.packageName, PackageManager.GET_META_DATA)
            val metaData = appInfo.metaData
            return metaData?.getString("contlo_api_key")
        }
        catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }
}