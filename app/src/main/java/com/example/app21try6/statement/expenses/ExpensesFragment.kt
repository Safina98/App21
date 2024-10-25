package com.example.app21try6.statement.expenses

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.ExpenseCategory
import com.example.app21try6.database.Expenses
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentExpensesBinding
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.statement.DiscountAdapter
import com.example.app21try6.statement.DiscountDelListener
import com.example.app21try6.statement.DiscountListener
import com.example.app21try6.statement.DiscountLongListener
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.util.Date


val tagg = "expenseprobs"

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding
    private lateinit var viewModel: StatementHSViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_expenses,container,false)
        val application= requireNotNull(this.activity).application
        val dataSource1 = VendibleDatabase.getInstance(application).discountDao
        val dataSource2 = VendibleDatabase.getInstance(application).customerDao
        val dataSource3 = VendibleDatabase.getInstance(application).expenseDao
        val dataSource4 = VendibleDatabase.getInstance(application).expenseCategoryDao
        val viewModelFactory = StatementHSViewModelFactory(application,dataSource1,dataSource2,dataSource3,dataSource4)
        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel
        val adapter = DiscountAdapter(
            DiscountListener {
                ///showDiscountDialog(it)
            }, DiscountLongListener {
            },
            DiscountDelListener {
               /// viewModel.deleteDiscountTable(it.id!!)
            })
        binding.rvDisc.adapter=adapter

        binding.btnAddNewExpense.setOnClickListener {
            //showsAddExpenseCategoryDialog(null)
            showExpensesDialog(null)
        }
        viewModel.allExpenseCategory.observe(viewLifecycleOwner, Observer {
           Log.i(tagg,"category $it")
        })
        viewModel.allExpensesFromDB.observe(viewLifecycleOwner, Observer {
            Log.i(tagg,"expense $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })


        return binding.root
    }
    fun showExpensesDialog(expenses:Expenses?){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        Log.i("DiscProbs", "updateDialogShows")


        // Inflate the custom view for the dialog
        val dialogBinding = DataBindingUtil.inflate<PopUpUpdateProductDialogBinding>(
            LayoutInflater.from(context), R.layout.pop_up_update_product_dialog, null, false
        )

        // Initialize views from the binding
        val textExpenseName = dialogBinding.textUpdateKet
        val textExpenseAmmount = dialogBinding.textUpdatePrice
        val textExpensesDate = dialogBinding.textCapital
        val textExpensesCategory = dialogBinding.textDiscount
        dialogBinding.textCapital2.visibility =View.GONE
        dialogBinding.defaultNet.visibility =View.GONE
        textExpenseName.hint= "Nama Pengeluaran"
        textExpenseAmmount.hint="Jumlah"
        textExpensesDate.hint="Tanggal"
        textExpensesCategory.hint="Kategori"

        // Set up the AutoCompleteTextView with a mutable adapter
        val merkAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        textExpensesCategory.setAdapter(merkAdapter)

        // Observe the ViewModel LiveData and update the adapter
        viewModel.allExpenseCategory.observe(viewLifecycleOwner) { allMerk ->
            allMerk?.let {

                merkAdapter.clear() // Clear the adapter's data
                merkAdapter.addAll(allMerk.sortedBy { it }) // Add the sorted data to the adapter
                merkAdapter.notifyDataSetChanged() // Notify the adapter about the data change
            }
        }

        if (expenses!=null){
            textExpenseName.setText(expenses.expense_name.toString())
            textExpenseAmmount.setText(expenses.expense_ammount.toString())
            textExpensesDate.setText(expenses.expense_date.toString())
            val expenseCategoryName=viewModel.expenseCategoryName.value
            if (expenseCategoryName!=null) textExpensesCategory.setText(expenseCategoryName)
            textExpenseName.requestFocus()
        }
        // Set the data for the dialog fields

        // Show the soft keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // Use dialogBinding.root instead of view
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Update") { dialog, which ->
            val expenseAmmount = textExpenseAmmount.text.toString().toIntOrNull()
            val expensesDate = Date()//textExpensesDate.text.toString()
            val expenseName =textExpenseName.text.toString().uppercase().trim()
            expenses?.expense_name = expenseName
            expenses?.expense_ammount=expenseAmmount
            expenses?.expense_date =Date()
            val expenseCatName = textExpensesCategory.text.toString().uppercase().trim()

            if (expenses==null) {
                viewModel.insertExpense(expenseName,expenseAmmount,expensesDate,expenseCatName)
            }
            else{viewModel.updateExpenses(expenses)}
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    fun showsAddExpenseCategoryDialog(expenseCategory: ExpenseCategory?){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Kategori")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textCatName = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textCatName.hint = "Nama Kategori"
        if (expenseCategory!=null){
            textCatName.setText(expenseCategory.expense_category_name.toString())
        }

        textCatName.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        builder.setView(view)
        builder.setPositiveButton("Ok") { dialog, which ->
            val catName=textCatName.text.toString().uppercase().trim()
            expenseCategory?.expense_category_name =catName
            if (expenseCategory==null) viewModel.insertExpenseCategory(catName)
            else viewModel.updateExpenseCategory(expenseCategory)
            }

        builder.setNegativeButton("No") { dialog, which ->

        }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
    }
}