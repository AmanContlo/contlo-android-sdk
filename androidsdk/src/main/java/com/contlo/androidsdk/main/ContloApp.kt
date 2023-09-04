package com.contlo.androidsdk.main

import android.app.Application
import android.os.Bundle
import com.contlo.androidsdk.lifecycle.ContloSDKLifecycleCallbacks


class ContloApp : Application() {

    var pendingIntentExtras: Bundle? = null

    override fun onCreate() {
        super.onCreate()
        appContext = this
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks(applicationContext))
    }
    companion object {
        lateinit var appContext: ContloApp
            private set
    }
}
