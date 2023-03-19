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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.contlo.contlosdk.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL


class PushNotifications() : FirebaseMessagingService() {


    private var apiKey: String? = null

    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("REMOTE", remoteMessage.notification.toString())
        Log.d("REMOTE", remoteMessage.data.toString())

        //Get the app's icon and set as small icon
        val appIcon = this.packageManager.getApplicationIcon(this.packageName)
        val appIconBitmap = (appIcon as BitmapDrawable).bitmap
        val appIconCompat = IconCompat.createWithBitmap(appIconBitmap)

        //Get Notification and payload
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["body"]
        val subtitle = remoteMessage.data["subtitle"]
        val imageUrl1 = remoteMessage.data["image"]
        val deepLink = remoteMessage.data["primary_url"]
        val internalID = remoteMessage.data["internal_id"]


        val imageUrl: String? = imageUrl1.toString()

        //Log Payload
        Log.d("PayloadTag", "Payload: \n ${title.toString()} \t ${message.toString()} \t ${imageUrl.toString()} \t  ${deepLink.toString()}  \t  ${internalID.toString()}  ")


        if (title != null && message != null) {


            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //FCM Channel
            val channelId = "contlo_channel_id"
            val channelName = "Contlo Channel"
            val description = "Contlo Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Create a notification channel if Android Level > Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

            // Create the intent for the Log in action
            val loginIntent = Intent(this, LoginActivity::class.java)
            val loginPendingIntent = PendingIntent.getActivity(this, 0, loginIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            // Create the intent for the Log out action
            val logoutIntent = Intent(this, LogoutActivity::class.java)
            val logoutPendingIntent = PendingIntent.getActivity(this, 0, logoutIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)



            //Create the notification
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSubText(subtitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSmallIcon(appIconCompat)
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(0, "Log in", loginPendingIntent)
                .addAction(0, "Log out", logoutPendingIntent)

            // Set the notification channel for Android Oreo and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(channelId)
            }

            // Load large image
            if (imageUrl != null) {
                Thread {
                    val largeImage = loadImage(imageUrl)
                    if (largeImage != null) {
                        notificationBuilder.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(largeImage)
                                .bigLargeIcon(null)
                        )
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }.start()
            } else {
                notificationManager.notify(0, notificationBuilder.build())
            }

            //Load Large Icon
            if (imageUrl != null) {
                Thread {
                    val largeImage = loadImage(imageUrl)
                    if (largeImage != null) {
                        notificationBuilder.setLargeIcon(largeImage)
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }.start()
            } else {
                notificationManager.notify(0, notificationBuilder.build())
            }

            //Register Notification Click
                Thread{

                    val clickIntent = Intent(this, PushClicked::class.java)
                    clickIntent.putExtra("internal_id", internalID)
                    val pendingIntent = PendingIntent.getService(
                        this,
                        0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    notificationBuilder.setContentIntent(pendingIntent)

                }.start()


            //Set Deep Link for the notification
            Thread{

                if (deepLink != null) {
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

            }.start()




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
            e.printStackTrace()
        }
        return null
    }


//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//        handler = Handler(Looper.getMainLooper())
//
//        val params = JSONObject()
//        params.put("fcm_token", token)
//
//        println(params.toString())
//
//
//        val url = "https://api.contlo.com/v1/register_mobile_push"
//
//        val headers = HashMap<String, String>()
//        headers["accept"] = "application/json"
//        headers["X-API-KEY"] = "$apiKey"
//        headers["content-type"] = "application/json"
//
//
//        Thread {
//
//            val httpPostRequest = HttpClient()
//            val response = httpPostRequest.sendRequest(url, headers, params, "POST")
//
//            println(response)
//            handler.post {
//                Toast.makeText(context, "Response: $response", Toast.LENGTH_SHORT).show()
//            }
//
//        }.start()
//    }
//
//
//
//    fun getAPIKey(context: Context){
//
//        try {
//            val appInfo = context.packageManager.getApplicationInfo(
//                context.packageName, PackageManager.GET_META_DATA
//            )
//            val metaData = appInfo.metaData
//            apiKey = metaData?.getString("contlo_api_key")
//        } catch (e: PackageManager.NameNotFoundException) {
//            // Handle the exception
//        }
//
//    }



}
