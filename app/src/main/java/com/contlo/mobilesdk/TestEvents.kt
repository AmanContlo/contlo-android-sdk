package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.contlo.androidsdk.api.ContloAPI
import com.contlo.androidsdk.main.ContloSDK
import com.contlo.androidsdk.permissions.ContloPermissions
import org.json.JSONObject


class TestEvents : AppCompatActivity() {


    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    private lateinit var btn4: Button
    private lateinit var btn5: Button

    private lateinit var x: ContloAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_test)

        x = ContloAPI(applicationContext)

        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)
        btn4 = findViewById(R.id.button4)
        btn5 = findViewById(R.id.button5)



        btn1.setOnClickListener {

            val prop = JSONObject()
            x.sendEvent("mobile_app_updated",prop)

        }

        btn2.setOnClickListener {

            val prop = JSONObject()
            x.sendEvent("mobile_settings_clicked",prop)

        }

        btn3.setOnClickListener {

            val prop = JSONObject()
            x.sendEvent("mobile_profile_clicked",prop)

        }

        btn4.setOnClickListener {

            val abc = ContloPermissions()
            abc.sendPushConsent(applicationContext,true)

        }

        btn5.setOnClickListener {

            val abc = ContloPermissions()
            abc.sendPushConsent(applicationContext,false)

        }








        }

    }
