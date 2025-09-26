package com.example.app21try6.statement.expenses

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.databinding.FragmentExpensesBinding
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.formatRupiah
import com.example.app21try6.statement.DiscountAdapter
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.statement.DiscountDelListener
import com.example.app21try6.statement.DiscountListener
import com.example.app21try6.statement.DiscountLongListener
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory
import com.example.app21try6.statement.purchase.PurchaseViewModel
import com.example.app21try6.statement.purchase.PurchaseViewModelFactory
import com.example.app21try6.statement.purchase.tagp
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.stock.brandstock.CategoryAdapter
import com.example.app21try6.stock.brandstock.CategoryModel

import com.example.app21try6.stock.brandstock.DeleteListener
import com.example.app21try6.stock.brandstock.UpdateListener
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.exp


val tagg = "expenseprobs"

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding
    private lateinit var viewModel: PurchaseViewModel
    private lateinit var adapter:DiscountAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_expenses,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner = viewLifecycleOwner

        val layoutOneViews = listOf(
            binding.spinnerC,

            binding.rvDisc,
            binding.lblTotal,
            binding.txtTotal
        )
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

        adapter = DiscountAdapter(
            DiscountListener {
                if(it.expense_category_name=="BELI BARANG"){
                    viewModel.onNavigateToPurcase(it.id!!)
                }
                else{
                    showExpensesDialog(it)
                }
            }, DiscountLongListener {
                showExpensesDialog(it)
            },

            DiscountDelListener {item->
                val id=item.id
                DialogUtils.showDeleteDialog(
                    requireContext(),
                    this,
                    viewModel,
                    item, { vm, item ->
                            (vm as  PurchaseViewModel).deleteExpense(item as DiscountAdapterModel)

                    })

            })
        val categoryAdapter = CategoryAdapter(
        UpdateListener {
            showsAddExpenseCategoryDialog(it)
        }, DeleteListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as PurchaseViewModel).deleteExpenseCategory(item as CategoryModel) })
            }
        )
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvCat.addItemDecoration(dividerItemDecoration)
        binding.rvDisc.adapter=adapter
        binding.rvCat.adapter=categoryAdapter

        val adapterYear = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.tahun))
        binding.spinnerYear.adapter = adapterYear

        val year = Calendar.getInstance().get(Calendar.YEAR).toString()

        val position = (binding.spinnerYear.adapter as ArrayAdapter<String>).getPosition(year)
        binding.spinnerYear.setSelection(position)


        val adapterMonth = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.bulan))
        binding.spinnerMonth.adapter = adapterMonth
        val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))

        val positionM = (binding.spinnerMonth.adapter as ArrayAdapter<String>).getPosition(currentMonth)
        binding.spinnerMonth.setSelection(positionM)
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedYearValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedMonthValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.spinnerC.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedECValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        binding.btnEditEcNew.setOnClickListener {
            if (layoutOneViews[0].visibility == View.VISIBLE) {
                layoutOneViews.forEach { it.visibility = View.GONE }
                binding.cardView.visibility = View.VISIBLE
            } else {
                layoutOneViews.forEach { it.visibility = View.VISIBLE }
               binding.cardView.visibility = View.GONE
            }
        }
        binding.btnAddNewExpense.setOnClickListener {
            if (layoutOneViews[0].visibility == View.VISIBLE) {
                showExpensesDialog(null)
            } else {
                showsAddExpenseCategoryDialog(null)
            }
        }

        binding.searchAllExpense.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterProduct(newText)
                return true
            }
        })

        viewModel.allExpenseCategory.observe(viewLifecycleOwner, Observer {
            categoryAdapter.submitList(it)
        })

        viewModel.allExpenseCategoryName.observe(viewLifecycleOwner){ entries->
            val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, entries)
            binding.spinnerC.adapter = adapter1
        }

        viewModel.selectedECSpinner.observe(viewLifecycleOwner) {
            viewModel.updateRv4()
        }
        viewModel.selectedYearSpinner.observe(viewLifecycleOwner){
            Log.i(tagg,"selectedYearObserver: $it")
        }
        viewModel.selectedMonthSpinner.observe(viewLifecycleOwner){
            Log.i(tagg,"selectedMonthObserver: $it")
        }

        viewModel.allExpenseCategoryName.observe(viewLifecycleOwner, Observer {
           //Log.i(tagg,"category $it")
        })
        viewModel.allExpensesFromDB.observe(viewLifecycleOwner, Observer {
          //  Log.i(tagg,"expense $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.expenseSum.observe(viewLifecycleOwner, Observer {
           // Log.i(tagg, "expensedum:$it")
        })
        viewModel.isNavigateToPurchase.observe(viewLifecycleOwner,Observer{
            it?.let {
                this.findNavController().navigate(ExpensesFragmentDirections.actionExpensesFragmentToTransactionPurchase(it))
                viewModel.onNavigatedToPurchase()
            }
        })




        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showExpensesDialog(expenses: DiscountAdapterModel?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")


        // Inflate the custom view for the dialog
        val dialogBinding = DataBindingUtil.inflate<PopUpUpdateProductDialogBinding>(
            LayoutInflater.from(context), R.layout.pop_up_update_product_dialog, null, false
        )
        val parent = dialogBinding.root.parent as? ViewGroup
        parent?.removeView(dialogBinding.root)
        val textExpenseName = dialogBinding.textUpdateKet
        val textExpenseAmmount = dialogBinding.textUpdatePrice
        val textExpensesDate = dialogBinding.textCapital
        val textExpensesCategory = dialogBinding.textDiscount
        dialogBinding.lblId.visibility = View.GONE
        dialogBinding.textCapital2.visibility = View.GONE
        dialogBinding.defaultNet.visibility = View.GONE
        dialogBinding.ilCapital2.visibility = View.GONE
        dialogBinding.ilDefaultNet.visibility = View.GONE
        dialogBinding.ilPurchaseUnit.visibility=View.GONE
        dialogBinding.ilPurchasePrice.visibility=View.GONE
        dialogBinding.ilAlternatePrice.visibility=View.GONE
        dialogBinding.ilKet.hint = "Nama Pengeluaran"
        dialogBinding.ilPrice.hint = "Jumlah"
        dialogBinding.ilCapital.hint = "Tanggal"
        dialogBinding.ilDisc.hint = "Kategori"

        // Set up the AutoCompleteTextView with a mutable adapter
        val merkAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        textExpensesCategory.setAdapter(merkAdapter)

        // Observe the ViewModel LiveData and update the adapter
        viewModel.allExpenseCategoryName.observe(viewLifecycleOwner) { allMerk ->
            allMerk?.let {
                merkAdapter.clear()
                merkAdapter.addAll(allMerk.sortedBy { it })
                merkAdapter.notifyDataSetChanged()
            }
        }

        // Load data if available
        if (expenses != null) {
            textExpenseName.setText(expenses.expense_name.toString())
            textExpenseAmmount.setText(expenses.expense_ammount.toString())
            textExpensesDate.setText(Constants.DETAILED_DATE_FORMATTER.format(expenses.date))
            textExpensesCategory.setText(expenses.expense_category_name)
            textExpenseName.requestFocus()
        }else textExpensesDate.setText(Constants.DETAILED_DATE_FORMATTER.format(Date()))

        // Set OnClickListener for the date field to open DatePickerDialog
        textExpensesDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                // Set the selected date
                calendar.set(year, month, dayOfMonth)

                // Get current hour and minute
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

                // Set current hour and minute to the selected date
                calendar.set(Calendar.HOUR_OF_DAY, currentHour)
                calendar.set(Calendar.MINUTE, currentMinute)

                // Format the date and time and set it in textExpensesDate
                textExpensesDate.setText(Constants.DETAILED_DATE_FORMATTER.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Set up the dialog's buttons and handling
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Update") { dialog, which ->
            val expenseAmmount = textExpenseAmmount.text.toString().toIntOrNull()
            val expensesDate = textExpensesDate.text.toString()
            val expenseName = textExpenseName.text.toString().uppercase().trim()
            expenses?.expense_name = expenseName
            expenses?.expense_ammount = expenseAmmount
            expenses?.date = SimpleDateFormat(Constants.DETAILED_DATE_FORMAT, Locale.getDefault()).parse(expensesDate) ?: Date()
            val expenseCatName = textExpensesCategory.text.toString().uppercase().trim()
            if (expenses == null) {
                viewModel.insertExpense(expenseName, expenseAmmount, SimpleDateFormat(
                    Constants.DETAILED_DATE_FORMAT, Locale.getDefault()).parse(expensesDate)?: Date(), expenseCatName)
            } else {
                viewModel.updateExpenses(expenses)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun showsAddExpenseCategoryDialog(expenseCategory: CategoryModel?){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Kategori")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textCatName = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textCatName.hint = "Nama Kategori"
        if (expenseCategory!=null){
            textCatName.setText(expenseCategory.categoryName.toString())
        }

        textCatName.requestFocus()
        builder.setView(view)
        builder.setPositiveButton("Ok") { dialog, which ->
            val catName=textCatName.text.toString().uppercase().trim()
            expenseCategory?.categoryName =catName
            if (expenseCategory==null) viewModel.insertExpenseCategory(catName)
            else viewModel.updateExpenseCategory(expenseCategory)
            }
        builder.setNegativeButton("No") { dialog, which ->
        }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        viewModel.getAllexpenseCategory()
    }
}