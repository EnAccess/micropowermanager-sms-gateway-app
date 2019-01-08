package com.gateway.android.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage

/**
 * A broadcast receiver who listens for incoming SMS
 */

class SmsBroadcastReceiver : BroadcastReceiver() {

    private var listener: Listener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsBody = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsBody += smsMessage.messageBody
                }
            } else {
                val smsBundle = intent.extras
                if (smsBundle != null) {
                    val pdus = smsBundle.get("pdus") as Array<*>

                    val messages = arrayOfNulls<SmsMessage>(pdus.size)

                    for (i in messages.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        smsBody += messages[i]!!.messageBody
                    }
                }
            }

            if (listener != null) {
                listener!!.onTextReceived(smsBody)
            }
        }
    }

    internal fun setListener(listener: Listener) {
        this.listener = listener
    }

    internal interface Listener {
        fun onTextReceived(text: String)
    }
}
