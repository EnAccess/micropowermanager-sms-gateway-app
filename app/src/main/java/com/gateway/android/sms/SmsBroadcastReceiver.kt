package com.gateway.android.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.gateway.android.GatewayApplication
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

class SmsBroadcastReceiver(context: GatewayApplication) : BroadcastReceiver() {

    private val mApiService: ApiService = RetrofitClient.getInstance(context).retrofit.create(ApiService::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsBody = ""
            var senderNumber = ""

            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.messageBody
            }

            senderNumber = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0].displayOriginatingAddress

            mApiService.createEmployee(Employee(senderNumber, smsBody, "19"))
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    }
                })
        }
    }
}
