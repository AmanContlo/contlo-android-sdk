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

    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText

    private lateinit var firstname: String
    private lateinit var email: String
    private lateinit var password: String

    private lateinit var contloAudience: ContloAudience


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val abc = ContloPermissions()
        abc.sendPushConsent(applicationContext,true)

        val contloSDK = ContloSDK()

       contloSDK.init(applicationContext)

        contloAudience = ContloAudience(applicationContext)



    }

    fun login(view: View){

        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)

        firstname = et1.text.toString()
        email = et2.text.toString()
        password = et3.text.toString()

        contloAudience.setUserFirstName(firstname)
        contloAudience.setUserEmail(email)
        contloAudience.setUserAttribute("Password",password)
        contloAudience.sendUserDatatoContlo()

        val intent = Intent(applicationContext,TestEvents::class.java)
        startActivity(intent)


    }





}