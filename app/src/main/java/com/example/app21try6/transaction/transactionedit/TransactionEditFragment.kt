package com.example.app21try6.transaction.transactionedit

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionEditBinding
import com.google.android.material.textfield.TextInputEditText


enum class Code(val text: String) {ZERO(""),LONGPLUS("Tambah"),LONGSUBS("Kurang"),TEXTITEM("Update Nama Barang"),TEXTPRICE("Update Harga barang")}
class TransactionEditFragment : Fragment() {
    private lateinit var binding:FragmentTransactionEditBinding
    val viewModel:TransactionEditViewModel by viewModels()
   // val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_edit, container, false)


        val application = requireNotNull(this.activity).application

        val datasource1 = VendibleDatabase.getInstance(application).transSumDao
        val datasource2 = VendibleDatabase.getInstance(application).transDetailDao

        val id= arguments?.let{TransactionEditFragmentArgs.fromBundle(it).id}
       Log.e("SUMVM","transum in TransEditFragmnet id is "+id.toString()+"")

        val viewModelFactory = TransactionEditViewModelFactory(application, datasource1, datasource2, id!!)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TransactionEditViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        var code:Code = Code.ZERO


       val adapter = TransactionEditAdapter(TransEditClickListener {
           code = Code.TEXTITEM
           viewModel.onShowDialog(it)
       }, SubsTransClickListener {
           viewModel.updateTransDetail(it, -1.0)
       }, PlusTransClickListener {
           viewModel.updateTransDetail(it, 1.0)
       }, SubsTransLongListener {
           code = Code.LONGSUBS
           viewModel.onShowDialog(it)
       }, PlusTransLongListener {
           code = Code.LONGPLUS
           viewModel.onShowDialog(it)
       }, TransEditDeleteLongListener {
           //Toast.makeText(context,it.trans_item_name+" deleted",Toast.LENGTH_SHORT).show()
           //viewModel.delete(it.trans_detail_id)
           deleteDialog(it, viewModel)
       }, TransEditPriceLongListener {
           showDialog(it,viewModel,Code.TEXTPRICE)
           Toast.makeText(context,it.trans_item_name+" price clicked",Toast.LENGTH_SHORT).show()
       })
       //TODO create custom adapter that shows or hide checkbox for delete purpose
       binding.recyclerViewEditTrans.adapter = adapter

       viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionDetailFragment(it))
               viewModel.onNavigatedtoDetail()
           }
       })

       viewModel.itemTransDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               adapter.submitList(it.sortedBy { it.trans_item_name })
               adapter.notifyDataSetChanged()
           }
       })

       viewModel.navigateToVendible.observe(viewLifecycleOwner, Observer {
           if (it != null) {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionProductFragment(it))
               viewModel.setCustomerName()
               viewModel.onNavigatedtoVendible()

           }
       })
       viewModel.showDialog.observe(viewLifecycleOwner, Observer {
           if (it != null) {
               showDialog(it, viewModel, code)
           } else {
               // Hide the keyboard.
           //TODO close keyboard when dialog closed
           }
       })
       viewModel.totalSum.observe(viewLifecycleOwner, Observer {
           it?.let {
               viewModel.updateTotalSum()
           }
       })
       viewModel.custName.observe(viewLifecycleOwner, Observer {
           it?.let{
               //viewModel.saveCustomerName()
               viewModel.setCustomerName()
               Log.e("SUMVM","transum in viewModel navigate fun is "+it+"")
           }
       })
       // Inflate the layout for this fragment
       return binding.root
    }


    private fun showDialog(transactionDetail: TransactionDetail, viewModel: TransactionEditViewModel, code: Code) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(code.text)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        when (code) {
            Code.TEXTITEM -> {
                textKet.setText(transactionDetail.trans_item_name)
            }
            Code.TEXTPRICE -> {
                textKet.setText(transactionDetail.trans_price.toString())
                textKet.inputType = InputType.TYPE_CLASS_NUMBER
            }
           else->{
               textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
           }
        }
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            var v = textKet.text.toString().toUpperCase().trim()
            when (code) {
                Code.LONGSUBS -> {
                    viewModel.updateTransDetail(transactionDetail, (v.toDouble() * -1))
                }
                Code.LONGPLUS -> {
                    viewModel.updateTransDetail(transactionDetail, v.toDouble())
                }
                Code.TEXTITEM -> {
                    viewModel.updateTransDetailItemName(transactionDetail, v)
                }
                Code.TEXTPRICE -> {
                    viewModel.updateTransDetailItemPrice(transactionDetail, v.toInt())
                }
            }
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            viewModel.onCloseDialog()
        }
        builder.setNegativeButton("No") { dialog, which ->
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            viewModel.onCloseDialog()
        }
        builder.setOnCancelListener {
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            viewModel.onCloseDialog()
        }

        val alert = builder.create()
        alert.show()

    }
    private fun deleteDialog(transactionDetail: TransactionDetail, viewModel: TransactionEditViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                viewModel.delete(transactionDetail.trans_detail_id)
                Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onPause(){
        Toast.makeText(context,"onDestroy View Called",Toast.LENGTH_SHORT).show()
        super.onPause()
    }
}