package com.example.app21try6.utils

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.app21try6.R
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.stock.brandstock.BrandStockViewModel

import com.google.android.material.textfield.TextInputEditText

object DialogUtils{
    fun showDeleteDialog(context:Context,
                         fragment: Fragment,
                         viewModel: ViewModel,
                         item: Any,
                         onDelete: (ViewModel, Any) -> Unit
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
    fun showFailedWarning(context:Context,
                          itemName: String,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Update Gagal ${itemName}.  Quantitas minimal 0")
            .setCancelable(true)
            .setPositiveButton("Ok") { dialog, id ->
                dialog.dismiss()
            }
            .setOnCancelListener { dialog -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    fun <VM : ViewModel, M> updateDialogQty(
        context:Context,
        viewModel: ViewModel,
        item: M,
        title:String,
        setBrandName: (M, Double) -> Unit,
        onUpdate: (ViewModel, M) -> Unit
        ) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textKet.requestFocus()
        textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        textKet.keyListener = DigitsKeyListener.getInstance("-0123456789.")
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            val newName = textKet.text.toString().trim().toDoubleOrNull()

            if (newName != null) {
                setBrandName(item, newName)
                onUpdate(viewModel, item)  // Use updateFunction with explicit types
            }
        }
            .setNegativeButton("No") { dialog, id -> dialog.dismiss() }
            .setOnCancelListener { dialog -> dialog.dismiss() }
        val alert = builder.create()

        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
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
        textKet.requestFocus()
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
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alert.show()
        alert.setOnDismissListener {
            alert.dismiss()
        }
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
    }

    fun updateBrandDialog(
        context: Context,
        viewModel: BrandStockViewModel,
        model: BrandProductModel?,
        title: String,
        categoryName:String,
        categoryList:List<String>?,
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update_brand ,null)
        //val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val txtBrand=view.findViewById<TextInputEditText>(R.id.text_update_brand)
        //val autoCompleteCat=view.findViewById<AutoCompleteTextView>(R.id.text_category_name)
        val spinnerCat = view.findViewById<Spinner>(R.id.spinner_category_name)
        // Set initial text if model is not null
        if (model != null) {
            txtBrand.setText(model.name)
           // autoCompleteCat.setText(categoryName)
        }
        val catAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            categoryList ?: emptyList()
        )
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = catAdapter
        // Set initial selection
        val index = categoryList?.indexOfFirst { it.equals(categoryName, ignoreCase = true) } ?: -1
        if (index >= 0) {
            spinnerCat.setSelection(index)
        }

        txtBrand.requestFocus()
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            val newName = txtBrand.text.toString().uppercase().trim()
            val selectedCategory = spinnerCat.selectedItem?.toString()?.uppercase()?.trim() ?: ""

            if (model != null) {
                model.name=newName
                if (newName.isNotEmpty()) {
                    viewModel.updateBrand( model,selectedCategory)  // Use updateFunction with explicit types
                }
            } else {
                if (newName.isNotEmpty()) {
                    viewModel.insertAnItemBrandStock(newName, selectedCategory)  // Use insertFunction with explicit types
                }
            }
        }

        builder.setNegativeButton("No") { dialog, which -> }
        val alert = builder.create()
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
    }

}