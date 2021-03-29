package com.kfouri.cryptoprice.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import kotlinx.coroutines.launch
import java.text.NumberFormat

class ListFragment: Fragment(), NewCurrencyDialog.NewCryptoCreated {

    private val sharedPrefFile = "kotlinsharedpreference"
    private val TAG = "ListFragment"
    private lateinit var viewModel: ListViewModel

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
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewCurrencies()

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        viewModel = ViewModelProvider(this, ViewModelFactory(ApiHelper(ApiBuilder.apiService),
                dbHelper as DatabaseHelper)).get(ListViewModel::class.java)

        viewModel.onCurrenciesList().observe(viewLifecycleOwner, Observer { list ->
            getCurrencies(list)
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            val oldTotal = getOldTotal()
            val currentTotal = getTotal(list)
            textView_total.text = format.format(currentTotal)
            swipeRefreshLayout.isRefreshing = false

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
        })

        if (adapter.itemCount == 0) {
            progressBar.visibility = View.VISIBLE
            viewModel.getAllCurrencies()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                onBackPressedCallback
        )

        swipeRefreshLayout.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                viewModel.getAllCurrencies()
            }

        })

        buttonAdd.setOnClickListener {
            val dialog = NewCurrencyDialog(this)
            dialog.show(requireActivity().supportFragmentManager, "Crypto")
        }
    }

    private fun getOldTotal(): Float {
        return sharedPreferences?.getFloat("oldTotal",0F)?: 0F
    }

    private fun getTotal(list: ArrayList<Currency>): Float {
        var total = 0F
        list.forEach {
            total += it.amount * it.currentPrice
        }

        val editor: SharedPreferences.Editor? =  sharedPreferences?.edit()
        editor?.putFloat("oldTotal", total)
        editor?.apply()
        editor?.commit()

        return total
    }

    private fun getCurrencies(list: ArrayList<Currency>) {
        adapter.setData(list)
        progressBar.visibility = View.GONE
    }

    /*
    private fun getGenres() {
        viewModel.getGenres().observe(viewLifecycleOwner, Observer {

            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        resource.data?.let { it ->
                            getMovies()
                            setGenresData(it.genres)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Error Genres:" + it.message)
                    }
                }
            }
        })
    }

    private fun getMovies() {
        viewModel.getMovies(currentPage).observe(viewLifecycleOwner, Observer {

            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        recyclerView_movies.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE

                        resource.data?.let { it ->
                            setData(it.results)
                            totalPages = it.totalPages
                        }
                    }
                    Status.ERROR -> {
                        recyclerView_movies.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Error:" + it.message)
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView_movies.visibility = View.GONE
                    }
                }
            }
        })
    }
     */

    private fun setRecyclerViewCurrencies() {
        val mPopularLayoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView_currencyList.setHasFixedSize(true)
        recyclerView_currencyList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recyclerView_currencyList.layoutManager = mPopularLayoutManager
        recyclerView_currencyList.adapter = adapter
    }

    private fun itemClicked(id: Int) {
        val action = ListFragmentDirections.actionOpenDetail(id)
        findNavController().navigate(action)
    }

    override fun receiveData(name: String, exchange: String, amount: Float, purchasePrice: Float) {
        val currency = Currency(0, name, exchange, amount, purchasePrice)

        lifecycleScope.launch {
            (dbHelper as DatabaseHelper).insertCurrency(currency)
            viewModel.getAllCurrencies()
        }
    }

}