package com.kfouri.cryptoprice.network

class ApiHelper(private val apiService: ApiService) {
    suspend fun getCurrencyPrice(name: String) = apiService.getPrice(name)
}