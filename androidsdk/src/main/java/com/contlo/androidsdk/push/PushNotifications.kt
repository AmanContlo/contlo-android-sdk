package com.contlo.androidsdk.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.contlo.contlosdk.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL


class PushNotifications() : FirebaseMessagingService() {

    private var apiKey: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val notification = remoteMessage.notification
        val title = notification?.title
        val message = notification?.body
        //val imageUrl = notification?.imageUrl?.toString()
        val deepLink = remoteMessage.data["deep_link"]

        if (title != null && message != null ) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelId = "contlo_channel_id"
            val channelName = "Contlo Channel"
            val description = "Contlo Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, importance)
                channel.description = description
                notificationManager.createNotificationChannel(channel)

                channel.description = description
                // Configure the notification channel with sound and vibration
                channel.enableVibration(true)
                channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)

            }

            val intent = Intent(this, PushNotificationHandlerActivity::class.java)
//            intent.putExtra("apikey", apiKey)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)


            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle("SDK Testing")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(com.google.android.material.R.drawable.ic_arrow_back_black_24)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Set the notification channel for Android Oreo and higher
                notificationBuilder.setChannelId(channelId)
            }

            notificationManager.notify(0, notificationBuilder.build())

//            Thread {
//                val notificationImage = loadImage(imageUrl)
//                if (notificationImage != null) {
//                    notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(notificationImage))
//                }

//            }.start()


            if (deepLink != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val pendingIntent1 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE )
                notificationBuilder.setContentIntent(pendingIntent1)
            }

        }
    }



    //Function to load Image in Notification
    private fun loadImage(imageUrl: String): Bitmap? {
        try {
            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        TODO("Not Yet Implemented")

    }


}
