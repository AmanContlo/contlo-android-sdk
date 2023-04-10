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


class ContloPermissions(private val context: Context) {

//
//    private val phonePermissionLauncher: ActivityResultLauncher<String> =
//        activityResultRegistry.register(
//            "phone permission",
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                Log.d("Phone Permission", "Permssion Granted")
//
//
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)   {
//
//                    Log.d("Location Permission", "Already Granted")
//
//                } else {
//
//                    Log.d("Location Permission", "Requesting Permssion")
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//                }
//
//
//            } else {
//
//                Log.d("Phone Permission", "Permssion Denied")
//
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)   {
//
//                    Log.d("Location Permission", "Already Granted")
//
//                } else {
//                    Log.d("Location Permission", "Requesting Permssion")
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
//                Log.d("Push Notification Permission", "Permission Granted")
//            } else {
//
//                Log.d("Push Notification Permission", "Permission Denied")
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
//                Log.d("Location Permission", "Permission Granted")
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    if (ContextCompat.checkSelfPermission(
//                            context, Manifest.permission.POST_NOTIFICATIONS
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//
//                        Log.d("Push Notification Permission", "Already Granted")
//
//                    } else {
//                        // Permission not granted, request the permission
//                        Log.d("Push Notification Permission", "Requesting Permission")
//                        pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                } else {
//                    Log.d("Push Notification Permission", "Android Level is 12 or less")
//                }
//
//
//            } else {
//
//                Log.d("Location Permission", "Permission Denied")
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    if (ContextCompat.checkSelfPermission(
//                            context, Manifest.permission.POST_NOTIFICATIONS
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//
//                        Log.d("Push Notification Permission", "Already Granted")
//
//                    } else {
//                        // Permission not granted, request the permission
//                        Log.d("Push Notification Permission", "Requesting Permission")
//                        pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                } else {
//                    Log.d("Push Notification Permission", "Android Level is 12 or less")
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
//                Log.d("Push Notification Permission", "Already Granted")
//
//            } else {
//                // Permission not granted, request the permission
//                Log.d("Push Notification Permission", "Requesting Permission")
//                pushPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        } else {
//            Log.d("Push Notification Permission", "Android Level is 12 or less")
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


        if(consent){

            if(sharedPreferences.contains("Already Subscribed")){

                //Do Nothing

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                editor.putString("Already Subscribed",null)
                editor.remove("Already Unsubscribed")
                editor.apply()
                TODO("Hit Subscribe API")

            }

            else{

                editor.putString("Already Subscribed",null)
                editor.apply()
                TODO("Hit Subscribe API")

            }

        }

        else if(!consent){

            if(sharedPreferences.contains("Already Subscribed")){

                editor.putString("Already Unsubscribed",null)
                editor.remove("Already Subscribed")
                editor.apply()
                TODO("Hit Unsubscribe API")

            }

            else if(sharedPreferences.contains("Already Unsubscribed")){

                //Do Nothing

            }

            else{

                editor.putString("Already Unsubscribed",null)
                editor.apply()
                TODO("Hit Unsubscribe API")

            }

        }

    }









}
