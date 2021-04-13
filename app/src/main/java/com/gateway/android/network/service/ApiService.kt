package com.gateway.android.network.service

import com.gateway.android.network.model.Sms
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("sms")
    fun sendReceivedSmsToServer(@Body sms: Sms): Call<ResponseBody>

    @GET("sms/{uuid}/confirm")
    fun smsCallback(@Path("uuid") uuid: String?): Call<ResponseBody>
}