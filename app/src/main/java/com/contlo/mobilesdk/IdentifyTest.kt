package com.contlo.mobilesdk


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.contlo.androidsdk.api.IdentifyAPI


class IdentifyTest : AppCompatActivity() {

    //Declarations
    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText
    private lateinit var et4: EditText
    private lateinit var et5: EditText
    private lateinit var et6: EditText
    private lateinit var et7: EditText
    private lateinit var et8: EditText
    private lateinit var et9: EditText

    lateinit var a1: String
    lateinit var a2: String
    lateinit var a3: String
    lateinit var a4: String
    lateinit var a5: String
    lateinit var a6: String
    lateinit var a7: String
    lateinit var a8: String
    lateinit var a9: String
    private lateinit var btn: Button
    private lateinit var  i: IdentifyAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identify_test)

        //Initialization
        et1 = findViewById(R.id.editText0)
        et2 = findViewById(R.id.editText1)
        et3 = findViewById(R.id.editText2)
        et4 = findViewById(R.id.editText3)
        et5 = findViewById(R.id.editText4)
        et6 = findViewById(R.id.editText5)
        et7 = findViewById(R.id.editText6)
        et8 = findViewById(R.id.editText7)
        et9 = findViewById(R.id.editText8)
        btn = findViewById(R.id.button)

        //Creating instance of the identify class
        i = IdentifyAPI(applicationContext)

        //Setting button onClickListener
        btn.setOnClickListener {

            //Retrieving Text from edittexts on button click
            a1 = et1.text.toString()
            a2 = et2.text.toString()
            a3 = et3.text.toString()
            a4 = et4.text.toString()
            a5 = et5.text.toString()
            a6 = et6.text.toString()
            a7 = et7.text.toString()
            a8 = et8.text.toString()
            a9 = et9.text.toString()

            //Calling sendRequest function from CONTLOSDK
            i.sendRequest(a1,a2,a3,a4,a5,a6,a7,a8,a9)

        }

    }
}