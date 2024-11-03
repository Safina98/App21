package com.example.app21try6.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.app21try6.R
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
            val newName = textKet.text.toString().toUpperCase().trim()

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


}