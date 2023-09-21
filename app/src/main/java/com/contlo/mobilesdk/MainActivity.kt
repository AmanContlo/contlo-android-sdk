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
import io.sentry.Sentry


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Contlo.init(application, "e33f4af9ea34b73f18c0fe46d02ed1a2") //prod school
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        Contlo.sendAdvertisingId(true)
    }

    fun subscribe(view : View){
        Contlo.sendPushConsent(true)
        Sentry.captureMessage("from MainActivity")
    }

    fun unsubscribe(view : View){
        Contlo.sendPushConsent(false)
    }
}