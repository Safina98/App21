package com.example.app21try6.grafik.grafikcustomer

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
import com.example.app21try6.databinding.FragmentGraphicCustomerBinding
import com.example.app21try6.formatRupiah
import com.example.app21try6.grafik.BarChartModelRVAdapter
import com.example.app21try6.grafik.BarChartModelRvListener
import com.example.app21try6.grafik.BarChartUtils
import com.example.app21try6.transaction.transactionall.AllTransClickListener
import com.example.app21try6.transaction.transactionall.AllTransactionAdapter
import com.example.app21try6.transaction.transactionall.CheckBoxListenerTransAll
import kotlin.getValue


class GraphicCustomerFragment : Fragment() {
    private val viewModel: GraphicCustomerViewModel by  activityViewModels { GraphicCustomerViewModel.Factory }
    private lateinit var binding: FragmentGraphicCustomerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_graphic_customer,container,false)
        val rvAdapter= BarChartModelRVAdapter(BarChartModelRvListener{

        })
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        val adapter_month = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.bulan))

        binding.spinnerTahunCg.adapter=adapter_year
        binding.spinnerBulanCg.adapter=adapter_month
        binding.rvChartCg.adapter=rvAdapter
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                when (parent.id) {
                    R.id.spinner_bulan_cg -> viewModel.setSelectedBulan(selected)
                    R.id.spinner_tahun_cg -> viewModel.setSelectedYear(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerTahunCg.onItemSelectedListener = spinnerListener
        binding.spinnerBulanCg.onItemSelectedListener = spinnerListener

        viewModel.custWithTotalTrans.observe(viewLifecycleOwner){
            //viewModel.getRvData(it)
        }
        viewModel.rvData.observe(viewLifecycleOwner){value->

        }

        viewModel.barChartModel.observe(viewLifecycleOwner){value->
            rvAdapter.submitList(value)
            BarChartUtils.setup(
                barChart = binding.barChart,
                data = value.take(10),
                chartLabel = "Omzet"
            )

        }
        return binding.root
    }
}