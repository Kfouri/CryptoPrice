package com.kfouri.cryptoprice.database.dao

import androidx.room.*
import com.kfouri.cryptoprice.database.model.Currency

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency where id = :id")
    suspend fun getCurrency(id: Int): Currency

    @Query("SELECT * FROM currency")
    suspend fun getAllCurrencies(): List<Currency>

    @Insert
    suspend fun insertCurrency(currency: Currency)

    @Update
    suspend fun updateCurrency(currency: Currency)

    @Delete
    suspend fun removeCurrency(currency: Currency)
}