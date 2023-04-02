package com.contlo.androidsdk

import android.app.Application

class SDKApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks())
    }
}
