package com.example.app21try6.stock

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.InvalidationTracker
import com.example.app21try6.R
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentInputUpdateProductBinding
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.stock.brandstock.BrandStockViewModelFactory

class InputUpdateProduct : Fragment() {

    private lateinit var binding:FragmentInputUpdateProductBinding
    private lateinit var viewModel: BrandStockViewModel
    private var list= mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_input_update_product,container,false)
        val application = requireNotNull(this.activity).application
        val repository = StockRepositories(application)
        val discountRepository= DiscountRepository(application)
        viewModel = ViewModelProvider(requireActivity(), BrandStockViewModelFactory(repository,discountRepository,application))
            .get(BrandStockViewModel::class.java)
        binding.viewModel=viewModel


        // Observe the ViewModel LiveData and update the adapter

        viewModel.allDiscountFromDB.observe(viewLifecycleOwner, Observer {discounts ->
            if(discounts!=null){
                val adapterr = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    discounts
                )
                //autoCompleteTextView.setAdapter(adapterr)
                binding.textDiscount.setAdapter(adapterr)
            }
        })
        viewModel.cathList_.observe(viewLifecycleOwner){entries->
            if(entries!=null){
                val adapterCat = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    entries
                )
                binding.textCategory.setAdapter(adapterCat)
            }
        }
        binding.textCategory.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position) as String
            viewModel.onCategorySelected(selectedCategory)
        }
        viewModel.brandList.observe(viewLifecycleOwner) { brands ->
            if (brands != null) {
                val adapterBrand = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    brands
                )
                binding.textBrand.setAdapter(adapterBrand)
            }
        }
        viewModel.navigateBackToBrandStok.observe(viewLifecycleOwner, Observer {
            if (it==true){
                requireActivity().onBackPressedDispatcher.onBackPressed()
                viewModel.onNavigatedBackToBrandStock()
            }
        })

        return binding.root
    }

}