package com.kfouri.cryptoprice.network

import com.kfouri.cryptoprice.model.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("data/price?tsyms=USDT")
    suspend fun getPrice(@Query("fsym") currency: String): CurrencyResponse
}