package com.example.app21try6.statement.expenses

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.R
import com.example.app21try6.DETAILED_DATE_FORMAT
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentExpensesBinding
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.statement.DiscountAdapter
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.statement.DiscountDelListener
import com.example.app21try6.statement.DiscountListener
import com.example.app21try6.statement.DiscountLongListener
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory
import com.example.app21try6.statement.purchase.tagp
import com.example.app21try6.stock.brandstock.CategoryAdapter
import com.example.app21try6.stock.brandstock.CategoryModel

import com.example.app21try6.stock.brandstock.DeleteListener
import com.example.app21try6.stock.brandstock.UpdateListener
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


val tagg = "expenseprobs"

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding
    private lateinit var viewModel: StatementHSViewModel
    private lateinit var adapter:DiscountAdapter
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
        val dataSource1 = VendibleDatabase.getInstance(application).discountDao
        val dataSource2 = VendibleDatabase.getInstance(application).customerDao
        val dataSource3 = VendibleDatabase.getInstance(application).expenseDao
        val dataSource4 = VendibleDatabase.getInstance(application).expenseCategoryDao
        val dataSource5 = VendibleDatabase.getInstance(application).transDetailDao
        val dataSource6 = VendibleDatabase.getInstance(application).transSumDao

        val viewModelFactory = StatementHSViewModelFactory(application,dataSource1,dataSource2,dataSource3,dataSource4,dataSource5,dataSource6)
        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel
        adapter = DiscountAdapter(
            DiscountListener {

                             viewModel.onNavigateToPurcase(it.id!!)

            }, DiscountLongListener {
                showExpensesDialog(it)
            },
            DiscountDelListener {
                viewModel.deleteExpense(it.id!!)
            })
        val categoryAdapter = CategoryAdapter(
        UpdateListener {
                       showsAddExpenseCategoryDialog(it)
        },
            DeleteListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as StatementHSViewModel).deleteExpenseCategory(item as CategoryModel) })
            }
        )
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvCat.addItemDecoration(dividerItemDecoration)
        binding.rvDisc.adapter=adapter
        binding.rvCat.adapter=categoryAdapter
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
        viewModel.allExpenseCategory.observe(viewLifecycleOwner, Observer {
            categoryAdapter.submitList(it)
        })
        viewModel.allExpenseCategoryName.observe(viewLifecycleOwner){ entries->
            val adapter1 = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, entries)
            binding.spinnerC.adapter = adapter1
        }

        viewModel.selectedECSpinner.observe(viewLifecycleOwner) {
            viewModel.updateRv()
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
            textExpensesDate.setText(DETAILED_DATE_FORMATTER.format(expenses.date))
            val expenseCategoryName = viewModel.expenseCategoryName.value
            if (expenseCategoryName != null) textExpensesCategory.setText(expenseCategoryName)
            textExpenseName.requestFocus()
        }else textExpensesDate.setText(DETAILED_DATE_FORMATTER.format(Date()))

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
                textExpensesDate.setText(DETAILED_DATE_FORMATTER.format(calendar.time))
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
            expenses?.date = SimpleDateFormat(DETAILED_DATE_FORMAT, Locale.getDefault()).parse(expensesDate) ?: Date()
            val expenseCatName = textExpensesCategory.text.toString().uppercase().trim()
            if (expenses == null) {
                viewModel.insertExpense(expenseName, expenseAmmount, SimpleDateFormat(
                    DETAILED_DATE_FORMAT, Locale.getDefault()).parse(expensesDate)?: Date(), expenseCatName)
            } else {
                viewModel.updateExpenses(expenses)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }


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
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllexpenseCategory()
    }
}