package com.kfouri.cryptoprice.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kfouri.cryptoprice.R
import kotlinx.android.synthetic.main.dialog_new_currency.*

class NewCurrencyDialog(val newCryptoCreated: NewCryptoCreated): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_new_currency, null)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setTitle("Nueva Moneda")
                .setPositiveButton(R.string.dialog_create) { dialog, id ->
/*
                    val name = view.editText_currencyName.text.toString()
                    val exchange = view.editText_currencyExchange.text.toString()
                    val amount = view.editText_currencyAmount.text.toString().toFloat()
                    val purchasePrice = view.editText_currencyPurchasePrice.text.toString().toFloat()

                    newCryptoCreated.receiveData(name, exchange, amount, purchasePrice)

 */
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onResume() {
        super.onResume()

        val dialog: AlertDialog = dialog as AlertDialog
        if (dialog != null) {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                var wantToCloseDialog = false

                val name = dialog.editText_currencyName.text.toString()
                var exchange = dialog.editText_currencyExchange.text.toString()
                var amount = 0F
                var purchasePrice = 0F

                if (name.isBlank()) {
                    Toast.makeText(dialog.editText_currencyAmount.context, "Nombre Moneda obligatorio", Toast.LENGTH_LONG).show()
                } else {
                    wantToCloseDialog = true
                }

                if (exchange.isBlank()) {
                    exchange = "-"
                }

                if (dialog.editText_currencyAmount.text.toString().isNotBlank()) {
                    amount = dialog.editText_currencyAmount.text.toString().toFloat()
                }

                if (dialog.editText_currencyPurchasePrice.text.toString().isNotBlank()) {
                    purchasePrice = dialog.editText_currencyPurchasePrice.text.toString().toFloat()
                }

                if (wantToCloseDialog) {
                    newCryptoCreated.receiveData(name, exchange, amount, purchasePrice)
                    dialog.dismiss()
                }
            }
        }
    }
    interface NewCryptoCreated {
        fun receiveData(name: String, exchange: String, amount: Float, purchasePrice: Float)
    }
}