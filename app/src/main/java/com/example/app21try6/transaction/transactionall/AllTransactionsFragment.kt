package com.example.app21try6.transaction.transactionall

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
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
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val img =  requireActivity().findViewById<ImageView>(R.id.delete_image)
        img.visibility = View.GONE
        val adapter = AllTransactionAdapter(
            AllTransClickListener {
            viewModel.onNavigatetoTransDetail(it.sum_id)
        },
            CheckBoxListenerTransAll{ view, stok ->
            }
        )
        val adapterSpinnerTransAll = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.spinner_all_trans) )
        binding.spinnerD.adapter = adapterSpinnerTransAll
        binding.recyclerViewAllTrans.adapter = adapter
        viewModel.setUiMode(nightModeFlags)
        viewModel.allTransactionSummary.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
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
        binding.searchAllTrans.setQueryHint("Search here...");
        binding.searchAllTrans.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Update the filter with the new search query
                viewModel.filterData(newText)
                return true
            }
        })
        viewModel.isStartDatePickerClicked.observe(viewLifecycleOwner) {
           if (it==true){
               showDatePickerDialog(1)
               viewModel.onStartDatePickerClicked()
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

        viewModel.navigateToTransDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(AllTransactionsFragmentDirections.actionAllTransactionsFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
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
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val startDate = viewModel.selectedStartDate.value
        var endDate = viewModel.selectedEndDate.value
        viewModel.updateRv4()
        var list = viewModel.allTransactionSummary.value
        Log.i("DateProb","on resume")
        Log.i("DateProb","on resume start date: $startDate")
        Log.i("DateProb","on resume End date: $endDate")
    }
    override fun onPause() {
       // viewModel.cancelJob()
        Log.i("DateProb","on pause")
        super.onPause()
    }
}