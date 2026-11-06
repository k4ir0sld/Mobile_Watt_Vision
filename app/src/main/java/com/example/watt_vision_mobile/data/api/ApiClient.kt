package com.example.watt_vision_mobile.data.api

import android.content.Context
import com.example.watt_vision_mobile.data.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.107.140.6:3000/"

    private var retrofitInstance: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        if (retrofitInstance == null) {
            val tokenManager = TokenManager(context.applicationContext)
            val authInterceptor = AuthInterceptor(tokenManager)

            // HttpLoggingInterceptor configured securely for development:
            // - Redacts sensitive headers like Authorization to prevent token exposure in logs.
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
                redactHeader("Cookie")
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build()

            retrofitInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }

    inline fun <reified T> createService(context: Context): T {
        return getRetrofit(context).create(T::class.java)
    }
}
