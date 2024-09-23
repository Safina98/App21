package com.example.app21try6.statement

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.bindDateFormatted
import com.example.app21try6.databinding.FragmentStatementHsBinding
import com.example.app21try6.databinding.FragmentTransactionSelectBinding
import com.example.app21try6.databinding.PopUpDiscBinding
import com.example.app21try6.stock.productstock.ProductStockAdapter
import com.example.app21try6.stock.productstock.ProductStockListener
import com.example.app21try6.stock.productstock.ProductStockLongListener
import com.example.app21try6.transaction.transactiondetail.TransactionDetailViewModel
import com.example.app21try6.transaction.transactiondetail.TransactionDetailViewModelFactory
import com.example.app21try6.transaction.transactionedit.Code
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel
import com.google.android.material.textfield.TextInputEditText


class StatementHsFragment : Fragment() {
    private lateinit var binding: FragmentStatementHsBinding
    private lateinit var viewModel: StatementHSViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_statement_hs,container,false)
        val application= requireNotNull(this.activity).application
        val viewModelFactory = StatementHSViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel
        val adapter = DiscountAdapter(DiscountListener {  }, DiscountLongListener {  })
        binding.btnAddExpense.setOnClickListener {
            showDiscountDialog()
            adapter.notifyDataSetChanged()
        }
        binding.rvDisc.adapter = adapter
        adapter.submitList(viewModel.dummyDiscList)
        return binding.root
    }
    private fun showDiscountDialog() {
        // Inflate the layout using data binding
        val binding: PopUpDiscBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.pop_up_disc, // Replace with your dialog layout file
            null,
            false
        )

        binding.textDiscValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textDiscQty.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        // Create the dialog using AlertDialog.Builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Enter Discount Details")
            .setPositiveButton("OK") { dialog, _ ->
                // Get values from the input fields
                val discName = binding.textDiscName.text.toString()
                val discValue = binding.textDiscValue.text.toString().toDouble()
                val discMinQty = binding.textDiscValue.text.toString().toDoubleOrNull()
                val selectedDiscType = binding.spinnerM.selectedItem.toString()
                viewModel.insertDiscount(discValue,discName,discMinQty,selectedDiscType)
                // Do something with the input data, e.g., save or pass it
                // ...

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

}