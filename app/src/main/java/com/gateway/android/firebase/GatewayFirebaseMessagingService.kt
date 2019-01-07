package com.gateway.android.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GatewayFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        var title: String? = ""

        if (remoteMessage != null && remoteMessage.notification != null && remoteMessage.notification!!.title != null) {
            title = remoteMessage.notification!!.title
        } else if (remoteMessage != null && remoteMessage.data != null && remoteMessage.data[KEY_NOTIFICATION_EXTRA_TITLE] != null) {
            title = remoteMessage.data[KEY_NOTIFICATION_EXTRA_TITLE]
        }

        var message: String? = ""

        if (remoteMessage != null && remoteMessage.notification != null && remoteMessage.notification!!.body != null) {
            message = remoteMessage.notification!!.body
        } else if (remoteMessage != null && remoteMessage.data != null && remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE] != null) {
            message = remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE]
        }
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        Log.d("GatewayFirebaseService", "Refreshed token: " + p0!!)
    }

    companion object {
        private const val KEY_NOTIFICATION_EXTRA_TITLE = "title"
        private const val KEY_NOTIFICATION_EXTRA_MESSAGE = "message"
    }
}
