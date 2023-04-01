package com.contlo.mobilesdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.contlo.androidsdk.ContloSDK
import com.contlo.androidsdk.UserProfile.ContloAudience
import com.contlo.androidsdk.permissions.RequestPermissions
import com.contlo.androidsdk.push.PushNotifications


class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button

    private lateinit var login: Button

    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText

    private lateinit var firstname: String
    private lateinit var email: String
    private lateinit var password: String

    private lateinit var push: PushNotifications
    private lateinit var contloAudience: ContloAudience

    private lateinit var multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestPermissions = RequestPermissions(applicationContext, activityResultRegistry)

        requestPermissions.requestContloPermissions()

        val contloSDK = ContloSDK()

       contloSDK.init(applicationContext, null, null)

        contloAudience = ContloAudience(applicationContext)



    }

    private fun onMultiplePermissionsResult(grantedPermissions: List<String>) {
        // Check if all permissions were granted
        if (grantedPermissions.size == 3) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions were not granted", Toast.LENGTH_SHORT).show()
        }
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

    fun manparams(view: View){

        contloAudience.printparams()


    }


    companion object {
        private const val TAG = "FCM Token - Client Side"
    }



}