package com.contlo.mobilesdk

import android.app.Application
import android.os.Bundle
import com.contlo.androidsdk.lifecycle.ContloSDKLifecycleCallbacks

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks(applicationContext))

    }
}