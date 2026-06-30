package com.example.app21try6.grafik.grafikomzet

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGrapichOmzetBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.example.app21try6.grafik.LineChartHelper
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class GrapichOmzetFragment : Fragment() {
    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }

    private lateinit var layout: View
    private lateinit var binding: FragmentGrapichOmzetBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_grapich_omzet,container,false)
        layout = binding.mainLayout
        binding.lifecycleOwner = this
        //getCustomerSpinnerEntries

        var isItemSelected = false
        val spinnerTahun = binding.spinnerTahunOg
        var isKeyboardOpen = false
       // val spinnerCustomer = binding.spinnerCustomerPg
        binding.customerAutoComplete.threshold = 1

        binding.customerAutoComplete.setOnClickListener {
            binding.customerAutoComplete.showDropDown()
        }

        binding.customerAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            isItemSelected = false
            if (hasFocus) binding.customerAutoComplete.showDropDown()
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyboardOpen = keypadHeight > screenHeight * 0.15
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        spinnerTahun.adapter=adapter_year
        val positionY = (spinnerTahun.adapter as ArrayAdapter<String>).getPosition(currentYear)
        spinnerTahun.setSelection(positionY)
        //viewModel.filterProfitModelList()
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
                    R.id.spinner_tahun_og -> viewModel.setSelectedYearValueOmzet(selected)
                   // R.id.spinner_customer_pg -> viewModel.setSelectedCustomerValueProfit(selected)


                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTahun.onItemSelectedListener = spinnerListener
        binding.customerAutoComplete.dropDownVerticalOffset = 0


        binding.customerAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            viewModel.setSelectedCustomerValueOmzet(selected)
            isItemSelected = true
            // hide keyboard
            binding.customerAutoComplete.dismissDropDown()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.customerAutoComplete.windowToken, 0)
            binding.customerAutoComplete.clearFocus()

        }
        binding.customerAutoComplete.setOnDismissListener {
            if (isItemSelected) return@setOnDismissListener
            if (isKeyboardOpen) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.customerAutoComplete.windowToken, 0)
                binding.customerAutoComplete.postDelayed({
                    binding.customerAutoComplete.showDropDown()
                }, 300)
            }
        }
        //spinnerCustomer.onItemSelectedListener=spinnerListener
       // viewModel.setSelectedYearValueProfit(currentYear)

        viewModel.customerEntries.observe(viewLifecycleOwner){it?.let {
            val adapter = ArrayAdapter(requireContext(), R.layout.item_list, it)
            binding.customerAutoComplete.setAdapter(adapter)
            }
        }

        viewModel.selectedOmzetYearSpinner.observe(viewLifecycleOwner){ value->
           viewModel.filterOmzetModelList()
        }
        viewModel.selectedCustomerOmzet.observe(viewLifecycleOwner){
            viewModel.filterOmzetModelList()
        }

        viewModel.omzetBCModel.observe(viewLifecycleOwner){ value->
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