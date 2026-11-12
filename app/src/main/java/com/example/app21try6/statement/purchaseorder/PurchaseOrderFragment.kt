package com.example.app21try6.statement.purchaseorder

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentPurchaseOrderBinding
import com.example.app21try6.utils.setupMultiSelect

val pOTag="OrderProbs"
class PurchaseOrderFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseOrderBinding
    private lateinit var viewModel: PurchaseOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_purchase_order,container,false)
        val application = requireNotNull(this.activity).application
        val repository = StockRepositories(application)
        val expenseRepository= ExpensesRepository(application)

        viewModel = ViewModelProvider(requireActivity(),
            PurchaseOrderViewModelFactory(repository,expenseRepository, application))
            .get(PurchaseOrderViewModel::class.java)
        binding.viewModel=viewModel
        binding.lifecycleOwner =this
        viewModel.updateProduct()

        val adapter= PurchaseOrderAdapter(POModelListener{})
        binding.poRv.adapter=adapter


        viewModel.suplierList.observe(viewLifecycleOwner){supliers->

            if (supliers != null) {
                val adapterSuplier = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    supliers
                )
                binding.textSuplier.setAdapter(adapterSuplier)
            }
        }

        viewModel.items.observe(viewLifecycleOwner){items->
            adapter.submitList(items)
            viewModel.draftText()
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it==true){
               // hide keyboard
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            }
        }

        // categories: List<Category>, loaded from your DB / ViewModel
        viewModel.categoryOptions.observe(viewLifecycleOwner) { options ->
            Log.i(pOTag,"category ${options}")
            setupMultiSelect(
                context = requireContext(),
                inputLayout = binding.ilCategory,
                editText = binding.etCategory,
                items = options,
                label = { it.name },
                getChecked = { it.isChecked },
                setChecked = { item, checked -> item.isChecked = checked },
                onChanged = { selected ->
                    viewModel.setSelectedCategories(selected.map { it.name }.toSet())
                }
            )

        }
        viewModel.productOptions.observe(viewLifecycleOwner){
                options ->
            Log.i(pOTag,"brand ${options}")
            if (options!=null)
            setupMultiSelect(
                context = requireContext(),
                inputLayout = binding.ilBrand,
                editText = binding.etBrand,
                items = options,
                label = { it.name },
                getChecked = { it.isChecked },
                setChecked = { item, checked -> item.isChecked = checked },
                onChanged = { selected ->
                    viewModel.setSelectedBrands(selected.map { it.name }.toSet())
                }
            )
        }

        return binding.root
    }
}

//advanced filtering
//filter by day, input the number of day, pass it as a parameter to the repository
//filter by brand and category
//get a list of brand and category name and id from database
//filter the item in the model based on the selected item in the list