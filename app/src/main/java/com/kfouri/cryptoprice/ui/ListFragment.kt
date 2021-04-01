package com.kfouri.cryptoprice.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.kfouri.cryptoprice.R
import com.kfouri.cryptoprice.adapter.ListAdapter
import com.kfouri.cryptoprice.database.DatabaseBuilder
import com.kfouri.cryptoprice.database.DatabaseHelper
import com.kfouri.cryptoprice.database.DatabaseHelperImpl
import com.kfouri.cryptoprice.database.model.Currency
import com.kfouri.cryptoprice.network.ApiBuilder
import com.kfouri.cryptoprice.network.ApiHelper
import com.kfouri.cryptoprice.viewmodel.ListViewModel
import com.kfouri.cryptoprice.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.NumberFormat

class ListFragment: Fragment(), NewCurrencyDialog.NewCryptoCreated {

    private val sharedPrefFile = "kotlinsharedpreference"
    private val TAG = "ListFragment"
    private lateinit var viewModel: ListViewModel

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }

    private val sharedPreferences by lazy {
        activity?.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    }

    private val adapter by lazy { ListAdapter() { id: Int -> itemClicked(id) }}

    private val dbHelper by lazy {
        activity?.applicationContext?.let {
            DatabaseBuilder.getInstance(
                    it
            )
        }?.let { DatabaseHelperImpl(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        viewModel =
            ViewModelProvider(
                this, ViewModelFactory(ApiHelper(ApiBuilder.apiService),
                    dbHelper as DatabaseHelper)).get(ListViewModel::class.java)

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        viewModel.onCurrenciesList().observe(viewLifecycleOwner, { list ->
            getCurrencies(list)
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            val oldTotal = getOldTotal()
            val currentTotal = getTotal(list)
            textView_total.text = format.format(currentTotal)
            swipeRefreshLayout.isRefreshing = false

            if (adapter.itemCount > 0) {
                if (oldTotal != currentTotal) {
                    imageView_diffPrice.visibility = View.VISIBLE
                    if (oldTotal > currentTotal) {
                        imageView_diffPrice.setImageDrawable(activity?.applicationContext?.let {
                            ContextCompat.getDrawable(
                                it,R.drawable.ic_arrow_down)
                        })
                    } else {
                        imageView_diffPrice.setImageDrawable(activity?.applicationContext?.let {
                            ContextCompat.getDrawable(
                                it,R.drawable.ic_arrow_up)
                        })
                    }
                } else {
                    imageView_diffPrice.visibility = View.GONE
                }
            } else {
                imageView_diffPrice.visibility = View.GONE
                setOldPrice(0F)
            }

        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE

        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)

        viewModel.getAllCurrencies()

        swipeRefreshLayout.setOnRefreshListener {
            Log.d("Kafu", "Refresh viewModel.getAllCurrencies()")
            viewModel.getAllCurrencies()
        }

        buttonAdd.setOnClickListener {
            val dialog = NewCurrencyDialog(this)
            dialog.show(requireActivity().supportFragmentManager, "Crypto")
        }

        setRecyclerViewCurrencies()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private fun getOldTotal(): Float {
        return sharedPreferences?.getFloat("oldTotal",0F)?: 0F
    }

    private fun getTotal(list: ArrayList<Currency>): Float {
        var total = 0F
        list.forEach {
            total += it.amount * it.currentPrice
        }

        setOldPrice(total)

        return total
    }

    private fun setOldPrice(total: Float) {
        val editor: SharedPreferences.Editor? =  sharedPreferences?.edit()
        editor?.putFloat("oldTotal", total)
        editor?.apply()
        editor?.commit()

    }

    private fun getCurrencies(list: ArrayList<Currency>) {
        adapter.setData(list)
        progressBar.visibility = View.GONE
    }

    private fun setRecyclerViewCurrencies() {
        val mPopularLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView_currencyList.setHasFixedSize(true)
        recyclerView_currencyList.layoutManager = mPopularLayoutManager
        recyclerView_currencyList.adapter = adapter
    }

    private fun itemClicked(id: Int) {
        val detailFragment = DetailFragment.newInstance()
        val bundle = Bundle()
        bundle.putInt("currencyId", id)

        buttonAdd.visibility = View.GONE
        detailFragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, detailFragment, "Detail")
            .commit()
    }

    override fun receiveData(id: Int, name: String, exchange: String, amount: Float, purchasePrice: Float) {
        val currency = Currency(id, name, exchange, amount, purchasePrice)
        viewModel.insertNewCurrency(currency)
    }
}