package com.kfouri.cryptoprice.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.network.ApiRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import kotlin.collections.ArrayList

class ListViewModel(private val apiRepository: ApiRepository, private val databaseHelper: DatabaseHelper): ViewModel() {

    private val currenciesListLiveData = MutableLiveData<ArrayList<Currency>>()

    fun getAllCurrencies() {
        viewModelScope.launch {
            var list = ArrayList<Currency>()
            try {
                list = databaseHelper.getAllCurrencies() as ArrayList<Currency>
            } catch (e: Exception) {
                Log.d("Kafu", "Database empty")
            }
            list.forEach {
                it.oldPrice = it.currentPrice
                runBlocking {
                    it.currentPrice = apiRepository.getCurrencyPrice(it.name).usdt
                }
                databaseHelper.updateCurrency(it)
            }
            currenciesListLiveData.value = list
        }
    }

    fun insertNewCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                databaseHelper.insertCurrency(currency)
                Log.d("Kafu", "Insert viewModel.getAllCurrencies()")
                getAllCurrencies()
            } catch (e: Exception) {
                Log.d("Kafu", "Error Insert: "+e.message)
            }
        }
    }

    fun onCurrenciesList() = currenciesListLiveData as LiveData<ArrayList<Currency>>

}