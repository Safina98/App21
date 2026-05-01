package com.example.app21try6.grafik.grafikproduct

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGraphicBinding
import com.example.app21try6.grafik.BarChartModelRVAdapter
import com.example.app21try6.grafik.BarChartModelRvListener
import com.example.app21try6.grafik.BarChartUtils
import com.example.app21try6.grafik.ChartRenderer
import com.example.app21try6.grafik.GraphicViewModel
import com.github.mikephil.charting.charts.BarChart
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class GraphicFragment : Fragment() {

    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Companion.Factory }

    private lateinit var layout: View
    private lateinit var binding: FragmentGraphicBinding
    private lateinit var barChart: BarChart
    private lateinit var chartRenderer: ChartRenderer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_graphic,container,false)
        //layout = binding.mainLayout
        barChart = binding.barChartSg
        chartRenderer = ChartRenderer(requireContext())

        val adapter_year = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.tahun)
        )
        val adapter_month = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.bulan)
        )

        val rvAdapter= BarChartModelRVAdapter(BarChartModelRvListener{

        })

        binding.spinnerTahunSg.adapter = adapter_year
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()

        val position = (binding.spinnerTahunSg.adapter as ArrayAdapter<String>).getPosition(year)
        binding.spinnerTahunSg.setSelection(position)

        binding.spinnerBulanSg.adapter = adapter_month

        val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        viewModel.setSelectedMonthValueStok(currentMonth)
        val positionM = (binding.spinnerBulanSg.adapter as ArrayAdapter<String>).getPosition(currentMonth)

        binding.spinnerBulanSg.setSelection(positionM)
        viewModel.setSelectedYearValueStok(year)

        binding.rvChartSg.adapter=rvAdapter


        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                when (parent.id) {
                    R.id.spinner_bulan_sg -> viewModel.setSelectedMonthValueStok(selected)
                    R.id.spinner_tahun_sg -> viewModel.setSelectedYearValueStok(selected)
                    R.id.spinner_category_sg->viewModel.setSelectedCategoryValueStok(selected)
                    R.id.spinner_product_sg->viewModel.setSelectedProductValueStok(selected)

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerTahunSg.onItemSelectedListener = spinnerListener
        binding.spinnerBulanSg.onItemSelectedListener = spinnerListener
        binding.spinnerCategorySg.onItemSelectedListener = spinnerListener
        binding.spinnerProductSg.onItemSelectedListener = spinnerListener

        viewModel.setSelectedMonthValueStok(currentMonth)

        viewModel.categoryEntries.observe(viewLifecycleOwner){it?.let {
            val adapterCategory =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.spinnerCategorySg.adapter = adapterCategory }
        }

        viewModel.productEntries.observe(viewLifecycleOwner){
            val adapterProduct =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.spinnerProductSg.adapter = adapterProduct
        }
        viewModel.selectedStockYearSpinner.observe(viewLifecycleOwner){ value->
            viewModel.newFilterModelList()
        }
        viewModel.selectedStockMonthSpinner.observe(viewLifecycleOwner){ value->
            viewModel.newFilterModelList()
        }
        viewModel.selectedStockCategorySpinner.observe(viewLifecycleOwner){ value->
            viewModel.newFilterModelList()
            viewModel.getProductEntriesStok()
        }
        viewModel.selectedStockProductSpinner.observe(viewLifecycleOwner){
            viewModel.newFilterModelList()
        }

        viewModel.productBCModel.observe(viewLifecycleOwner) { value ->
            rvAdapter.submitList(value)
            BarChartUtils.setup(
                barChart = binding.barChartSg,
                data = value.take(10),
                chartLabel = "terjual"
            )
        }

        return binding.root
    }
}