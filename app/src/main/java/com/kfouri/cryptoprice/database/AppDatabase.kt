package com.kfouri.cryptoprice.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kfouri.cryptoprice.database.dao.CurrencyDao
import com.kfouri.cryptoprice.database.model.Currency

@Database(entities = [Currency::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

}