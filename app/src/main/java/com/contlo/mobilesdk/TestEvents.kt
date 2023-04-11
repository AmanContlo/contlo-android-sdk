package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.contlo.androidsdk.ContloSDK
import com.contlo.androidsdk.api.ContloAPI
import org.json.JSONObject


class TestEvents : AppCompatActivity() {


    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_test)


        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)


        btn1.setOnClickListener {

            val x = ContloSDK()
            x.callAppInstallorUpdate("mobile_app_updated")

        }

        btn2.setOnClickListener {

            val prop = JSONObject()

            val x = ContloAPI()
            x.sendUserEvent(applicationContext,"mobile_push_settings_clicked",prop)

        }

        btn3.setOnClickListener {

            val prop = JSONObject()

            val x = ContloAPI()
            x.sendUserEvent(applicationContext,"mobile_push_settings_opened",prop)


        }






        }

    }
