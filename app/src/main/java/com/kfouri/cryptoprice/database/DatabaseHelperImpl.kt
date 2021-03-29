package com.kfouri.cryptoprice.database

import com.kfouri.cryptoprice.database.model.Currency

class DatabaseHelperImpl(private val appDatabase: AppDatabase): DatabaseHelper {

    override suspend fun getAllCurrencies(): List<Currency> = appDatabase.currencyDao().getAllCurrencies()

    override suspend fun getCurrency(name: String): Currency = appDatabase.currencyDao().getCurrency(name)

    override suspend fun insertCurrency(currency: Currency) = appDatabase.currencyDao().insertCurrency(currency)

    override suspend fun updateCurrency(currency: Currency) = appDatabase.currencyDao().updateCurrency(currency)

    override suspend fun removeCurrency(currency: Currency) = appDatabase.currencyDao().removeCurrency(currency)
}