package com.kfouri.cryptoprice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.model.Currency
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailViewModel(private val databaseHelper: DatabaseHelper): ViewModel() {

    private val currencyLiveData = MutableLiveData<Currency>()
    private val currencyErrorLiveData = MutableLiveData<Unit>()

    fun getCurrency(id: Int) {
        viewModelScope.launch {
            try {
                val currency = databaseHelper.getCurrency(id)
                currencyLiveData.postValue(currency)
            } catch (e: Exception) {
                Log.d("Kafu", "Object does not exists")
                currencyErrorLiveData.postValue(Unit)
            }
        }
    }

    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            try {
                databaseHelper.updateCurrency(currency)
            } catch (e: Exception) {
                Log.d("Kafu", "Error Updating: "+e.message)
            }
        }
    }

    fun onCurrency(): LiveData<Currency> = currencyLiveData
    fun onErrorCurrency(): LiveData<Unit> = currencyErrorLiveData

}