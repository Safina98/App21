package com.example.app21try6.transaction.transactionall

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentAllTransactionsBinding
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
        val adapterSpinnerTransAll = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.spinner_all_trans) )
        binding.spinnerD.adapter = adapterSpinnerTransAll

        binding.recyclerViewAllTrans.adapter = adapter

        viewModel.allTransactionSummary.observe(viewLifecycleOwner){
            if (it!=null) {
               // Log.i("DATEPROB","all trans sum $it")
                adapter.submitList(it)
            }
        }

        viewModel.navigateToTransDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(AllTransactionsFragmentDirections.actionAllTransactionsFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
        }

        binding.spinnerD.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedSpinner(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewModel.selectedSpinner.observe(viewLifecycleOwner){
            it?.let {
                viewModel.updateRv4()
            }
        }


        viewModel.isStartDatePickerClicked.observe(viewLifecycleOwner) {
           if (it==true){
               showDatePickerDialog(1)
               viewModel.onDatePickerClicked()
           }
        }
        viewModel.isEndDatePickerClicked.observe(viewLifecycleOwner) {
            if (it==true){
                showDatePickerDialog(2)
                viewModel.onEndDatePickerClicked()
            }
        }
        viewModel.selectedStartDate.observe(viewLifecycleOwner) {
            viewModel.updateRv4()
        }
        viewModel.selectedEndDate.observe(viewLifecycleOwner) {
            viewModel.updateRv4()
        }
        return binding.root

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog(code:Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.date_picker)


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->

                    val startDate = Calendar.getInstance().apply {
                        set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                    }.time
                viewModel.setSelectedSpinner("Date Range")
                if (code==1) viewModel.setStartDateRange(startDate)
                else if (code==2) viewModel.setEndDateRange(startDate)




              //  viewModel.setSelectedBulanValue("Date Range")

            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}