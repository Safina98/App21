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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentAllTransactionsBinding
import com.example.app21try6.transaction.transactiondetail.TransactionDetailFragment
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
            AllTransClickListener {transaction->
                if (binding.transactionDetailFragmentContainer != null) {
                    val bundle = Bundle().apply {
                        putInt("id", transaction.sum_id)
                    }
                    val transactionDetailFragment = TransactionDetailFragment().apply {
                        arguments = bundle
                    }
                    childFragmentManager.beginTransaction()
                        .replace(binding.transactionDetailFragmentContainer!!.id, transactionDetailFragment)
                        .commit()
                    //for toggle color
                    viewModel.getSelectedTransSumId(transaction.sum_id)

                } else {
                    viewModel.onNavigatetoTransDetail(transaction.sum_id)
                }

            },
            CheckBoxListenerTransAll { view, stok -> },
            null
        )

        val adapterSpinnerTransAll = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.spinner_all_trans) )
        binding.spinnerD.adapter = adapterSpinnerTransAll
        binding.recyclerViewAllTrans.adapter = adapter
        viewModel.setUiMode(nightModeFlags)
        viewModel.allTransactionSummary.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
                //adapter.notifyDataSetChanged()
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
               // viewModel.updateRv4()
            }
        }

        //limit selected data on recylerview
        binding.recyclerViewAllTrans.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isLoading = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount && dy > 0) {
                    isLoading = true // Set the flag to prevent multiple triggers
                    viewModel.loadMoreData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = false // Reset the flag when scrolling stops
                }
            }
        })

        viewModel.selectedTransSum.observe(viewLifecycleOwner){
            if (it==null) {
                binding.transactionDetailFragmentContainer?.visibility = View.INVISIBLE
            }else{
                binding.transactionDetailFragmentContainer?.visibility = View.VISIBLE
            }
            adapter.selectedItemId = it  // Pass the selected ID to the adapter
            adapter.notifyDataSetChanged()
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
               showDatePickerDialog()
               viewModel.onStartDatePickerClicked()
           }
        }

        viewModel.selectedStartDate.observe(viewLifecycleOwner) {}
        viewModel.selectedEndDate.observe(viewLifecycleOwner) {}

        viewModel.navigateToTransDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(AllTransactionsFragmentDirections.actionAllTransactionsFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
        }
        return binding.root

    }

    private fun showDatePickerDialog() {
        //clearSearchQuery()
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = dialogView.findViewById<DatePicker>(R.id.datePickerEnd)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startYear = datePickerStart.year
                val startMonth = datePickerStart.month
                val startDay = datePickerStart.dayOfMonth
                val endYear = datePickerEnd.year
                val endMonth = datePickerEnd.month
                val endDay = datePickerEnd.dayOfMonth

                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay, 0, 0, 1) // Set time to start of the day
                    set(Calendar.MILLISECOND, 0)
                }.time

                val endDate = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay, 23, 59, 58) // Set time to end of the day
                    set(Calendar.MILLISECOND, 999)
                }.time

                viewModel.updateDateRangeString(startDate,endDate)
                viewModel.setStartAndEndDateRange(startDate,endDate)
                viewModel.updateRv5()

                // viewModel.setEndDateRange(endDate)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.setOnDismissListener {

        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        val startDate = viewModel.selectedStartDate.value
        val endDate = viewModel.selectedEndDate.value
        if (startDate==null){
            viewModel.setSelectedSpinner("Hari Ini")
        }


    }
    override fun onPause() {
       // viewModel.cancelJob()

        super.onPause()
    }
}