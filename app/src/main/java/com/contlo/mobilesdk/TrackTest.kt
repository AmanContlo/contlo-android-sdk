package com.contlo.mobilesdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.contlo.contlosdk.TrackAPI

class TrackTest : AppCompatActivity() {

    //Declarations
    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText

    lateinit var a1: String
    lateinit var a2: String
    lateinit var a3: String

    private lateinit var btn: Button
    private lateinit var t: TrackAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_test)

        //Initializations
        et1 = findViewById(R.id.editText0)
        et2 = findViewById(R.id.editText1)
        et3 = findViewById(R.id.editText2)
        btn = findViewById(R.id.button)

        //Creating Instance of the Track Class
        t  = TrackAPI(applicationContext)

        //Setting Button onClickListeners
        btn.setOnClickListener {

            //Retrieving Text from edittexts on button click
            a1 = et1.text.toString()
            a2 = et2.text.toString()
            a3 = et3.text.toString()

            //Calling sendRequest function from CONTLOSDK
            t.sendRequest(a1,a2,a3)


        }




    }
}