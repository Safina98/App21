package com.example.app21try6.statement.purchase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.purchaseDummy
import com.example.app21try6.databinding.FragmentStatementHsBinding
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory


class TransactionPurchase : Fragment() {
    private lateinit var binding:FragmentTransactionPurchaseBinding
    private lateinit var viewModel: PurchaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_purchase,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner=this
        val id= arguments?.let { TransactionPurchaseArgs.fromBundle(it).id }?:-1
        val dataSource3 = VendibleDatabase.getInstance(application).expenseDao
        val dataSource4 = VendibleDatabase.getInstance(application).expenseCategoryDao
        val dataSource5 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource6 = VendibleDatabase.getInstance(application).productDao
        val viewModelFactory = PurchaseViewModelFactory(application,id,dataSource3,dataSource4,dataSource5,dataSource6)
        viewModel = ViewModelProvider(this,viewModelFactory).get(PurchaseViewModel::class.java)
        binding.viewModel=viewModel
        val adapter =PurchaseAdapter(
            UpdateListener {
                viewModel.rvClick(it)
                //Log.i(tagp,"${productName.value}")
            },
            DeleteListener {  })
        binding.purchaseRv.adapter=adapter
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.purchaseRv.addItemDecoration(dividerItemDecoration)
        val autoCompleteSuplier: AutoCompleteTextView = binding.textDiscount
        val autoCompleteSubName: AutoCompleteTextView = binding.textSub
        viewModel.allSubProductFromDb.observe(viewLifecycleOwner) { subProductList ->
            if (subProductList!=null){
                val subNames = subProductList.map { it.subProduct.sub_name }
                val adapterSub = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subNames)
                autoCompleteSubName.setAdapter(adapterSub)
            }
        }
        viewModel.isAddItemClick.observe(viewLifecycleOwner){
            if (it==true){
                adapter.notifyDataSetChanged()
                viewModel.onItemAdded()
            }
        }


        autoCompleteSubName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val subName = s.toString().trim().uppercase()
                viewModel.setProductPriceAndNet(subName)
            }
        })
        val supNames=viewModel.suplierDummy.map { it.suplierName }
        val adapterSuplier = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, supNames)
        autoCompleteSuplier.setAdapter(adapterSuplier)

        viewModel.inventoryPurchaseList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        //adapter.submitList(purchaseDummy)
        binding.btnAdd.setOnClickListener {
        }
        viewModel.isNavigateToExpense.observe(viewLifecycleOwner){
            if (it==true){
                this.findNavController().navigate(TransactionPurchaseDirections.actionTransactionPurchaseToExpensesFragment())
                viewModel.onNavigatedToExpense()
            }
        }

        return binding.root
    }


}