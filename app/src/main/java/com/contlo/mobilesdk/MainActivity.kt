package com.contlo.mobilesdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.contlo.androidsdk.main.Contlo
import com.contlo.androidsdk.permissions.ContloPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var contloSDK: Contlo
    private lateinit var contloPermissions: ContloPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        contloSDK = Contlo()
//        contloSDK.init(applicationContext)
//        Contlo.init(application, "60bdc58353bcdc99a0a5dbd7732c3da4") //prod
//        Contlo.init(application, "b7451ff6fc3e1e226d8edfe3b7bd29a6") //prod schoolzilla
        Contlo.init(application, "e33f4af9ea34b73f18c0fe46d02ed1a2") //prod school

        contloPermissions = ContloPermissions()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            preference.setPushConsent(true)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                );

            }
        }
    }


    fun loginScreen(view : View){

        val intent = Intent(applicationContext,LoginActivity::class.java)
        startActivity(intent)

    }

    fun eventScreen(view : View){

        val intent = Intent(applicationContext,TestEvents::class.java)
        startActivity(intent)

    }

    fun trackAdId(view : View){

//        contloSDK.trackAdId(applicationContext,true)
        Contlo.sendAdvertisingId(true)
    }

    fun subscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,true)

    }

    fun unsubscribe(view : View){

        contloPermissions.sendPushConsent(applicationContext,false)

    }





}