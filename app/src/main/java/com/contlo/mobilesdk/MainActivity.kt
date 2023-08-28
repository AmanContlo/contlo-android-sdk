package com.contlo.mobilesdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.contlo.androidsdk.main.ContloSDK
import com.contlo.androidsdk.UserProfile.ContloAudience
import com.contlo.androidsdk.permissions.ContloPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var contloSDK: ContloSDK
    private lateinit var contloPermissions: ContloPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contloSDK = ContloSDK()
        contloSDK.init(applicationContext)

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

        contloSDK.trackAdId(applicationContext,true)

    }

    fun subscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,true)

    }

    fun unsubscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,false)

    }





}