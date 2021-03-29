package com.kfouri.cryptoprice.network

import com.kfouri.cryptoprice.util.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiBuilder {

    /*
    private var client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $TOKEN")
            .build()
        chain.proceed(newRequest)
    }.build()

     */

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
                //.client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}