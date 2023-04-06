package com.contlo.androidsdk.push

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.contlo.androidsdk.api.ContloAPI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL


class PushNotifications() : FirebaseMessagingService() {


    private var apiKey: String? = null

     var  messageReceived: String? = null


    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("Contlo-Push","Message Received")

        val context1 = this

        //Get Notification and payload
        val title = remoteMessage.data["title"]                         //Title
        val message = remoteMessage.data["body"]                        //Body
        val subtitle = remoteMessage.data["subtitle"]                   //Subtitle
        val imageUrl = remoteMessage.data["image"]                     //Large Image
        val deepLink = remoteMessage.data["primary_url"]                //Notification Deep Link
        val internalID = remoteMessage.data["internal_id"]              //Internal ID
        val ctatitle1 = remoteMessage.data["cta_title_1"]                 //Button 1 Title
        val ctalink1 = remoteMessage.data["cta_link_1"]                   //Button 1 Link
        val ctatitle2 = remoteMessage.data["cta_title_2"]                 //Button 2 Title
        val ctalink2 = remoteMessage.data["cta_link_2"]                   //Button 2 Link


        val x = ContloAPI()
        if (internalID != null) {
            x.sendPushCallbacks(this,"received", internalID)
        }


        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        apiKey = sharedPreferences.getString("API_KEY",null)


        Log.d("REMOTE", remoteMessage.data.toString())

        //Get the app's icon and set as small icon
        val appIcon = this.packageManager.getApplicationIcon(this.packageName)
        val appIconBitmap = (appIcon as BitmapDrawable).bitmap
        val appIconCompat = IconCompat.createWithBitmap(appIconBitmap)


        //Title is compulsory to create a notification
        if (title != null && title != "" ) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //FCM Channel
            val channelId = "contlo_channel_id"
            val channelName = "Contlo Channel"
            val description = "Contlo Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Create a notification channel if Android Level > Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Log.d("Contlo-Push","Creating Notification Channel")
                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = description
                notificationManager.createNotificationChannel(channel)

                // Configure the notification channel with sound and vibration
                channel.enableVibration(true)
                channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
            }

            val deleteIntent = Intent(this, NotificationDeleteReceiver::class.java)
            deleteIntent.putExtra("internal_id", internalID)
            deleteIntent.action = "com.contlo.androidsdk.DELETE_NOTIFICATION"
            val deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


            //Create the notification
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(appIconCompat)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setDeleteIntent(deletePendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)


            //Set Subtitle if not null
            if(subtitle != null)
            {
                notificationBuilder.setSubText(subtitle)
            }

            //CTA Button 1
            if(ctatitle1 != null && ctatitle1 != "")
            {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setPackage(null) // This line sets the package name to null, which allows any app to handle the intent

                val pendingIntent = PendingIntent.getActivity(context1, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                //CTA Button 1 with Deep Link
                if(ctalink1 != null && ctalink1 == ""){
                    val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(ctalink1))
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val pendingIntent1 = PendingIntent.getActivity(
                        this,
                        0,
                        intent1,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    notificationBuilder.addAction(0, ctatitle1, pendingIntent1)
                }

                else{
                    notificationBuilder.addAction(0, ctatitle1, pendingIntent )
                }


            }

            //CTA Button 2
            if(ctatitle2 != null && ctatitle2 != "")
            {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setPackage(null) // This line sets the package name to null, which allows any app to handle the intent

                val pendingIntent = PendingIntent.getActivity(context1, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                //CTA Button 2 with Deep Link
                if(ctalink2 != null && ctalink2 == ""){
                    val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(ctalink2))
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val pendingIntent1 = PendingIntent.getActivity(
                        this,
                        0,
                        intent1,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    notificationBuilder.addAction(0, ctatitle2, pendingIntent1)
                }

                else{
                    notificationBuilder.addAction(0, ctatitle2, pendingIntent )
                }
            }


            //Register Notification Click
            CoroutineScope(Dispatchers.IO).launch {

                Log.d("Contlo-Push","Registering Notification Click")

                    val clickIntent = Intent(context1, PushClicked::class.java)
                    clickIntent.putExtra("internal_id", internalID)
                    val pendingIntent = PendingIntent.getService(
                        context1,
                        0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    notificationBuilder.setContentIntent(pendingIntent)

                }


            //Set Deep Link for the notification
            if (deepLink != null) {

                Log.d("Contlo-Push","Setting Deep Link")

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val pendingIntent1 = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                notificationBuilder.setContentIntent(pendingIntent1)
            }


            // Set the notification channel for Android Oreo and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(channelId)
            }



            if (imageUrl != null) {

                Log.d("Contlo-Push","Loading large image and preview")

                // Load large image
                CoroutineScope(Dispatchers.IO).launch {
                        val largeImage = loadImage(imageUrl)
                        if (largeImage != null) {
                            notificationBuilder.setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(largeImage)
                                    .bigLargeIcon(null)
                            )

                        }
                    notificationManager.notify(0, notificationBuilder.build())

                    }


                // Load large Icon
                CoroutineScope(Dispatchers.IO).launch {

                    val largeImage = loadImage(imageUrl)
                    if (largeImage != null) {
                        notificationBuilder.setLargeIcon(largeImage)
                    }
                    notificationManager.notify(0, notificationBuilder.build())

                }

            }
            else {
                notificationManager.notify(0, notificationBuilder.build())
            }


        }
    }



    //Helper Function to load Image in Notification
    private fun loadImage(imageUrl: String?): Bitmap? {

        try {

            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.d("Contlo-Push","Error in Loading Image")
            e.printStackTrace()
        }
        return null
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("Contlo-onNewToken", "true")

    }









}
