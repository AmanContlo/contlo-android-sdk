package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.contlo.androidsdk.UserProfile.ContloAudience
import com.contlo.androidsdk.api.TrackAPI


class TestEvents : AppCompatActivity() {

    private lateinit var contloAudience: ContloAudience

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_test)


        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)



        btn1.setOnClickListener {


            val x = TrackAPI()
            x.sendMobileEvents(applicationContext,"mobile_app_updated","1.0.0", "android", "email")

        }

        btn2.setOnClickListener {


            val x = TrackAPI()
            x.sendevent2(applicationContext,"mobile_push_settings_clicked")

        }

        btn3.setOnClickListener {


            val x = TrackAPI()
            x.sendevent2(applicationContext,"mobile_push_settings_opened")


        }






        }

    }
