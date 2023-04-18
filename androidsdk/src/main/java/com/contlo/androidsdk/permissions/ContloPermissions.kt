package com.contlo.androidsdk.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


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
//                TODO("Hit Subscribe API")

            }

            else{

                editor.putString("Already Subscribed","1")
                editor.apply()
//                TODO("Hit Subscribe API")

            }

        }

        else if(!consent){

            if(sharedPreferences.contains("Already Subscribed")){

                editor.putString("Already Unsubscribed","1")
                editor.remove("Already Subscribed")
                editor.apply()
//                TODO("Hit Unsubscribe API")

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                //Do Nothing

            }

            else{

                editor.putString("Already Unsubscribed","1")
                editor.apply()
//                TODO("Hit Unsubscribe API")

            }

        }

    }









}
