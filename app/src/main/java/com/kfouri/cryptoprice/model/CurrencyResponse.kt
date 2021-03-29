package com.kfouri.cryptoprice.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponse (
    @SerializedName("USDT") val usdt: Float,
)