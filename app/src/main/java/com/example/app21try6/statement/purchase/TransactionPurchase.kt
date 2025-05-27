package com.example.app21try6.statement.purchase

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.models.SubWithPriceModel
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.databinding.FragmentStatementHsBinding
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar


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
        val id= arguments?.let { TransactionPurchaseArgs.fromBundle(it).exId }?:-1
        val dataSource3 = VendibleDatabase.getInstance(application).expenseDao
        val dataSource4 = VendibleDatabase.getInstance(application).expenseCategoryDao
        val dataSource5 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource6 = VendibleDatabase.getInstance(application).productDao
        val dataSource7 = VendibleDatabase.getInstance(application).inventoryPurchaseDao
        val dataSource8 = VendibleDatabase.getInstance(application).inventoryLogDao
        val dataSource9 = VendibleDatabase.getInstance(application).detailWarnaDao
        val dataSource10 = VendibleDatabase.getInstance(application).suplierDao

        val viewModelFactory = PurchaseViewModelFactory(application,id,dataSource3,dataSource4,dataSource5,dataSource6,dataSource7,dataSource8,dataSource9,dataSource10)
        viewModel = ViewModelProvider(this,viewModelFactory).get(PurchaseViewModel::class.java)
        binding.viewModel=viewModel

        viewModel.getExpense(id)
        viewModel.getInventoryList(id)

        val autoCompleteSuplier: AutoCompleteTextView = binding.textDiscount
        val autoCompleteSubName: AutoCompleteTextView = binding.textSub

        val adapter =PurchaseAdapter(
            UpdateListener {
                viewModel.rvClick(it)
                //Log.i(tagp,"${productName.value}")
            },
            DeleteListener {
                viewModel.deletePurchase(it)

            })
        binding.purchaseRv.adapter=adapter
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.purchaseRv.addItemDecoration(dividerItemDecoration)
        viewModel.allSubProductFromDb.observe(viewLifecycleOwner) { subProductList ->
            setAutoCompleteSubNameAdapter(subProductList, autoCompleteSubName)
        }
        viewModel.isAddItemClick.observe(viewLifecycleOwner){
            if (it==true){
               // adapter.notifyDataSetChanged()
                viewModel.onItemAdded()
            }
        }
        viewModel.transSumDateLongClick.observe(viewLifecycleOwner){
            if (it==true){
                showDatePickerDialog(null)
            }
        }
        viewModel.isDiscountClicked.observe(viewLifecycleOwner){
            if (it!=null){
                DialogUtils.updateDialog(
                    context = requireContext(),
                    viewModel = viewModel, // Replace with your ViewModel instance
                    model = null,         // Replace with your model instance
                    title = "Update Brand",
                    getBrandName = { (it as InventoryPurchase).subProductName },
                    setBrandName = { it, name -> (it as InventoryPurchase).subProductName = name },
                    updateFunction = { vm, item -> (vm as PurchaseViewModel).addDiscount(0 )},
                    insertFunction = { vm, name -> (vm as PurchaseViewModel).addDiscount(name.toIntOrNull()?:0)}
                )
               viewModel.onDiscountClicked()
            }
        }

        autoCompleteSubName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val subName = s.toString().trim().uppercase()
                viewModel.searchProduct(subName)
                autoCompleteSubName.showDropDown()
            }
        })
        autoCompleteSubName.setOnItemClickListener { _, _, position, _ ->

        }
        viewModel.suplierDummy.observe(viewLifecycleOwner, Observer {list->list?.let {
            val supNames=list.map { it.suplierName }
            val adapterSuplier = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, supNames)
            autoCompleteSuplier.setAdapter(adapterSuplier)
        }
        })


        viewModel.inventoryPurchaseList.observe(viewLifecycleOwner){
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        //adapter.submitList(purchaseDummy)
        binding.btnAdd.setOnClickListener {
            viewModel.onAddClick()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0) // "it" = button's window token
            val snackbar = Snackbar.make(binding.root, "added", Snackbar.LENGTH_SHORT)
            snackbar.duration = 1000 // Duration in milliseconds
            snackbar.show()

        }
        viewModel.isNavigateToExpense.observe(viewLifecycleOwner){
            if (it==true){
               this.findNavController().navigate(TransactionPurchaseDirections.actionTransactionPurchaseToExpensesFragment())
                viewModel.onNavigatedToExpense()
            }
        }
        return binding.root
    }

    private fun setAutoCompleteSubNameAdapter(
        subProductList: List<SubWithPriceModel>?,
        autoCompleteSubName: AutoCompleteTextView
    ) {
        if (!subProductList.isNullOrEmpty()) {
            val subNames = subProductList.map { it.subProduct.sub_name }
            val adapterSub = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                subNames
            )
            autoCompleteSubName.setAdapter(adapterSub)
            adapterSub.notifyDataSetChanged()

        }
    }

    private fun showDatePickerDialog(paymentModel: PaymentModel?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = dialogView.findViewById<DatePicker>(R.id.datePickerEnd)
        datePickerEnd.visibility=View.GONE
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startDate = Calendar.getInstance().apply {
                    set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
                }.time
                viewModel.updateLongClickedDate(startDate)
                viewModel.onTxtTransSumLongClikced()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    override fun onPause() {
        super.onPause()
        viewModel.savePurchase()
    }

}