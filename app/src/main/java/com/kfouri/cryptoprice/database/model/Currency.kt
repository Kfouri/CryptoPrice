package com.kfouri.cryptoprice.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "exchange") val exchange: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "puchasePrice") val puchasePrice: Float,
    @ColumnInfo(name = "currentPrice") var currentPrice: Float,
)