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
import com.contlo.androidsdk.main.SDKApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


class NotificationHandler() : FirebaseMessagingService() {

    private var apiKey: String? = null
    private val bitmap: Bitmap? = null

    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("Contlo-Notification","Push Received")

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

        val contloAPI = ContloAPI(applicationContext)
        if (internalID != null) {
            contloAPI.sendPushCallbacks("received", internalID)
        }

        val sharedPreferences = this.getSharedPreferences("contlosdk", Context.MODE_PRIVATE)
        apiKey = sharedPreferences.getString("API_KEY",null)

        Log.d("Contlo-Push-Payload", remoteMessage.data.toString())

        //Get the app's icon and set as small icon
        val appIcon = this.packageManager.getApplicationIcon(this.packageName)
        val appIconBitmap = (appIcon as BitmapDrawable).bitmap
        val appIconCompat = IconCompat.createWithBitmap(appIconBitmap)

        //Title is compulsory to create a notification
        if (!title.isNullOrBlank()) {

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "contlo_channel_id"
            createNotificationChannel(notificationManager)

            val deletePendingIntent = createDeleteIntent(internalID)

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
            if (!subtitle.isNullOrBlank()) { notificationBuilder.setSubText(subtitle) }

            val defaultPendingIntent = createDefaultIntent()

            //CTA Button 1
            if(!ctatitle1.isNullOrBlank())
            {
                //CTA Button 1 with Deep Link
                if(!ctalink1.isNullOrBlank()){
                    val btnClickPendingIntent = createNotificationClickIntent(ctalink1, internalID, context1)
                    notificationBuilder.addAction(0, ctatitle1, btnClickPendingIntent)
                }
                else{
                    notificationBuilder.addAction(0, ctatitle1, defaultPendingIntent )
                }
            }

            //CTA Button 2
            if(!ctatitle2.isNullOrBlank())
            {
                //CTA Button 2 with Deep Link
                if(!ctalink2.isNullOrBlank()){
                    val btnClickPendingIntent = createNotificationClickIntent(ctalink2, internalID, context1)
                    notificationBuilder.addAction(0, ctatitle2, btnClickPendingIntent)
                }
                else{
                    notificationBuilder.addAction(0, ctatitle2, defaultPendingIntent )
                }
            }

            //Register Notification Click
            CoroutineScope(Dispatchers.IO).launch{

                if(!deepLink.isNullOrBlank()){
                    val pendingIntent = createNotificationClickIntent(deepLink, internalID, context1)
                    notificationBuilder.setContentIntent(pendingIntent)
                }
                else{
                    notificationBuilder.setContentIntent(defaultPendingIntent)
                }
            }

            // Set the notification channel for Android Oreo and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { notificationBuilder.setChannelId(channelId) }

            if (imageUrl != null) {

                // Load large image and icon
                CoroutineScope(Dispatchers.IO).launch {
                    val largeImage = loadImage(imageUrl)
                    if (largeImage != null) {
                        notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(largeImage)
                            .bigLargeIcon(bitmap))

                        notificationBuilder.setLargeIcon(largeImage)
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }
            }
            else { notificationManager.notify(0, notificationBuilder.build()) }
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

    private fun createNotificationChannel(notificationManager: NotificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Contlo-Push", "Creating Notification Channel")

            //FCM Channel
            val channelId = "contlo_channel_id"
            val channelName = "Contlo Channel"
            val description = "Contlo Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

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
    }

    private fun createNotificationClickIntent(ctalink: String?, internalID: String?, context: Context): PendingIntent? {

        val clickIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ctalink))
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        clickIntent.putExtra("internal_id", internalID)
        clickIntent.putExtra("notification_clicked",true)

        val app = context.applicationContext as SDKApplication
        app.pendingIntentExtras = clickIntent.extras

        return PendingIntent.getActivity(
            context,
            Random.nextInt(0,100),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDeleteIntent(internalID: String?): PendingIntent {

        val deleteIntent = Intent(this, NotificationDismissReceiver::class.java)
        deleteIntent.putExtra("internal_id", internalID)
        deleteIntent.action = "com.contlo.androidsdk.DELETE_NOTIFICATION"
        return PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createDefaultIntent(): PendingIntent {
        val packageManager = applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Contlo-onNewToken", "true")
    }









}
