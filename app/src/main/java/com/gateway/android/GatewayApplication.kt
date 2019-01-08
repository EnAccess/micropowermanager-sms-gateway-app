package com.gateway.android

import android.app.Application
import android.content.IntentFilter
import android.provider.Telephony
import com.gateway.android.sms.SmsBroadcastReceiver
import com.google.firebase.messaging.FirebaseMessaging

class GatewayApplication : Application() {

    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway-debug")
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway")
        }

        smsBroadcastReceiver = SmsBroadcastReceiver()
        registerReceiver(smsBroadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))

        smsBroadcastReceiver!!.setListener(object : SmsBroadcastReceiver.Listener {
            override fun onTextReceived(text: String) {
                //TODO Api Service Call
            }
        })
    }
}
