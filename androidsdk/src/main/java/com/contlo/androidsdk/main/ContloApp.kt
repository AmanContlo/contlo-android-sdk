package com.contlo.androidsdk.main

import android.app.Application
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.lifecycle.ContloSDKLifecycleCallbacks


/**
 * @author Aman
 * This Application class is not used. It can be used by clients who do not have an application class
 * To use this, add this to AndroidManifest
 * There can only be 1 instance of Application class
 **/
class ContloApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Contlo.init(this)
        registerActivityLifecycleCallbacks(ContloSDKLifecycleCallbacks(applicationContext))
    }
}
