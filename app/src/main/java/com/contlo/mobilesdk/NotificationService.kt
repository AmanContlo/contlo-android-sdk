package com.contlo.mobilesdk

import com.contlo.androidsdk.push.ContloNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService(): FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        ContloNotification.processContloNotification(this, remoteMessage = message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}