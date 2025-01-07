package com.example.app21try6.grafik

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGraphicBinding
import com.github.mikephil.charting.charts.BarChart
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
class GraphicFragment : Fragment() {

    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }
    private var listStockModel = mutableListOf<StockModel>()
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
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_graphic,container,false)
        layout = binding.mainLayout
        barChart = binding.barChart
        chartRenderer = ChartRenderer(requireContext())

        val adapter_year = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        val adapter_month = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.bulan))


        binding.spinnerTahun.adapter = adapter_year
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()

        val position = (binding.spinnerTahun.adapter as ArrayAdapter<String>).getPosition(year)
        binding.spinnerTahun.setSelection(position)

        binding.spinnerBulan.adapter = adapter_month

        val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        viewModel.setSelectedMonthValueStok(currentMonth)
        val positionM = (binding.spinnerBulan.adapter as ArrayAdapter<String>).getPosition(currentMonth)
        binding.spinnerBulan.setSelection(positionM)
        viewModel.setSelectedYearValueStok(year)



        binding.spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedYearValueStok(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedMonthValueStok(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        viewModel.setSelectedMonthValueStok(currentMonth)
        binding.spinnerCategory.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedCategoryValueStok(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerProduct.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedProductValueStok(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        viewModel.transDetailModel.observe(viewLifecycleOwner){list->
            list?.forEach {

            }

        }

       // viewModel.modelList.observe(viewLifecycleOwner) {}
        viewModel.combinedStockLiveData.observe(viewLifecycleOwner){
            if (it!=null){
                viewModel.getCurrentYearAndMothData()
            }
        }
        viewModel.summariesLiveData.observe(viewLifecycleOwner){it?.let {} }
        viewModel.categoryEntries.observe(viewLifecycleOwner){it?.let {
            val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.spinnerCategory.adapter = adapterCategory }
        }

        viewModel.productEntries.observe(viewLifecycleOwner){
            val adapterProduct = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.spinnerProduct.adapter = adapterProduct
        }
        viewModel.selectedStockYearSpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterModelListStok()
        }
        viewModel.selectedStockMonthSpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterModelListStok()
        }
        viewModel.selectedStockCategorySpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterModelListStok()
            viewModel.getProductEntriesStok()
        }
        viewModel.selectedStockProductSpinner.observe(viewLifecycleOwner){
            viewModel.filterModelListStok()
        }
        viewModel.filteredmodelList.observe(viewLifecycleOwner) { value ->
            viewModel.calculateTotalItemCountStok(listStockModel,"","")
        }
        viewModel.mapModel.observe(viewLifecycleOwner) { value ->
            viewModel.getTopEightItemsStok(value)
        }
        viewModel.topEightMap.observe(viewLifecycleOwner) { value ->
            chartRenderer.showChart(barChart, value)
        }
        return binding.root
    }
}