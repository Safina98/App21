package com.example.app21try6.grafik.grafikprofit

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGraphicProfitBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.LineChartHelper
import java.util.Calendar
import kotlin.getValue


class GraphicProfitFragment : Fragment() {

    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }

    private lateinit var layout: View
    private lateinit var binding: FragmentGraphicProfitBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_graphic_profit,container,false)
        layout = binding.mainLayout
        binding.lifecycleOwner = this
        // Inflate the layout for this fragment
        Log.i("chartprobs","On Profit Fragment")
        var isItemSelected = false
        val spinnerTahun = binding.spinnerTahunPg
        var isKeyboardOpen = false
        // val spinnerCustomer = binding.spinnerCustomerPg
//        binding.customerAutoCompletepg.threshold = 1
//        binding.customerAutoCompletepg.setOnClickListener {
//            binding.customerAutoCompletepg.showDropDown()
//        }
//        binding.customerAutoCompletepg.setOnFocusChangeListener { _, hasFocus ->
//            isItemSelected = false
//            if (hasFocus) binding.customerAutoCompletepg.showDropDown()
//        }
//        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
//            val rect = android.graphics.Rect()
//            binding.root.getWindowVisibleDisplayFrame(rect)
//            val screenHeight = binding.root.rootView.height
//            val keypadHeight = screenHeight - rect.bottom
//            isKeyboardOpen = keypadHeight > screenHeight * 0.15
//        }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        spinnerTahun.adapter=adapter_year
        val positionY = (spinnerTahun.adapter as ArrayAdapter<String>).getPosition(currentYear)
        spinnerTahun.setSelection(positionY)

        val chart=binding.lineChartPg
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
                    // R.id.spinner_customer_pg -> viewModel.setSelectedCustomerValueProfit(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinnerTahun.onItemSelectedListener = spinnerListener
//        binding.customerAutoCompletepg.dropDownVerticalOffset = 0
//
//        binding.customerAutoCompletepg.setOnItemClickListener { parent, _, position, _ ->
//            val selected = parent.getItemAtPosition(position).toString()
//            viewModel.setSelectedCustomerValueProfit(selected)
//            isItemSelected = true
//            // hide keyboard
//            binding.customerAutoCompletepg.dismissDropDown()
//            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(binding.customerAutoCompletepg.windowToken, 0)
//            binding.customerAutoCompletepg.clearFocus()
//
//        }
//        binding.customerAutoCompletepg.setOnDismissListener {
//            if (isItemSelected) return@setOnDismissListener
//            if (isKeyboardOpen) {
//                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(binding.customerAutoCompletepg.windowToken, 0)
//                binding.customerAutoCompletepg.postDelayed({
//                    binding.customerAutoCompletepg.showDropDown()
//                }, 300)
//            }
//        }

        viewModel.selectedProfitYearSpinner.observe(viewLifecycleOwner){ value->
            viewModel.filterProfitModelList()

        }
        viewModel.profitBCModel.observe(viewLifecycleOwner){ value->
            //use this data to create line chart
            LineChartHelper.setData(
                chart = chart,
                entries =value,
                lineLabel = "Profit",
                lineColor = ContextCompat.getColor(requireContext(), R.color.black),
                fillColor = ContextCompat.getColor(requireContext(), R.color.grey_green)
            )

            value?.forEach {
                if (it.value>0.05){
                   // Log.i("chartprobs","${it.label}  ${it.value}")
                }

            }
        }
        return binding.root
    }
}