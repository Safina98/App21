package com.example.app21try6.statement.report

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentReportBinding
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding
import com.example.app21try6.statement.purchase.PurchaseViewModel
import com.example.app21try6.statement.purchase.PurchaseViewModelFactory
import java.util.Calendar


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: ReportViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_report,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner=this
        val expenseRepo= ExpensesRepository(application)
        val transRepo= TransactionsRepository(application)
        val viewModelFactory = ReportViewModelFactory(application,expenseRepo,transRepo)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ReportViewModel::class.java)
        binding.viewModel=viewModel
        val adapterHPP= ReportAdapter(ReportListener{})
        val adapterBOP=ReportAdapter(ReportListener{})
        binding.recyclerViewHpp.adapter=adapterHPP
        binding.recyclerViewBebanOp.adapter=adapterBOP

        viewModel.HPPList.observe(viewLifecycleOwner){
            adapterHPP.submitList(it)

        }
        viewModel.BOPList.observe(viewLifecycleOwner){
            adapterBOP.submitList(it)
        }
        viewModel.isDatePickerClicked.observe(viewLifecycleOwner){
            showDatePickerDialog()
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
                viewModel.getCategoryByType()

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
}

