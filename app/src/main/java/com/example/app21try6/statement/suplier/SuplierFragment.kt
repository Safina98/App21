package com.example.app21try6.statement.suplier

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.databinding.FragmentSuplierBinding
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory
import com.example.app21try6.stock.brandstock.BrandStockAdapter
import com.example.app21try6.stock.brandstock.BrandStockListener
import com.example.app21try6.stock.brandstock.BrandStockLongListener
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.stock.brandstock.CategoryAdapter
import com.example.app21try6.stock.brandstock.DeleteListener
import com.example.app21try6.stock.brandstock.StockCategoryModel
import com.example.app21try6.stock.brandstock.UpdateListener
import com.example.app21try6.utils.DialogUtils


class SuplierFragment : Fragment() {
    private lateinit var binding :FragmentSuplierBinding
    private lateinit var viewModel: StatementHSViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_suplier, container,false)

        val application= requireNotNull(this.activity).application

        val discountRepo= DiscountRepository(application)
        val expensesRepository= ExpensesRepository(application)
        val viewModelFactory = StatementHSViewModelFactory(application, discountRepo,expensesRepository)

        val adapter = CategoryAdapter(
            UpdateListener {
                DialogUtils.updateDialog(
                    context = requireContext(),
                    viewModel = viewModel,
                    model = it,
                    title = "Update Suplier",
                    getBrandName = { (it as StockCategoryModel).categoryName },
                    setBrandName = { it, name -> (it as StockCategoryModel).categoryName = name },
                    updateFunction = { vm, item -> (vm as StatementHSViewModel).updateSuplier(item as StockCategoryModel) },
                    insertFunction = { vm, name -> (vm as StatementHSViewModel).insertSuplier(name as String) }
                )
            },DeleteListener {
                DialogUtils.showConfirmationDialog(requireContext(),viewModel, it.id, "Are you sure you want to delete?",{ vm, item -> (vm as StatementHSViewModel).deleteSuplier(item as Long) })
            })

        binding.suplierRv.adapter=adapter

        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel

        viewModel.suplierList.observe(viewLifecycleOwner, Observer{
            if (it!=null){
                adapter.submitList(it)
            }
        })

        viewModel.isAddSuplierClicked.observe(viewLifecycleOwner, Observer{
            Log.i("suplierprobs","$it")
            if (it==true){
                Log.i("suplierprobs","$it")
                    DialogUtils.updateDialog(
                        context = requireContext(),
                        viewModel = viewModel,
                        model = null,
                        title = "Insert Suplier",
                        getBrandName = { (it as StockCategoryModel).categoryName },
                        setBrandName = { it, name -> (it as StockCategoryModel).categoryName = name },
                        updateFunction = { vm, item -> (vm as StatementHSViewModel).updateSuplier(item as StockCategoryModel) },
                        insertFunction = { vm, name -> (vm as StatementHSViewModel).insertSuplier(name as String) }
                    )
                viewModel.onAddSuplierClicked()
        }}
        )
        return binding.root
    }
}