package com.nguyenminhson.yoheoha.data.remote

import android.content.Context
import com.nguyenminhson.yoheoha.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    // 10.0.2.2 connects from Android Emulator to localhost of development host PC
    private const val BASE_URL = "http://127.0.0.1:3000/api/v1/"

    @Volatile
    private var apiService: YoheOhaApiService? = null

    fun getApiService(context: Context): YoheOhaApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createApiService(context).also { apiService = it }
        }
    }

    private fun createApiService(context: Context): YoheOhaApiService {
        val sessionManager = SessionManager(context)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(sessionManager.getAuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(YoheOhaApiService::class.java)
    }
}
