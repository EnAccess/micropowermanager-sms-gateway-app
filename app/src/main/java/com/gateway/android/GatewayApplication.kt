package com.gateway.android

import android.app.Application
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
    }
}
