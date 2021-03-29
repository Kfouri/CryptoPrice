package com.kfouri.cryptoprice.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.kfouri.cryptoprice.R
import com.kfouri.cryptoprice.database.DatabaseBuilder
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.DatabaseHelperImpl
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.viewmodel.DetailViewModel
import com.kfouri.cryptoprice.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.currency_item.view.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.NumberFormat

class DetailFragment: Fragment() {

    private lateinit var viewModel: DetailViewModel

    private val args: DetailFragmentArgs by navArgs()
    private var currencyId = 0

    private val dbHelper by lazy {
        activity?.applicationContext?.let {
            DatabaseBuilder.getInstance(
                it
            )
        }?.let { DatabaseHelperImpl(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currencyId = args.ID
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        viewModel = ViewModelProvider(this, ViewModelFactory(
            null,
            dbHelper as DatabaseHelper
        )
        ).get(DetailViewModel::class.java)

        viewModel.onCurrency().observe(viewLifecycleOwner, { currency -> showCurrency(currency)})

        viewModel.getCurrency(currencyId)

        imageView_back.setOnClickListener {
            activity?.onBackPressed()
        }
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

    }
}