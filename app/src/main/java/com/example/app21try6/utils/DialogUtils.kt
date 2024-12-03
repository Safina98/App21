package com.example.app21try6.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.app21try6.R
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.stock.brandstock.BrandStockViewModel

import com.google.android.material.textfield.TextInputEditText

object DialogUtils{
    fun showDeleteDialog(context:Context,fragment: Fragment, viewModel: ViewModel, item: Any, onDelete: (ViewModel, Any) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                onDelete(viewModel, item)
            }
            .setNegativeButton("No") { dialog, id -> dialog.dismiss() }
            .setOnCancelListener { dialog -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    fun <VM : ViewModel, M> updateDialog(
        context: Context,
        viewModel: VM,
        model: M?,
        title: String,
        getBrandName: (M) -> String?,
        setBrandName: (M, String) -> Unit,
        updateFunction: (VM, M) -> Unit,
        insertFunction: (VM, String) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)

        // Set initial text if model is not null
        if (model != null) {
            textKet.setText(getBrandName(model))
        }

        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            val newName = textKet.text.toString().uppercase().trim()

            if (model != null) {
                setBrandName(model, newName)
                if (newName.isNotEmpty()) {
                    updateFunction(viewModel, model)  // Use updateFunction with explicit types
                }
            } else {
                if (newName.isNotEmpty()) {
                    insertFunction(viewModel, newName)  // Use insertFunction with explicit types
                }
            }
        }

        builder.setNegativeButton("No") { dialog, which -> }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
    }

    fun updateDialog(context: Context,viewModel: BrandStockViewModel,discList:List<String>?) {
        val vendible=viewModel._product.value

        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Update")

        // Inflate the custom view for the dialog
        val dialogBinding = DataBindingUtil.inflate<PopUpUpdateProductDialogBinding>(
            LayoutInflater.from(context), R.layout.pop_up_update_product_dialog, null, false
        )
        // Initialize views from the binding
        val textKet = dialogBinding.textUpdateKet
        val textPrice = dialogBinding.textUpdatePrice
        val textCapital = dialogBinding.textCapital
        val textDisc = dialogBinding.textDiscount
        val textCapital2=dialogBinding.textCapital2
        val textModusNoet=dialogBinding.defaultNet
        val textPurchasePrice=dialogBinding.purchasePrice
        val textPurchaseUnit=dialogBinding.purchaseUnit
        val tVId=dialogBinding.txtId

        dialogBinding.ilCapital2.hint="Modal 2"
        dialogBinding.ilDefaultNet.hint="net modus"
        dialogBinding.ilKet.hint="Product"
        dialogBinding.ilPrice.hint="Harga"
        dialogBinding.ilCapital.hint="Modal"
        dialogBinding.ilDisc.hint="Diskon"
        dialogBinding.ilPurchasePrice.hint="Harga Beli"
        dialogBinding.ilPurchaseUnit.hint="Unit"

        val discName=viewModel.discountName.value
        // Set up the AutoCompleteTextView with a mutable adapter
        val merkAdapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        textDisc.setAdapter(merkAdapter)
        // Observe the ViewModel LiveData and update the adapter

                merkAdapter.clear() // Clear the adapter's data
                merkAdapter.addAll(discList?: emptyList()) // Add the sorted data to the adapter

        // Set the data for the dialog fields
        if (vendible!=null){
            textKet.setText(vendible.product_name.toString())
            textPrice.setText(vendible.product_price.toString())
            textCapital.setText(vendible.product_capital.toString())
            textCapital2.setText(vendible.alternate_price.toString())
            textModusNoet.setText(vendible.default_net.toString())
            tVId.setText(vendible.product_id.toString())
            textPurchasePrice.setText(vendible.purchasePrice?.let { it.toString() }?:"")
            textPurchaseUnit.setText(vendible.puchaseUnit?.let { it }?:"")

            if (discName!=null) textDisc.setText(discName)
            textKet.requestFocus()
        }

        // Use dialogBinding.root instead of view
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Update") { dialog, which ->
            val priceString = textPrice.text.toString()
            val capitalString = textCapital.text.toString()
            val price = priceString.toIntOrNull()
            val capital = capitalString.toIntOrNull()
            val alternateCapital =textCapital2.text.toString().toDoubleOrNull()
            val modusNet =textModusNoet.text.toString().toDoubleOrNull()
            val product_name=textKet.text.toString().uppercase().trim()
            val purchasePrice=textPurchasePrice.text.toString().trim().toIntOrNull()
            val purchaseUnit=textPurchasePrice.text.toString().trim().uppercase()
            if (price != null) {
                vendible?.product_price = price
            }
            if (capital != null) {
                vendible?.product_capital = capital
            }
            vendible?.product_name = product_name
            vendible?.alternate_price=alternateCapital?:0.0
            vendible?.default_net=modusNet ?: 0.0
            vendible?.purchasePrice=purchasePrice
            vendible?.puchaseUnit=purchaseUnit
            val discName = textDisc.text.toString().uppercase().trim()
            if (vendible?.product_name?.isNotEmpty() == true) {
                viewModel.updateProduct(vendible, discName)
            }else{
                viewModel.insertAnItemProductStock(product_name, price?:0,capital?:0,alternateCapital?:0.0,modusNet?:0.0,discName,purchasePrice,purchaseUnit)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }
}