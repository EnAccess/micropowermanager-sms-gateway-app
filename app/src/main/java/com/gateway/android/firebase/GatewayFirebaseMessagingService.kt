package com.gateway.android.firebase

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.gateway.android.network.http.RetrofitClient
import com.gateway.android.network.service.ApiService
import com.gateway.android.utils.SharedPreferencesWrapper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GatewayFirebaseMessagingService : FirebaseMessagingService() {

    private val sharedPreferences = SharedPreferencesWrapper.getInstance()
    private var mApiService = RetrofitClient.getInstance().retrofit.create(ApiService::class.java)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val number = remoteMessage.data[KEY_NOTIFICATION_EXTRA_NUMBER]
        val message = remoteMessage.data[KEY_NOTIFICATION_EXTRA_MESSAGE]
        val callback = remoteMessage.data[KEY_NOTIFICATION_EXTRA_CALLBACK_URL] ?: ""

        incrementNotification()

        // Register receivers
        val sentIntent = PendingIntent.getBroadcast(this, 0, Intent(SMS_SENT).putExtra(KEY_NOTIFICATION_EXTRA_CALLBACK_URL, callback), PendingIntent.FLAG_UPDATE_CURRENT)
        val deliveryIntent = PendingIntent.getBroadcast(this, 0, Intent(SMS_DELIVERED).putExtra(KEY_NOTIFICATION_EXTRA_CALLBACK_URL, callback), PendingIntent.FLAG_UPDATE_CURRENT)
        registerSmsSentReceiver(callback)
        registerSmsDeliveredReceiver(callback)

        // Check SIM state
        if (sharedPreferences.simState == TelephonyManager.SIM_STATE_READY) {
            try {
                sendSms(number, message, sentIntent, deliveryIntent)
            } catch (e: Exception) {
                handleSmsException(e)
            }
        } else {
            handleSimStateNotReady()
        }
    }

    private fun registerSmsSentReceiver(callback: String) {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val count = sharedPreferences.elseCount ?: 0
                        sharedPreferences.elseCount = count + 1
                        confirmSms(context, callback, "sent")
                    }
                    else -> {
                        confirmSms(context, callback, "failed")
                    }
                }
            }
        }, IntentFilter(SMS_SENT))
    }

    private fun registerSmsDeliveredReceiver(callback: String) {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val count = sharedPreferences.sentMessageCount ?: 0
                        sharedPreferences.sentMessageCount = count + 1
                        confirmSms(context, callback, "delivered")
                    }
                    else -> {
                        confirmSms(context, callback, "failed")
                    }
                }
            }
        }, IntentFilter(SMS_DELIVERED))
    }

    private fun sendSms(number: String?, message: String?, sentIntent: PendingIntent, deliveryIntent: PendingIntent) {
        try {
            SmsManager.getDefault().sendTextMessage(number, null, message, sentIntent, deliveryIntent)
        } catch (e: Exception) {
            handleSmsException(e)
        }
    }

    private fun handleSmsException(e: Exception) {
        Log.e("Error State", e.message)
        sharedPreferences.catchCount = (sharedPreferences.catchCount ?: 0) + 1
        confirmSms(this, "", "failed")
    }

    private fun handleSimStateNotReady() {
        Log.e("Sim State", "simState not equal to Sim_State_Ready enum.")
        sharedPreferences.catchCount = (sharedPreferences.catchCount ?: 0) + 1
        confirmSms(this, "", "failed")
    }

    private fun incrementNotification() {
        sharedPreferences.receivedNotificationCount = (sharedPreferences.receivedNotificationCount ?: 0) + 1
    }

    private fun confirmSms(context: Context?, callback: String, status: String) {
        if (callback.isNotEmpty()) {
            val modifiedCallback = when (status) {
                "failed" -> callback.replace("confirm", "failed")
                "sent" -> callback.replace("confirm", "sent")
                else -> callback.replace("confirm", "delivered")
            }

            mApiService.smsCallback(modifiedCallback).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Sms confirm", "$callback confirmation failed")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("Sms confirm", "callback has been confirmed")
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
        private const val KEY_NOTIFICATION_EXTRA_CALLBACK_URL = "callback"
        private const val SMS_SENT = "SMS_SENT"
        private const val SMS_DELIVERED = "SMS_DELIVERED"
    }
}


