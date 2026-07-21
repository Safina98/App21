package com.example.app21try6.statement.purchase

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.models.SubWithPriceModel
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding
import com.example.app21try6.transaction.transactiondetail.PaymentAdapter
import com.example.app21try6.transaction.transactiondetail.TransDatePaymentClickListener
import com.example.app21try6.transaction.transactiondetail.TransPaymentClickListener
import com.example.app21try6.transaction.transactiondetail.TransPaymentLongListener
import com.example.app21try6.utils.DialogUtils
import com.example.app21try6.utils.SimilarWordAdapter
import com.example.app21try6.utils.SpaceInsensitiveArrayAdapter
import java.util.Calendar


class TransactionPurchase : Fragment() {
    private lateinit var binding:FragmentTransactionPurchaseBinding
    private lateinit var viewModel: PurchaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_purchase,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner=this
        val id= arguments?.let { TransactionPurchaseArgs.fromBundle(it).exId }?:-1

        val stockRepo = StockRepositories(application)
        val expenseRepo= ExpensesRepository(application)
        val logsRepo= LogsRepository(application)

        val viewModelFactory = PurchaseViewModelFactory(application,id,stockRepo,expenseRepo,logsRepo)
        viewModel = ViewModelProvider(this,viewModelFactory).get(PurchaseViewModel::class.java)
        binding.viewModel=viewModel

        viewModel.getExpense(id)
        //.getNewInventoryList(id)

        val autoCompleteSuplier: AutoCompleteTextView = binding.textSuplier
        val autoCompleteSubName: AutoCompleteTextView = binding.textSub

        val adapter =PurchaseAdapter(
            UpdateListener {
                viewModel.rvClick(it)
            },
            DeleteListener {
                DialogUtils.showDeleteDialog(requireContext(),viewModel, it, { vm, item -> (vm as PurchaseViewModel).deletePurchase(item as InventoryPurchase ) })

            })
        val discAdapter = PaymentAdapter(false,
            TransPaymentClickListener {
                //show update dialog

            },TransPaymentLongListener {
                //show delete confirmation dialog
                DialogUtils.showDeleteDialog(requireContext(),viewModel, it, { vm, item -> (vm as PurchaseViewModel).deleteDiscount(item as PaymentModel ) })
            },
            TransDatePaymentClickListener {
                //showDatePickerDialog(it)
            })
        binding.purchaseRv.adapter=adapter
        binding.recyclerViewPdiscount.adapter=discAdapter

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        binding.purchaseRv.addItemDecoration(dividerItemDecoration)

        autoCompleteSubName.threshold = 1

        val suplierList= listOf<String>("Mitra Jaya","Mbtech","Simnu","Busa Yerry","Lancar","Cahaya Indah","Vision","Owl Crown","Bali Jaya","Toko Utama","Sentral Logam","Toko ada","Trijaya","Maliang")
        val suplierNameAdapter=SimilarWordAdapter(requireContext(), suplierList)
        autoCompleteSuplier.setAdapter(suplierNameAdapter)

        viewModel.allSubProductFromDb.observe(viewLifecycleOwner) { subProductList ->

            if (subProductList != null) {
                setAutoCompleteSubNameAdapter(subProductList,autoCompleteSubName)
            }

        }
        viewModel.isAddItemClick.observe(viewLifecycleOwner){
            if (it==true){
                viewModel.onItemAdded()
            }
        }
        viewModel.transSumDateLongClick.observe(viewLifecycleOwner){
            if (it==true){
                showDatePickerDialog(null)
            }
        }
        viewModel.showSaveToInventoryConfirmDialog.observe(viewLifecycleOwner){
            if (it==true){
                DialogUtils.showConfirmationDialog(
                    requireContext(),viewModel,null,"Masukkan transaksi ke stok?",{ vm, item -> (vm as PurchaseViewModel).addInventoryLog() }
                )
            }
        }
        viewModel.isDiscountClicked.observe(viewLifecycleOwner){
            if (it!=null){
                DialogUtils.updateDialog(
                    context = requireContext(),
                    viewModel = viewModel, // Replace with your ViewModel instance
                    model = null,         // Replace with your model instance
                    title = "Diskon",
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
                if (subName.length >= 1) {
                    viewModel.searchProduct(subName)
                }
            }
        })
        autoCompleteSubName.setOnItemClickListener { _, _, position, _ ->

        }

        autoCompleteSuplier.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateExpenseName()
            }
        })
        viewModel.suplierDummy.observe(viewLifecycleOwner) { list ->
                val supliernames = list.map { it.suplierName }
                val adapterr = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    supliernames
                )
               autoCompleteSuplier.setAdapter(adapterr)
        }

        viewModel.inventoryPurchaseList.observe(viewLifecycleOwner){
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.expenseIsKeeped.observe(viewLifecycleOwner, Observer{})
        viewModel.pDiscountList.observe(viewLifecycleOwner){
            discAdapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        //adapter.submitList(purchaseDummy)
        binding.btnAdd.setOnClickListener {
            //viewModel.onAddClick()
            viewModel.insertPurchase()


        }
        viewModel.isNavigateToExpense.observe(viewLifecycleOwner){
            if (it==true){
               this.findNavController().navigate(TransactionPurchaseDirections.actionTransactionPurchaseToExpensesFragment())
                viewModel.onNavigatedToExpense()
            }
        }
        viewModel.isCrudSucsess.observe(viewLifecycleOwner){
            if (it!=null){
                showStatus(binding,it)
            }
        }
        viewModel.isAddInventorySucsess.observe(viewLifecycleOwner){
            if (it!=null){
                showStatus2(binding,it)
               // viewModel.onInventorySuccessed()
            }
        }
        return binding.root
    }

    private fun setAutoCompleteSubNameAdapter(
        subProductList: List<SubWithPriceModel>?,
        autoCompleteSubName: AutoCompleteTextView
    ) {
        if (!subProductList.isNullOrEmpty()) {
            val subNames = subProductList.map { it.subProduct.sub_name.trim()
            }
            val adapterSub = SpaceInsensitiveArrayAdapter(
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
    fun showStatus(binding: FragmentTransactionPurchaseBinding, message: String) {
        binding.statusBanner.animate().cancel()
        binding.statusText.text = message
        binding.statusBanner.visibility = View.VISIBLE
        binding.statusBanner.postDelayed({
            binding.statusBanner.visibility = View.GONE
        }, 500)
    }
    fun showStatus2(binding: FragmentTransactionPurchaseBinding, status: String) {
        binding.status2Banner.animate().cancel()
        val message =if (status== Constants.CRUDSTATUS.SUCSESS) "Data berhasil ditambakan ke Inventori" else "Gagal"
        binding.status2Text.text = message
        binding.status2Banner.visibility = View.VISIBLE
        binding.status2Banner.postDelayed({
            binding.status2Banner.visibility = View.GONE
        }, 2000)
    }


}