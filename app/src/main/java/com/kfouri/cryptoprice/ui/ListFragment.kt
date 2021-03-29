package com.kfouri.cryptoprice.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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

    private val TAG = "ListFragment"
    private lateinit var viewModel: ListViewModel

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
            textView_total.text = format.format(getTotal(list))
            swipeRefreshLayout.isRefreshing = false
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

            /*
            viewModel.insertNewCurrency(Currency(0, "BTC", "Kraken", 1F, 10F, 0F))
            viewModel.insertNewCurrency(Currency(0, "KAFU", "Kraken", 2F, 15F, 0F))
            viewModel.insertNewCurrency(Currency(0, "ETH", "Kraken", 10F, 1F, 0f))
            viewModel.insertNewCurrency(Currency(0, "ETH", 3F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "BNB", 4F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "ADA", 5F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "UNI", 6F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "LUNA", 7F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "XRP", 8F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "BCN", 9F, "Kraken", 0F, 1F))
            viewModel.insertNewCurrency(Currency(0, "SOL", 10F, "Kraken", 0F, 1F))
             */
        }
    }

    private fun getTotal(list: ArrayList<Currency>): Float {
        var total = 0F
        list.forEach {
            total += it.amount * it.currentPrice
        }

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
        //val action = ListFragmentDirections.actionOpenDetail(id)
        //findNavController().navigate(action)
        Log.d("Kafu", "Click Item ID: " + id)
    }

    override fun receiveData(name: String, exchange: String, amount: Float, purchasePrice: Float) {
        val currency = Currency(0, name, exchange, amount, purchasePrice, 0F)

        lifecycleScope.launch {
            (dbHelper as DatabaseHelper).insertCurrency(currency)
            viewModel.getAllCurrencies()
        }
    }

}