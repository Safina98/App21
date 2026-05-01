package com.example.app21try6.grafik.grafikprofit

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGrapichProfitBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.LineChartHelper
import com.github.mikephil.charting.charts.LineChart
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class GrapichProfitFragment : Fragment() {
    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }

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
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        binding.spinnerTahunPg.adapter=adapter_year
        val positionY = (binding.spinnerTahunPg.adapter as ArrayAdapter<String>).getPosition(currentYear)
        binding.spinnerTahunPg.setSelection(positionY)
        viewModel.filterProfitModelList()
        val chart=binding.lineChart
        LineChartHelper.setupChart(
            chart = chart,
            context = requireContext(),
            showLegend = true
        )


        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                when (parent.id) {
                    R.id.spinner_tahun_pg -> viewModel.setSelectedYearValueProfit(selected)

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerTahunPg.onItemSelectedListener = spinnerListener
        viewModel.setSelectedMonthValueProfit(currentYear)
        viewModel.selectedProfitYearSpinner.observe(viewLifecycleOwner){ value->
           viewModel.filterProfitModelList()
        }
        viewModel.profitBCModel.observe(viewLifecycleOwner){value->
           //use this data to create line chart
            LineChartHelper.setData(
                chart = chart,
                entries =value,
                lineLabel = "Profit",
                lineColor = ContextCompat.getColor(requireContext(), R.color.black),
                fillColor = ContextCompat.getColor(requireContext(), R.color.grey_green)
            )
        }



        return binding.root
    }


}