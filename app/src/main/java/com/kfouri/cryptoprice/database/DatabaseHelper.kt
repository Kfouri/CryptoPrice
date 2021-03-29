package com.kfouri.cryptoprice.database

import com.kfouri.cryptoprice.database.model.Currency


interface DatabaseHelper {

    suspend fun getAllCurrencies(): List<Currency>

    suspend fun getCurrency(id: Int): Currency

    suspend fun insertCurrency(currency: Currency)

    suspend fun updateCurrency(currency: Currency)

    suspend fun removeCurrency(currency: Currency)

}