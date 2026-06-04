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
import com.example.app21try6.grafik.BarChartModelRVAdapter
import com.example.app21try6.grafik.BarChartModelRvListener
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.LineChartHelper
import com.example.app21try6.utils.setupDropdown
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
        //val spinnerProduct=binding.spinnerProductPt
        val spinnerCategory=binding.spinnerCategoryPt
        val spinnerSp=binding.spinnerSpPt
        spinnerTahun.adapter=adapter_year
        val positionY = (spinnerTahun.adapter as ArrayAdapter<String>).getPosition(currentYear)
        spinnerTahun.setSelection(positionY)

        val adapter = BarChartModelRVAdapter(BarChartModelRvListener{

        })
        val adapterAtc = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>())
        binding.actProductPt.setAdapter(adapterAtc)
        binding.actProductPt.threshold = 1
        binding.rvChartPt.adapter=adapter

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
                    R.id.spinner_tahun_pt -> viewModel.setSelectedYearPt(selected)
                    R.id.spinner_category_pt-> viewModel.setSelectedCategoryPt(selected)
                   // R.id.spinner_product_pt-> viewModel.setSelectedProductPt(selected)
                    R.id.spinner_sp_pt-> viewModel.setSelectedSP(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTahun.onItemSelectedListener=spinnerListener
       // spinnerProduct.onItemSelectedListener=spinnerListener
        spinnerCategory.onItemSelectedListener=spinnerListener
        spinnerSp.onItemSelectedListener=spinnerListener
        binding.actProductPt.setupDropdown { selected ->
            viewModel.setSelectedProductPt(selected)
        }
        viewModel.setSelectedProductPt("ALL")
        viewModel.categoryEntries.observe(viewLifecycleOwner){it?.let {
            val adapterCategory =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            spinnerCategory.adapter = adapterCategory }
        }
        viewModel.productEntries.observe(viewLifecycleOwner){
//            val adapterProduct =
//                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
//            spinnerProduct.adapter = adapterProduct
            adapterAtc.clear()
            adapterAtc.addAll(it)
            adapterAtc.notifyDataSetChanged()
        }
        viewModel.spEntries.observe(viewLifecycleOwner){
            val adapterProduct =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            spinnerSp.adapter = adapterProduct
        }

        viewModel.selectedYearSpinnerPt.observe(viewLifecycleOwner){ value->
            viewModel.filterProductTrend()
        }
        viewModel.selectedCategorySpinnerPt.observe(viewLifecycleOwner){value->
            viewModel.getProductEntriesStok(value)
            viewModel.filterProductTrend()
        }
        viewModel.selectedProductSpinnerPt.observe(viewLifecycleOwner){value->
            viewModel.getSPEntriesStok(value)
            viewModel.filterProductTrend()
            Log.i("chartprobs","product trend $value")
            if (value!="ALL"){
                binding.rvChartPt.visibility= View.VISIBLE
            }
        }
        viewModel.selectedSPpinnerPt.observe(viewLifecycleOwner){value->
            viewModel.filterProductTrend()
        }

        viewModel.custCountProductLit.observe(viewLifecycleOwner){value->
            value?.let {
                value.forEach {
                    Log.i("chartprobs","customer count $it")
                }

                adapter.submitList(it)
            }
        }
        viewModel.productTrendBCModel.observe(viewLifecycleOwner){value->
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