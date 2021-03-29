package com.kfouri.cryptoprice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.network.ApiHelper
import com.kfouri.cryptoprice.network.ApiRepository

class ViewModelFactory(private val apiHelper: ApiHelper, private val databaseHelper: DatabaseHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                ListViewModel(ApiRepository(apiHelper), databaseHelper) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }

}