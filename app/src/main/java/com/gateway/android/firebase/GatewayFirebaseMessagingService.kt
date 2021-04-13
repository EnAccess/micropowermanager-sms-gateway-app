package com.gateway.android.firebase

import android.telephony.SmsManager
import android.telephony.TelephonyManager
import com.gateway.android.utils.SharedPreferencesWrapper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.IntentFilter
import android.app.Activity
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.app.PendingIntent
import android.util.Log
import android.widget.Toast
import com.gateway.android.network.http.RetrofitClient
import com.gateway.android.network.service.ApiService
import retrofit2.Callback
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class GatewayFirebaseMessagingService : FirebaseMessagingService() {

    private var isReceiverRegistered = false
    private val sharedPreferences = SharedPreferencesWrapper.getInstance()
    private var mApiService = RetrofitClient.getInstance().retrofit.create(ApiService::class.java)

    private lateinit var smsId: String

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val number = remoteMessage.data[KEY_NOTIFICATION_EXTRA_NUMBER]
        val message = remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE]
        smsId = remoteMessage.data[KEY_NOTIFICATION_EXTRA_SMS_ID] ?: ""

        this.incrementNotification()

        if (!isReceiverRegistered) {
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            sharedPreferences.sentMessageCount = sharedPreferences.sentMessageCount!! + 1
                            confirmSms()
                        }
                        else -> {
                            sharedPreferences.failedMessageCount = sharedPreferences.failedMessageCount!! + 1
                        }
                    }
                }
            }, IntentFilter(SMS_DELIVERED))

            isReceiverRegistered = true
        }

        if (sharedPreferences.simState == TelephonyManager.SIM_STATE_READY) {
            try {
                val sentIntent = PendingIntent.getBroadcast(this, 0, Intent(SMS_SENT), 0)
                val deliveryIntent = PendingIntent.getBroadcast(this, 0, Intent(SMS_DELIVERED), 0)

                SmsManager.getDefault().sendTextMessage(number, null, message, sentIntent, deliveryIntent)
            } catch (e: Exception) {
                //TODO Send error to server
            }
        } else {
            //TODO Send error to server
        }
    }

    private fun incrementNotification() {
        sharedPreferences.receivedNotificationCount = sharedPreferences.receivedNotificationCount!! + 1
    }

    private fun confirmSms() {
        if (smsId.isNotEmpty()) {
            mApiService.smsCallback(smsId).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Sms confirm", "Sms with Id:$smsId confirmation failed")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Sms confirm", "Sms with Id:$smsId has been confirmed")
                }
            })
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sharedPreferences.deviceToken = p0
    }

    companion object {
        private const val KEY_NOTIFICATION_EXTRA_NUMBER = "number"
        private const val KEY_NOTIFICATION_EXTRA_MESSAGE = "message"
        private const val KEY_NOTIFICATION_EXTRA_SMS_ID = "sms_id"
        private const val SMS_SENT = "SMS_SENT"
        private const val SMS_DELIVERED = "SMS_DELIVERED"
    }
}
