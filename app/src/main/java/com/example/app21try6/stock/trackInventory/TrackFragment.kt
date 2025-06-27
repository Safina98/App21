package com.example.app21try6.stock.trackInventory

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.SIMPLE_DATE_FORMATTER
import com.example.app21try6.databinding.FragmentTrackBinding
import com.example.app21try6.grafik.GraphicViewModel
import java.util.Calendar


class TrackFragment : Fragment() {
    private lateinit var binding: FragmentTrackBinding
    private val viewModel: TrackViewModel by  activityViewModels { TrackViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_track,container,false)
        binding.viewModel= viewModel
        binding.lifecycleOwner=this
        viewModel.selectedStartDate.observe(viewLifecycleOwner) {}
        viewModel.selectedEndDate.observe(viewLifecycleOwner) {}

        viewModel.allSubTrans.observe(viewLifecycleOwner){
            it?.let {
                it.forEach {
                    Log.i("SubTrans","itemName: ${it.trans_item_name}, net:${it.qty} ,date:${SIMPLE_DATE_FORMATTER.format(it.trans_detail_date)}")
                }
            }
        }
        viewModel.isStartDatePickerClicked.observe(viewLifecycleOwner) {
            if (it==true){
                showDatePickerDialog()
                viewModel.onStartDatePickerClicked()
            }
        }
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
                viewModel.updateRv()

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