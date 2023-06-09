package com.contlo.androidsdk.main

import android.app.Application
import com.contlo.androidsdk.lifecycle.ContloSDKLifecycleCallbacks

class SDKApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks(applicationContext))
    }
}
