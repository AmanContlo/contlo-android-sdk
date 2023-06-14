package com.contlo.androidsdk.permissions

import android.content.Context
import android.util.Log
import com.contlo.androidsdk.api.HttpClient
import com.contlo.contlosdk.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.HashMap


class ContloPermissions() {

//
//    private val phonePermissionLauncher: ActivityResultLauncher<String> =
//        activityResultRegistry.register(
//            "phone permission",
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//
//
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)   {
//
//
//                } else {
//
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//                }
//
//
//            } else {
//
//
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)   {
//
//
//                } else {
//
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//                }
//
//
//            }
//        }
//
//    private val pushPermissionLauncher: ActivityResultLauncher<String> =
//        activityResultRegistry.register(
//            "push permission",
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//            } else {
//
//
//            }
//        }


//
//    private val locationPermissionLauncher: ActivityResultLauncher<String> =
//        activityResultRegistry.register(
//            "location permission",
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    if (ContextCompat.checkSelfPermission(
//                            context, Manifest.permission.POST_NOTIFICATIONS
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//
//
//                    } else {
//                        // Permission not granted, request the permission
//                        pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                } else {
//                }
//
//
//            } else {
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    if (ContextCompat.checkSelfPermission(
//                            context, Manifest.permission.POST_NOTIFICATIONS
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//
//
//                    } else {
//                        // Permission not granted, request the permission
//                        pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                } else {
//                }
//
//
//            }
//        }


//
//    fun requestContloPermissions() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    context, Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//
//
//            } else {
//                // Permission not granted, request the permission
//                pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        } else {
//        }
//
//
//    }

////    fun requestListenerPermission(){
////
////        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
////        val packageName = sharedPreferences.getString("PACKAGE_NAME", null)
////        val editor = sharedPreferences.edit()
////
////
////
////        if (NotificationManagerCompat.getEnabledListenerPackages(context)
////                .contains(packageName)) {
////
////            TODO()
////
////        }
////        else
////        {
////
////            val builder = AlertDialog.Builder(context)
////            builder.setMessage("Do you want to grant permission for listening to notifications?")
////                .setPositiveButton("Yes") { dialog, which ->
////                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
////                    startActivity(context, intent,null)
////                }
////                .setNegativeButton("No") { dialog, which ->
////
////                }
////                .show()
////        }
////
////
////    }

    private var fcm: String? = null
    private var apiKey: String? = null

   fun sendPushConsent(context: Context,consent : Boolean){

        Log.d("Contlo-Permission", "Sending Push Consent")

        val sharedPreferences  = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("MOBILE_PUSH_CONSENT",consent)
        editor.apply()


        if(consent){

            if(sharedPreferences.contains("Already Subscribed")){

                //Do Nothing

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                editor.putString("Already Subscribed","1")
                editor.remove("Already Unsubscribed")
                editor.apply()
                changeMPConsent(context,true,null,1)

            }

            else{

                editor.putString("Already Subscribed","1")
                editor.apply()
                changeMPConsent(context,true,null,1)

            }

        }

        else if(!consent){

            if(sharedPreferences.contains("Already Subscribed")){

                editor.putString("Already Unsubscribed","1")
                editor.remove("Already Subscribed")
                editor.apply()
                changeMPConsent(context,false,null,1)

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                //Do Nothing

            }

            else{

                editor.putString("Already Unsubscribed","1")
                editor.apply()
                changeMPConsent(context,false,null, 1)

            }

        }

    }


    internal fun changeMPConsent(context: Context,consent: Boolean, fcm_token: String?,source: Int){

        //Retrieve fcm and api key
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if(source == 1){

            Log.d("Contlo-Permissions","Changing consent Directly")


            fcm = sharedPreferences.getString("FCM_TOKEN",null)
            if (fcm==null){

                val editor = sharedPreferences.edit()
                editor.putBoolean("PUSH_CONSENT_FCM_NOT_FOUND",true)
                Log.d("Contlo-Permissions","PUSH_CONSENT_FCM_NOT_FOUND")
                editor.apply()
                return
            }

        }
        else if(source == 0){

            Log.d("Contlo-Permissions","Changing consent from onSuccess")

            fcm = fcm_token

        }

        apiKey = sharedPreferences.getString("API_KEY", null)

        //Put FCM and consent in params
        val params = JSONObject()
        params.put("fcm_token", fcm)

        val mobilePushConsent = if (consent) "TRUE" else "FALSE"

        params.put("mobile_push_consent",mobilePushConsent)

        //Make API Request
        val url = context.getString(R.string.registerfcm_url)

        val headers = HashMap<String, String>()
        headers["accept"] = "application/json"
        headers["X-API-KEY"] = "$apiKey"
        headers["content-type"] = "application/json"


        CoroutineScope(Dispatchers.IO).launch {

            Log.d("Contlo-Permission", "Changing Mobile Push Consent to $consent")

            val httpPostRequest = HttpClient()
            val response = httpPostRequest.sendPOSTRequest(url, headers, params)

            Log.d("Contlo-Permission", "Response Changing Mobile Push Consent: $response")

        }


    }

//    private fun sendPushConsent(context:Context){
//
//        //Put FCM and consent in params
//        val params = JSONObject()
//        params.put("fcm_token", fcm)
//
//        val mobilePushConsent = if (consent) "TRUE" else "FALSE"
//
//        params.put("mobile_push_consent",mobilePushConsent)
//
//        //Make API Request
//        val url = context.getString(R.string.registerfcm_url)
//
//        val headers = HashMap<String, String>()
//        headers["accept"] = "application/json"
//        headers["X-API-KEY"] = "$apiKey"
//        headers["content-type"] = "application/json"
//
//    }





}
