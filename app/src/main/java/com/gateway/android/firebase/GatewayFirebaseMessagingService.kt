package com.gateway.android.firebase

import android.telephony.SmsManager
import android.telephony.TelephonyManager
import com.gateway.android.utils.SharedPreferencesWrapper
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

        if (number!!.isNotEmpty() && message!!.isNotEmpty() && SharedPreferencesWrapper.getInstance().simState == TelephonyManager.SIM_STATE_READY) {
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)

            SharedPreferencesWrapper.getInstance()
                .sentMessageCount = SharedPreferencesWrapper.getInstance().sentMessageCount!! + 1
        } else {
            //TODO Trigger Failed Api Service
        }
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        SharedPreferencesWrapper.getInstance().deviceToken = p0
    }

    companion object {
        private const val KEY_NOTIFICATION_EXTRA_NUMBER = "number"
        private const val KEY_NOTIFICATION_EXTRA_MESSAGE = "message"
    }
}
