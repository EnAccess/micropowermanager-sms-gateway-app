package com.gateway.android.network.http

import com.gateway.android.GatewayApplication
import com.gateway.android.utils.SharedPreferencesWrapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Client implementation for Square's Retrofit.
 */
class RetrofitClient
/**
 * Retrofit Connection Builder
 */
private constructor(context: GatewayApplication) {
    /**
     * Returns Retrofit instance
     *
     * @return Retrofit instance
     */
    val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(SharedPreferencesWrapper(context).baseUrl!!)
            .client(client)
            .build()
    }

    companion object {

        private const val DEFAULT_CONNECT_TIMEOUT: Long = 30000
        private const val DEFAULT_READ_TIMEOUT: Long = 30000

        @Volatile
        private var instance: RetrofitClient? = null

        fun getInstance(context: GatewayApplication): RetrofitClient =
            instance ?: synchronized(this) {
                instance ?: buildRetrofitClient(context).also { instance = it }
            }

        private fun buildRetrofitClient(context: GatewayApplication) =
            RetrofitClient(context)
    }
}