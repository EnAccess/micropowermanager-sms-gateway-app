package com.gateway.android.firebase

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
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

        val id =
            if (remoteMessage != null && remoteMessage.data != null && remoteMessage.data[KEY_NOTIFICATION_EXTRA_ID] != null) {
                remoteMessage.data[KEY_NOTIFICATION_EXTRA_ID]
            } else {
                ""
            }

        if (number!!.isNotEmpty() && message!!.isNotEmpty() && SharedPreferencesWrapper.getInstance().simState == TelephonyManager.SIM_STATE_READY) {
            SmsManager.getDefault().sendTextMessage(number, null, message, null, null)

            var lastMessageNumber = ""
            var lastMessageBody = ""

            val handler = Handler(Looper.getMainLooper())

            handler.postDelayed({
                val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)

                if (cursor != null && cursor.moveToFirst()) {
                    lastMessageNumber = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                    lastMessageBody = cursor.getString(cursor.getColumnIndexOrThrow("body"))

                    cursor.close()
                }

                if (lastMessageNumber == number && lastMessageBody == message) {
                    //TODO Trigger SmsSent Api Service

                    SharedPreferencesWrapper.getInstance()
                        .sentMessageCount = SharedPreferencesWrapper.getInstance().sentMessageCount!! + 1
                } else {
                    Log.d("Message Information", "$lastMessageNumber $lastMessageBody")
                }
            }, 2000)
        } else {
            //TODO Trigger FailedToSentSms Api Service
        }
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        SharedPreferencesWrapper.getInstance().deviceToken = p0
    }

    companion object {
        private const val KEY_NOTIFICATION_EXTRA_NUMBER = "number"
        private const val KEY_NOTIFICATION_EXTRA_MESSAGE = "message"
        private const val KEY_NOTIFICATION_EXTRA_ID = "id"
    }
}
