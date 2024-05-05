package com.example.app21try6.transaction.transactionall

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.editdetail.BookkeepingViewModel
import com.example.app21try6.databinding.FragmentAllTransactionsBinding
import com.example.app21try6.databinding.TransactionAllItemListBinding
import com.example.app21try6.transaction.transactionactive.ActiveClickListener
import com.example.app21try6.transaction.transactionactive.CheckBoxListenerTransActive
import com.example.app21try6.transaction.transactionactive.TransactionActiveAdapter
import com.example.app21try6.transaction.transactionactive.TransactionActiveFragmentDirections
import java.util.Calendar

class AllTransactionsFragment : Fragment() {
    private lateinit var binding: FragmentAllTransactionsBinding
    private val viewModel  : AllTransactionViewModel by activityViewModels { AllTransactionViewModel.Factory }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_transactions,container,false)
        // Inflate the layout for this fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = AllTransactionAdapter(AllTransClickListener {
            viewModel.onNavigatetoTransDetail(it.sum_id)
        }
            , CheckBoxListenerTransAll{ view, stok ->
                val cb = view as CheckBox
            }
        )
        binding.recyclerViewAllTrans.adapter = adapter

        viewModel.allTransactionSummary.observe(viewLifecycleOwner){
            if (it!=null) {

                adapter.submitList(it)
            }
        }
        viewModel.navigateToTransDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(AllTransactionsFragmentDirections.actionAllTransactionsFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
        }

        viewModel.is_date_picker_clicked.observe(viewLifecycleOwner) {
            if (it == true) {
                showDatePickerDialog()
                viewModel.onDatePickerClicked()
            }
        }
        viewModel.selectedStartDate.observe(viewLifecycleOwner) {

        }
        viewModel.selectedEndDate.observe(viewLifecycleOwner) {
            viewModel.updateRv4()

        }
        return binding.root

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.date_picker)


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startDate = Calendar.getInstance().apply {
                    set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                }.time
                val endDate = Calendar.getInstance().apply {
                    set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                }.time

              //  viewModel.setSelectedBulanValue("Date Range")
                viewModel.setDateRange(startDate, endDate)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}