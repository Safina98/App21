package com.example.app21try6.grafik.graphictrend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGraphicProductTrendBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.LineChartHelper
import java.util.Calendar
import kotlin.getValue


class GraphicProductTrendFragment : Fragment() {
    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val binding: FragmentGraphicProductTrendBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_graphic_product_trend,container,false)
        binding.lifecycleOwner = this
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        val spinnerTahun=binding.spinnerTahunPt
        val spinnerProduct=binding.spinnerProductPt
        val spinnerCategory=binding.spinnerCategoryPt
        val spinnerSp=binding.spinnerSpPt
        spinnerTahun.adapter=adapter_year
        val positionY = (spinnerTahun.adapter as ArrayAdapter<String>).getPosition(currentYear)
        spinnerTahun.setSelection(positionY)

        val chart=binding.lineChartPt
        LineChartHelper.setupChart(
            chart = chart,
            context = requireContext(),
            showLegend = true
        )
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                when (parent.id) {
                    R.id.spinner_tahun_pt -> viewModel.setSelectedYearValueProfit(selected)
                    R.id.spinner_category_pt-> viewModel.setSelectedCategoryValueStok(selected)
                    R.id.spinner_product_pt-> viewModel.setSelectedProductValueStok(selected)
                    R.id.spinner_sp_pt-> viewModel.setSelectedSPValue(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTahun.onItemSelectedListener=spinnerListener
        spinnerProduct.onItemSelectedListener=spinnerListener
        spinnerCategory.onItemSelectedListener=spinnerListener
        spinnerSp.onItemSelectedListener=spinnerListener

        viewModel.categoryEntries.observe(viewLifecycleOwner){it?.let {
            val adapterCategory =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            spinnerCategory.adapter = adapterCategory }
        }
        viewModel.productEntries.observe(viewLifecycleOwner){

            val adapterProduct =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            spinnerProduct.adapter = adapterProduct
        }
        viewModel.spEntries.observe(viewLifecycleOwner){
            val adapterProduct =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            spinnerSp.adapter = adapterProduct
        }

        viewModel.selectedProfitYearSpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterProductTrend()
        }
        viewModel.selectedStockCategorySpinner.observe(viewLifecycleOwner){value->
            viewModel.getProductEntriesStok()
            viewModel.filterProductTrend()
        }
        viewModel.selectedStockProductSpinner.observe(viewLifecycleOwner){value->
            viewModel.getSPEntriesStok()
            viewModel.filterProductTrend()
        }
        viewModel.selectedSPpinner.observe(viewLifecycleOwner){value->
            viewModel.filterProductTrend()
        }

        viewModel.profitBCModel.observe(viewLifecycleOwner){value->
            //use this data to create line chart
            LineChartHelper.setData(
                chart = chart,
                entries =value,
                lineLabel = "Product Trend",
                lineColor = ContextCompat.getColor(requireContext(), R.color.black),
                fillColor = ContextCompat.getColor(requireContext(), R.color.grey_green)
            )
        }

        return binding.root
    }

}