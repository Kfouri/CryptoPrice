package com.kfouri.cryptoprice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.network.ApiHelper
import com.kfouri.cryptoprice.network.ApiRepository

class ViewModelFactory(private val apiHelper: ApiHelper?, private val databaseHelper: DatabaseHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                apiHelper?.let { ApiRepository(it) }?.let { ListViewModel(it, databaseHelper) } as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(databaseHelper) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }

}