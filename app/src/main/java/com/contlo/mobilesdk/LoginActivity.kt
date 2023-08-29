package com.contlo.mobilesdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.contlo.androidsdk.UserProfile.ContloAudience
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText
    private lateinit var et4: EditText
    private lateinit var et5: EditText

    private lateinit var btn1: Button
    private lateinit var btn2: Button

    private lateinit var contloAudience: ContloAudience

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        et4 = findViewById(R.id.et4)
        et5 = findViewById(R.id.et5)

        btn1 = findViewById(R.id.button)
        btn2 = findViewById(R.id.button1)

        contloAudience = ContloAudience(applicationContext)

        btn1.setOnClickListener { login(false) }

        btn2.setOnClickListener { login(true) }

    }

    fun login(update: Boolean){

        val email = et1.text.toString()
        var phoneNumber = ""

        if(et2.text.toString() != ""){
            phoneNumber = "+91" + et2.text.toString()
        }

        val firstName = et3.text.toString()
        val lastName = et4.text.toString()
        val password = et5.text.toString()

        Log.d("Contlo-DEBUG", phoneNumber)

        if(phoneNumber != ""){
            contloAudience.setUserPhone(phoneNumber)
        }

        if(email != ""){
            contloAudience.setUserEmail(email)
        }

        if(firstName != ""){
            contloAudience.setUserFirstName(firstName)
        }

        if(lastName != ""){
            contloAudience.setUserLastName(lastName)
        }

        val prop = JSONObject()
        prop.put("Password",password)

        contloAudience.setUserAttribute(prop)

        if (update)
            contloAudience.sendUserDatatoContlo(true)
        else
            contloAudience.sendUserDatatoContlo(false)

        Log.d("Contlo-DemoApp", "Sent Details")

        Toast.makeText(applicationContext,"Sent Details",Toast.LENGTH_SHORT).show()


    }

}