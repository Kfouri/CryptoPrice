package com.kfouri.cryptoprice.network


class ApiRepository(private val apiHelper: ApiHelper) {
    suspend fun getCurrencyPrice(name: String) = apiHelper.getCurrencyPrice(name)
}