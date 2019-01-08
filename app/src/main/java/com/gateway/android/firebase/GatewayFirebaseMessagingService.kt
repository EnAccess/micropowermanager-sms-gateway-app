package com.gateway.android.firebase

import android.telephony.SmsManager
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
        val number =
            if (remoteMessage != null && remoteMessage.data != null && remoteMessage.data[KEY_NOTIFICATION_EXTRA_NUMBER] != null) {
                remoteMessage.data[KEY_NOTIFICATION_EXTRA_NUMBER]
            } else {
                ""
            }

        val message =
            if (remoteMessage != null && remoteMessage.data != null && remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE] != null) {
                remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE]
            } else {
                ""
            }

        if (number!!.isNotEmpty() && message!!.isNotEmpty()) {
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)
        }
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        Log.d("GatewayFirebaseService", "Refreshed token: " + p0!!)
    }

    companion object {
        private const val KEY_NOTIFICATION_EXTRA_NUMBER = "number"
        private const val KEY_NOTIFICATION_EXTRA_MESSAGE = "message"
    }
}
