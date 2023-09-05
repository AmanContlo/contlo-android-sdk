package com.contlo.mobilesdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.permissions.ContloPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var contloSDK: Contlo
    private lateinit var contloPermissions: ContloPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        contloSDK = Contlo()
//        contloSDK.init(applicationContext)
        contloSDK = Contlo.init(application, "d9fa1a810ce66312beab9f86eaa3480c")
        contloPermissions = ContloPermissions()

    }


    fun loginScreen(view : View){

        val intent = Intent(applicationContext,LoginActivity::class.java)
        startActivity(intent)

    }

    fun eventScreen(view : View){

        val intent = Intent(applicationContext,TestEvents::class.java)
        startActivity(intent)

    }

    fun trackAdId(view : View){

//        contloSDK.trackAdId(applicationContext,true)
        Contlo.sendAdvertisingId(true)
    }

    fun subscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,true)

    }

    fun unsubscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,false)

    }





}