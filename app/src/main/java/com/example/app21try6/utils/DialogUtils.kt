package com.example.app21try6.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.app21try6.R

object DialogUtils{
    fun showDeleteDialog(context:Context,fragment: Fragment, viewModel: ViewModel, item: Any, onDelete: (ViewModel, Any) -> Unit
    ) {
        val builder = AlertDialog.Builder(fragment.requireContext())
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
}