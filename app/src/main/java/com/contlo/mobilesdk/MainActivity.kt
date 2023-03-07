package com.contlo.mobilesdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.contlo.androidsdk.api.FCMToken
import com.contlo.androidsdk.permissions.RequestPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button


    lateinit var fcmToken: FCMToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestPermissions = RequestPermissions(
            applicationContext,
            activityResultRegistry
        )
        requestPermissions.requestPermission()

        btn = findViewById(R.id.button)
        btn1 = findViewById(R.id.button2)
        btn2 = findViewById(R.id.button3)



        btn.setOnClickListener {
            val intent = Intent(this, IdentifyTest::class.java)
            startActivity(intent)
        }

        btn1.setOnClickListener {
            val intent = Intent(this, TrackTest::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {

            fcmToken =
                FCMToken(applicationContext) // Create an instance of your SDK
            fcmToken.getFCMRegistrationToken { task ->
                if (task.isSuccessful) {
                    // Get the FCM registration token from the task result
                    val token = task.result

                    // Do something with the FCM registration token
                    Log.d(TAG, "FCM registration token: $token")
                    println("FCM registration token - Client: $token")
                } else {
                    // Handle the error
                    val e = task.exception
                    Log.w(TAG, "Fetching FCM registration token failed", e)
                }
            }

        }




    }

    companion object {
        private const val TAG = "FCM Token - Client Side"
    }


}