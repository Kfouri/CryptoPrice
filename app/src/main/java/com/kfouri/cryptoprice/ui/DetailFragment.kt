package com.kfouri.cryptoprice.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kfouri.cryptoprice.R
import com.kfouri.cryptoprice.database.DatabaseBuilder
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.DatabaseHelperImpl
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.network.ApiBuilder
import com.kfouri.cryptoprice.network.ApiHelper
import com.kfouri.cryptoprice.viewmodel.DetailViewModel
import com.kfouri.cryptoprice.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_detail.*
import java.text.NumberFormat


class DetailFragment: Fragment(), NewCurrencyDialog.NewCryptoCreated {

    companion object {
        fun newInstance(): DetailFragment = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel

    private var currencyId = 0

    private val dbHelper by lazy {
        activity?.applicationContext?.let {
            DatabaseBuilder.getInstance(
                it
            )
        }?.let { DatabaseHelperImpl(it) }
    }

    private lateinit var mCurrentCurrency: Currency

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currencyId = arguments?.getInt("currencyId")?: 0

        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                ApiHelper(ApiBuilder.apiService),
                dbHelper as DatabaseHelper
            )
        ).get(DetailViewModel::class.java)

        viewModel.onCurrency().observe(viewLifecycleOwner, { currency -> showCurrency(currency) })
        viewModel.onRemoveCurrency().observe(viewLifecycleOwner, { goBackToList() })

        viewModel.getCurrency(currencyId)

        imageView_back.setOnClickListener {
            goBackToList()
        }

        buttonEdit.setOnClickListener {
            val dialog = NewCurrencyDialog(this, mCurrentCurrency)
            dialog.show(requireActivity().supportFragmentManager, "Crypto")
        }

        buttonRemove.setOnClickListener {

            val makeDialog: AlertDialog.Builder = AlertDialog.Builder(activity)

            makeDialog.setTitle("Crypto")
            makeDialog.setMessage("¿Desea eliminar la moneda?")
            makeDialog.setPositiveButton("Aceptar") { _, _ ->
                viewModel.removeCurrency(mCurrentCurrency)
            }

            makeDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            val ad: AlertDialog = makeDialog.create()
            ad.show()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBackToList()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun goBackToList() {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, ListFragment.newInstance(), "List")
            .addToBackStack(null)
            .commit()
    }

    private fun showCurrency(currency: Currency) {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()

        textView_currencyName.text = currency.name
        textView_amount.text = currency.amount.toString()
        textView_purchasePrice.text = currency.puchasePrice.toString()
        textView_initial.text = format.format(currency.amount * currency.puchasePrice)

        if (currency.currentPrice == 0F) {
            textView_currentPrice.text = "N/A"
            textView_balance.text = "--.-"
            textView_winloss.text = "--.-"
        } else {
            textView_currentPrice.text = currency.currentPrice.toString()
            textView_balance.text = format.format(currency.amount * currency.currentPrice)

            val winloss = currency.amount * currency.currentPrice - currency.amount * currency.puchasePrice
            textView_winloss.text = format.format(winloss)

            if (winloss > 0) {
                textView_winloss.setTextColor(Color.parseColor("#00A973"))
            } else {
                textView_winloss.setTextColor(Color.parseColor("#A90017"))
            }
        }

        mCurrentCurrency = currency
    }

    override fun receiveData(
        id: Int,
        name: String,
        exchange: String,
        amount: Float,
        purchasePrice: Float
    ) {
        val currency = Currency(id, name, exchange, amount, purchasePrice)

        viewModel.updateCurrency(currency)
    }
}