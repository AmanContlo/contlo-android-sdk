package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.contlo.androidsdk.UserProfile.ContloAudience


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
        btn4 = findViewById(R.id.button4)
        btn5 = findViewById(R.id.button5)
        btn6 = findViewById(R.id.button6)

        btn1.setOnClickListener {



        }

        btn2.setOnClickListener {



        }

        btn3.setOnClickListener {



        }

        btn4.setOnClickListener {



        }

        btn5.setOnClickListener {



        }

        btn6.setOnClickListener {



        }



        }

    }
