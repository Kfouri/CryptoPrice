package com.kfouri.cryptoprice.model

data class Currency (
        val name: String,
        var amount: Float,
        val exchange: String
)