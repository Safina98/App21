package com.example.app21try6.grafik.grafikprofit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGrapichProfitBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.StockModel
import com.example.app21try6.grafik.MonthValueFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate


class GrapichProfitFragment : Fragment() {
    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }
    private var listStockModel = mutableListOf<StockModel>()
    private lateinit var layout: View
    private lateinit var binding: FragmentGrapichProfitBinding
    private lateinit var topfivemap :Map<String,Double>
    private lateinit var lineChart:LineChart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_grapich_profit,container,false)
        layout = binding.mainLayout
        lineChart = binding.lineChart
        binding.lifecycleOwner = this

        binding.spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedYearValueProfit(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        viewModel.summariesLiveData.observe(viewLifecycleOwner){
        }
        viewModel.combinedStockLiveData.observe(viewLifecycleOwner){
            if (it!=null){
                viewModel.populateListModelProfit()
            }
        }

        viewModel.filteredmodelListProfit.observe(viewLifecycleOwner){it?.let {
            Log.i("ChartProb","filteredmodeListProfit: $it")
            val monthlyIncome = viewModel.calculateTotalItemCountProfit(it)
                //viewModel.mapAndSumByMonth(it)
           // Log.i("ChartProb","monhtly income: $it")
            setupLineChart(monthlyIncome)
        } }
        viewModel.monthIncomeMap.observe(viewLifecycleOwner){it?.let {

        }}

        viewModel.selectedProfitYearSpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterModelListProfit()
        }
        viewModel.yearProfitEntries.observe(viewLifecycleOwner){it?.let {
            val adapterYear = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it.sortedBy { it })
            binding.spinnerTahun.adapter = adapterYear }
        }

        return binding.root
    }

    private fun setupLineChart(monthlyIncome: Map<String, Double>) {
        val entries = ArrayList<Entry>()
        val months = monthlyIncome.keys.toList().sortedBy { getMonthNumber(it) }
        months.forEachIndexed { index, month ->
            val income = monthlyIncome[month] ?: 0.0
            entries.add(Entry(index.toFloat(), income.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Monthly Income")
        dataSet.color = ColorTemplate.getHoloBlue()
        dataSet.valueTextSize = 16f

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f
        xAxis.valueFormatter = MonthValueFormatter() // Use the custom formatter

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.granularity = 1f
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.textSize = 12f

        val yAxisRight = lineChart.axisRight
        yAxisRight.isEnabled = false

        lineChart.description.isEnabled = false
        lineChart.invalidate() // Refresh the chart
    }

    private fun getMonthNumber(month: String): Int {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return months.indexOf(month) + 1
    }

}