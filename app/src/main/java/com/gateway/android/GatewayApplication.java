package com.gateway.android;

import android.app.Application;
import com.google.firebase.messaging.FirebaseMessaging;

public class GatewayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway-debug");
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("gateway");
        }

        //com.coingapp.android.util.SharedPreferenceWrapper.getInstance().initialize(this);
    }
}
