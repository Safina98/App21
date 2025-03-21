package com.example.app21try6.transaction.transactionproduct

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
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
        val stockRepo= StockRepositories(application)
        val transRepo= TransactionsRepository(application)
        val sumAndProductId= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }
        var datee  = sumAndProductId!!.toMutableList()
        Log.i("SUMIDPROB","TransactionProductFragment arguments $sumAndProductId[0]")

        viewModel = ViewModelProvider(requireActivity(), TransactionSelectViewModelFactory(stockRepo,transRepo,sumAndProductId[0].toInt()!!,sumAndProductId,application))
            .get(TransactionSelectViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setProductSumId(sumAndProductId[0]?.toInt())

        val adapter = TransactionProductAdapter(ProductTransListener {
            Log.i("SelectedRvPos","id: ${it.product_id}")
            viewModel.getTransModel(it.product_id)
            viewModel.setProductId(it.product_id)
            viewModel.saveSelectedItemId(it.product_id)
            viewModel.saveSelectedItemPosition(it.product_id)
            viewModel.onNavigatetoTransSelect(it.product_id.toString())

        })
        binding.transproductRv.adapter  = adapter
        binding.transproductRv.layoutManager = LinearLayoutManager(context)

        binding.searchBarProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterProduct(newText)
                return true
            }
        })
        //selected spinner
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Log.i("PRODUCTBUG", "selected item $selectedItem")
                viewModel.setSelectedKategoriValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewModel.categoryEntries.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
                binding.spinnerCategory.adapter = adapterCategory
                // Set the selected item to match the observed value
                viewModel.selectedKategoriSpinner.value?.let { selectedCategory ->
                    binding.spinnerCategory.setSelection(adapterCategory.getPosition(selectedCategory))
                }
            }
        }

        viewModel.selectedKategoriSpinner.observe(viewLifecycleOwner) { selectedCategory ->
            selectedCategory?.let {
                //val position = (binding.spinnerCategory.adapter as ArrayAdapter<String>).getPosition(it)
                //binding.spinnerCategory.setSelection(position)
                viewModel.updateRv(selectedCategory)
            }
        }

        viewModel.productId.observe(viewLifecycleOwner){
           // viewModel.getTransModel(it)
        }

        viewModel.allProduct.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.sortedBy { it.product_name })
            (binding.transproductRv.layoutManager as LinearLayoutManager).scrollToPosition(viewModel.selectedItemPosition)
            Log.i("SelectedRvPos","TransactionpPRODDUCT PRODUCT ${viewModel.selectedItemPosition}")
        })

        viewModel.navigateToTransSelect.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                clearSearchQuery()
                this.findNavController().navigate(TransactionProductFragmentDirections.actionTransactionProductFragmentToTransactionSelectFragment(it))
                viewModel.onNavigatedtoTransSelect()

            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Your additional code before navigating back
            //Toast.makeText(requireContext(),"BackPressed",Toast.LENGTH_SHORT).show()
            //viewModel.cancelUiScope()
            // Navigate up using NavController
            viewModel.resetSelectedSpinnerValue()
            findNavController().navigateUp()
        }
    }
    // Handle the navigate up button in the app bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            // Home is the ID for the navigate up button
            //viewModel.cancelUiScope()
            //Toast.makeText(requireContext(),"BackPressed",Toast.LENGTH_SHORT).show()
            viewModel.resetSelectedSpinnerValue()
            findNavController().navigateUp()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun clearSearchQuery() {
        binding.searchBarProduct.setQuery("", false)
        binding.searchBarProduct.clearFocus()
    }

}