package com.example.app21try6.transaction.transactionproduct

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionProductBinding
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModelFactory


class TransactionProductFragment : Fragment() {
    private lateinit var binding : FragmentTransactionProductBinding
    private lateinit var viewModel: TransactionSelectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_product,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource = SummaryDatabase.getInstance(application).summaryDbDao
        val dataSource1 = VendibleDatabase.getInstance(application).categoryDao
        val dataSource2 = VendibleDatabase.getInstance(application).productDao
        val dataSource3 = VendibleDatabase.getInstance(application).brandDao
        val dataSource4 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource5 = VendibleDatabase.getInstance(application).transSumDao
        val dataSource6 = VendibleDatabase.getInstance(application).transDetailDao
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }
        var datee  = date!!.toMutableList()
/*
        val viewModelFactory = TransactionSelectViewModelFactory(date[0].toInt()!!,dataSource1,dataSource2,dataSource4,date,dataSource6,application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TransactionSelectViewModel::class.java)


 */
        viewModel = ViewModelProvider(requireActivity(), TransactionSelectViewModelFactory(date[0].toInt()!!,dataSource1,dataSource2,dataSource4,date,dataSource6,application))
            .get(TransactionSelectViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setProductSumId(date[0]?.toInt())
        Log.i("CHECKBOXPROB","productFragment date: $date")
        val adapter = TransactionProductAdapter(ProductTransListener {
            viewModel.setProductId(it.product_id)
            viewModel.onNavigatetoTransSelect(it.product_id.toString())
        })
        binding.transproductRv.adapter  = adapter
        binding.searchBarProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterProduct(newText)
                return true
            }
        })


        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Update the selected value in your ViewModel
                viewModel.setSelectedKategoriValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when nothing is selected
            }
        }
        viewModel.categoryEntries.observe(viewLifecycleOwner){it?.let {
            val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
            binding.spinnerCategory.adapter = adapterCategory

        }
        }
        viewModel.selectedKategoriSpinner.observe(viewLifecycleOwner) {
            // Handle the selected value
            viewModel.updateRv(it)
        }
        viewModel.productId.observe(viewLifecycleOwner){
           // viewModel.getTransModel(it)
        }

        viewModel.allProduct.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.sortedBy { it.product_name })
        })

        viewModel.navigateToTransSelect.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(TransactionProductFragmentDirections.actionTransactionProductFragmentToTransactionSelectFragment(it))
                viewModel.onNavigatedtoTransSelect()

            }
        })



        return binding.root
    }


}