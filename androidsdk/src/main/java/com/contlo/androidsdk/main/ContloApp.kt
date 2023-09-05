package com.contlo.androidsdk.main

import android.app.Application
import android.os.Bundle
import com.contlo.androidsdk.lifecycle.ContloSDKLifecycleCallbacks


//This Application class is not used
class ContloApp : Application() {

    var pendingIntentExtras: Bundle? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks(applicationContext))
    }
}
