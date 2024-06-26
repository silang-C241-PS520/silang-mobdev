package com.example.silang_mobdev.data.api.retrofit


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {

        fun getApiService(token: String? = null): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // Increase connect timeout
                .writeTimeout(30, TimeUnit.SECONDS) // Increase write timeout
                .readTimeout(30, TimeUnit.SECONDS) // Increase read timeout
            if (token != null) {
                val authInterceptor = Interceptor { chain ->
                    val req = chain.request()
                    val requestHeaders = req.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(requestHeaders)
                }
                clientBuilder.addInterceptor(authInterceptor)
            }

            val client = clientBuilder.build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://silang-api-nmw6skuooa-et.a.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
