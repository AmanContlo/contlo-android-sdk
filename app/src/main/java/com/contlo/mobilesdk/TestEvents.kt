package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.contlo.androidsdk.main.Contlo


class TestEvents : AppCompatActivity() {

    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText
    private lateinit var et4: EditText
    private lateinit var et5: EditText

    private lateinit var btn1: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_test)

        btn1 = findViewById(R.id.button)

        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        et4 = findViewById(R.id.et4)
        et5 = findViewById(R.id.et5)

        btn1.setOnClickListener {

            val event = et1.text.toString()

            val email = et2.text.toString().ifBlank { null }
            val phone = et3.text.toString().ifBlank { null }

            val propKey = et4.text.toString()
            val propValue = et5.text.toString()

//            val prop = JSONObject()
//            prop.put(propKey,propValue)
                val map = HashMap<String, String>()
            map.put(propKey, propValue)
//            contloAPI.sendEvent(event,email,phone,prop,null)
//            val contlo = Contlo()
            Contlo.sendEvent(event, email, phone, map, null)
        }
    }
}
