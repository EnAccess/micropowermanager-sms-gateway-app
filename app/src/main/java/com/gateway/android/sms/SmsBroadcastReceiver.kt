package com.gateway.android.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.gateway.android.network.http.RetrofitClient
import com.gateway.android.network.model.Employee
import com.gateway.android.network.service.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A broadcast receiver who listens for incoming SMS
 */

class SmsBroadcastReceiver : BroadcastReceiver() {

    private val mApiService: ApiService = RetrofitClient.instance.retrofit.create(ApiService::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsBody = ""

            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.messageBody
            }

            mApiService.createEmployee(Employee("Aydin Mehmet Ozkan", "123456", "19"))
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    }
                })
        }
    }
}
