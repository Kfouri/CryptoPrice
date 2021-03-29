package com.kfouri.cryptoprice.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.network.ApiRepository
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList

class ListViewModel(private val apiRepository: ApiRepository, private val databaseHelper: DatabaseHelper): ViewModel() {

    private val currenciesListLiveData = MutableLiveData<ArrayList<Currency>>()

    /*
    fun getMovies(page: Long) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiRepository.getMovies(page)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error getting movies..."))
        }
    }

    fun getGenres() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiRepository.getGenres()))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error getting genres..."))
        }
    }

     */

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
                it.currentPrice = apiRepository.getCurrencyPrice(it.name).usdt
                databaseHelper.updateCurrency(it)
            }
            currenciesListLiveData.postValue(list)
        }
    }

    fun insertNewCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                databaseHelper.insertCurrency(currency)
                getAllCurrencies()
            } catch (e: Exception) {
                Log.d("Kafu", "Error Insert: "+e.message)
            }
        }
    }

    fun onCurrenciesList(): LiveData<ArrayList<Currency>> = currenciesListLiveData

}