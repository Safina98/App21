package com.example.app21try6.statement

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentStatementHsBinding
import com.example.app21try6.databinding.PopUpDiscBinding
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.example.app21try6.utils.DialogUtils


class StatementHsFragment : Fragment() {
    private lateinit var binding: FragmentStatementHsBinding
    private lateinit var viewModel: StatementHSViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_statement_hs,container,false)
        val application= requireNotNull(this.activity).application
        val dataSource1 = VendibleDatabase.getInstance(application).discountDao
        val dataSource2 = VendibleDatabase.getInstance(application).customerDao
        val dataSource3 = VendibleDatabase.getInstance(application).expenseDao
        val dataSource4 = VendibleDatabase.getInstance(application).expenseCategoryDao
        val dataSource5 = VendibleDatabase.getInstance(application).transDetailDao
        val dataSource6 = VendibleDatabase.getInstance(application).transSumDao
        val viewModelFactory = StatementHSViewModelFactory(application,dataSource1,dataSource2,dataSource3,dataSource4,dataSource5,dataSource6)
        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel
        val adapter = DiscountAdapter(
            DiscountListener {
                showDiscountDialog(it)
            }, DiscountLongListener {
            },
            DiscountDelListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as StatementHSViewModel).deleteDiscountTable(it.id!!) })
            })

        val adapterCustomer = CustomerAdapter(
            CustomerListener {
                showCustomerDialog(it)
            },
            CustomerLongListener {

            },
            CustomerDelListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as StatementHSViewModel).deleteCustomerTable(item as CustomerTable) })
            }
        )
        binding.btnAddDiscount.setOnClickListener {
            showDiscountDialog(null)
        }
        binding.btnAddCustomer.setOnClickListener {
            showCustomerDialog(null)
        }
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvDisc.adapter = adapter
        binding.rvCust.adapter=adapterCustomer
        binding.rvDisc.addItemDecoration(dividerItemDecoration)
        binding.rvCust.addItemDecoration(dividerItemDecoration)
        viewModel.allDiscountFromDB.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.allCustomerFromDb.observe(viewLifecycleOwner, Observer {
            adapterCustomer.submitList(it.sortedBy { it.customerBussinessName })
            adapter.notifyDataSetChanged()
        })
        binding.searchCustomer?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    viewModel.allCustomerFromDb.observe(viewLifecycleOwner, Observer { list ->
                        list?.let { items ->
                            val filteredList = items.filter { item ->
                                item.customerBussinessName.contains(query, ignoreCase = true)
                            }
                            adapterCustomer.submitList(filteredList)
                        }
                    })
                }
                return true
            }
        })

        return binding.root
    }
    private fun showCustomerDialog(customerTable: CustomerTable?) {
        // Inflate the layout using data binding
        val spinnerItems = resources.getStringArray(R.array.disc_Tipe)
        val binding: PopUpUpdateProductDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.pop_up_update_product_dialog, // Replace with your dialog layout file
            null,
            false
        )
        val tvName= binding.textUpdateKet
        val tvLocation= binding.textUpdatePrice
        val tvTag1=binding.textCapital
        binding.ilKet.hint="Nama"
        binding.ilPrice.hint="Lokasi"
        binding.ilCapital.hint="Alamat"
        binding.textDiscount.visibility=View.GONE
        binding.textCapital2.visibility=View.GONE
        binding.defaultNet.visibility=View.GONE
        binding.purchaseUnit.visibility=View.GONE
        // Create the dialog using AlertDialog.Builder
        if (customerTable!=null){
            tvName.setText( customerTable.customerBussinessName)
            tvLocation.setText( customerTable.customerLocation)
            tvTag1.setText(customerTable.customerAddress)
        }
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Tambah Customer")
            .setPositiveButton("OK") { dialog, _ ->
                // Get values from the input fields
                val bussinessNmae= tvName.text.toString().uppercase().trim()
                val location = tvLocation.text.toString().uppercase().trim()
                val address = tvLocation.text.toString().uppercase().trim()
                if (customerTable==null){
                    viewModel.insertCustomer(null,bussinessNmae,location,address,null,null)
                }else{
                    viewModel.updateCustomer(customerTable.custId,null,bussinessNmae,location,address,null,null)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }
    private fun showDiscountDialog(discountTable: DiscountAdapterModel?) {
        // Inflate the layout using data binding
        val spinnerItems = resources.getStringArray(R.array.disc_Tipe)
        val binding: PopUpDiscBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
            R.layout.pop_up_disc, // Replace with your dialog layout file
            null,
            false
        )
        binding.textDiscValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textDiscQty.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        // Create the dialog using AlertDialog.Builder
        if (discountTable!=null){
            binding.textDiscName.setText( discountTable.discountName)
            binding.textDiscValue.setText( discountTable.discountValue.toString())
            binding.textDiscQty.setText( discountTable.minimumQty.toString())
            binding.textCustLoc.setText( discountTable.custLocation ?: "")
            val position = spinnerItems.indexOf(discountTable.discountType)
            binding.spinnerM.setSelection(position)
        }
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Enter Discount Details")
            .setPositiveButton("OK") { dialog, _ ->
                // Get values from the input fields
                val discName = binding.textDiscName.text.toString().uppercase().trim()
                val discValue = binding.textDiscValue.text.toString().trim().toDouble()
                val discMinQty = binding.textDiscQty.text.toString().trim().toDoubleOrNull()
                val selectedDiscType = binding.spinnerM.selectedItem.toString().trim()
                val custLocation = binding.textCustLoc.text.toString().uppercase().trim()
                if (discountTable==null){
                    viewModel.insertDiscount(discValue,discName,discMinQty,selectedDiscType,custLocation)
                }else{
                    viewModel.updateDiscount(discountTable.id!!,discValue,discName,discMinQty,selectedDiscType,custLocation)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        // Show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }
}