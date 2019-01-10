package com.gateway.android.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Keeps a reference to the SharedPreference
 * Acts as a Singleton class
 */
class SharedPreferencesWrapper(context: Context) {

    private var mSharedPreferences: SharedPreferences? =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var baseUrl: String?
        get() = mSharedPreferences!!.getString(KEY_BASE_URL, "http://dummy.restapiexample.com/api/v1/")
    set(url) {
        if (mSharedPreferences != null) {
            mSharedPreferences!!.edit().putString(KEY_BASE_URL, url).apply()
        }
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "inensus-gateway"
        private const val KEY_BASE_URL = "baseUrl"
    }
}
