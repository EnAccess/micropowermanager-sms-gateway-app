package com.gateway.android.network.service

import com.gateway.android.network.model.Employee
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("create")
    fun createEmployee(@Body employee: Employee): Call<ResponseBody>
}