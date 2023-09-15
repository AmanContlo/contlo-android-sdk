package com.contlo.androidsdk.push

import android.annotation.SuppressLint
import com.contlo.androidsdk.utils.ContloUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*

class NotificationHandler() : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        ContloNotification.processContloNotification(this, remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ContloUtils.printLog(this, "Contlo-onNewToken", "true")
        ContloNotification.updateFcmToken(this, token)
    }
}
