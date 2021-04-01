package com.kfouri.cryptoprice.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.network.ApiRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailViewModel(private val apiRepository: ApiRepository, private val databaseHelper: DatabaseHelper): ViewModel() {

    private val currencyLiveData = MutableLiveData<Currency>()
    private val currencyErrorLiveData = MutableLiveData<Unit>()
    private val removeCurrencyLiveData = MutableLiveData<Unit>()

    fun getCurrency(id: Int) {
        viewModelScope.launch {
            try {
                val currency = databaseHelper.getCurrency(id)
                currencyLiveData.value = currency
            } catch (e: Exception) {
                Log.d("Kafu", "Object does not exists")
                currencyErrorLiveData.value = Unit
            }
        }
    }

    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                currency.currentPrice = apiRepository.getCurrencyPrice(currency.name).usdt
                currency.oldPrice = currency.currentPrice
                databaseHelper.updateCurrency(currency)
                getCurrency(currency.id)
            } catch (e: Exception) {
                Log.d("Kafu", "Error Updating: "+e.message)
            }
        }
    }

    fun removeCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                databaseHelper.removeCurrency(currency)
                removeCurrencyLiveData.value = Unit
            } catch (e: Exception) {
                Log.d("Kafu", "Error Updating: "+e.message)
            }
        }
    }

    fun onCurrency(): LiveData<Currency> = currencyLiveData
    fun onErrorCurrency(): LiveData<Unit> = currencyErrorLiveData
    fun onRemoveCurrency(): LiveData<Unit> = removeCurrencyLiveData

}