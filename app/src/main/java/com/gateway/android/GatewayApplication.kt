package com.gateway.android

import android.app.Application
import android.telephony.TelephonyManager
import com.gateway.android.utils.SharedPreferencesWrapper
import com.gateway.android.utils.Util

class GatewayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        SharedPreferencesWrapper.getInstance(this).simState = telephonyManager.simState
        Util.checkConnectivity(this)
    }
}
