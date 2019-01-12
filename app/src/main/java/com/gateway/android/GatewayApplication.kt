package com.gateway.android

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.gateway.android.utils.SharedPreferencesWrapper
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.fabric.sdk.android.Fabric

class GatewayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway-debug")
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway")
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        firebaseRemoteConfig.setConfigSettings(configSettings)

        firebaseRemoteConfig.fetch(43200).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseRemoteConfig.activateFetched()

                SharedPreferencesWrapper(this).baseUrl = firebaseRemoteConfig.getString("base_url")
            }
        }
    }
}
