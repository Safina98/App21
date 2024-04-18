package com.example.app21try6.transaction.transactionselect

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionSelectBinding
import com.example.app21try6.transaction.transactionedit.Code
import com.example.app21try6.transaction.transactionedit.TransactionEditViewModel
import com.google.android.material.textfield.TextInputEditText


class TransactionSelectFragment : Fragment() {
    private lateinit var binding:FragmentTransactionSelectBinding
    private val viewModel :TransactionSelectViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_select,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource = SummaryDatabase.getInstance(application).summaryDbDao
        val dataSource1 = VendibleDatabase.getInstance(application).categoryDao
        val dataSource2 = VendibleDatabase.getInstance(application).productDao
        val dataSource3 = VendibleDatabase.getInstance(application).brandDao
        val dataSource4 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource5 = VendibleDatabase.getInstance(application).transSumDao
        val dataSource6 = VendibleDatabase.getInstance(application).transDetailDao
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }
        var datee  = date!!.toMutableList()
        val viewModelFactory = TransactionSelectViewModelFactory(date[0].toInt()!!,dataSource1,dataSource2,dataSource3,dataSource4,date,dataSource5,dataSource6,application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TransactionSelectViewModel::class.java)

        var code: Code = Code.ZERO
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = TransactionSelectAdapter(
            PlusSelectListener {
                               it.qty = it.qty+1
               // Toast.makeText(context, it.qty.toString(), Toast.LENGTH_SHORT).show()
                viewModel.updateTransDetail(it)

        }, SubsSelectListener {
                it.qty = it.qty-1
                //Toast.makeText(context, it.qty.toString(), Toast.LENGTH_SHORT).show()
                viewModel.updateTransDetail(it)
        },
            CheckBoxSelectListener{view:View, trans:TransSelectModel ->
                val cb = view as CheckBox
               // trans.is_selected  = cb.isChecked
                viewModel.onCheckBoxClicked(trans,cb.isChecked)
            },
            PlusSelectLongListener {
               // Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
                code = Code.LONGPLUS
                viewModel.onShowDialog(it)
            },
            SubsSelectLongListener {
                //Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
                code = Code.LONGSUBS
                viewModel.onShowDialog(it)
            }
        )
        binding.transselectRv.adapter = adapter
        viewModel.trans_select_model.observe(viewLifecycleOwner, Observer {it?.let {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        })
        viewModel.showDialog.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                showDialog(it,viewModel,code)
            }
            else{
                adapter.notifyDataSetChanged()
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unCheckedAllSubs()
    }

    private fun showDialog(transSelectModel: TransSelectModel, viewModel: TransactionSelectViewModel, code: Code) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(code.text)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        when (code) {
            Code.TEXTITEM -> {
                textKet.setText(transSelectModel.item_name)
            }
            Code.TEXTPRICE -> {
                textKet.setText(transSelectModel.item_price.toString())
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
                    //viewModel.updateTransDetail(transactionDetail, (v.toDouble() * -1))
                    transSelectModel.qty = transSelectModel.qty -v.toDouble()
                   // viewModel.updateTransDetail(transSelectModel,v.toDouble()*-1)
                    viewModel.updateTransDetail(transSelectModel)

                }
                Code.LONGPLUS -> {
                    //viewModel.updateTransDetail(transactionDetail, v.toDouble())
                    //viewModel.updateTransDetail(transSelectModel,v.toDouble())
                    transSelectModel.qty = transSelectModel.qty +v.toDouble()
                    // viewModel.updateTransDetail(transSelectModel,v.toDouble()*-
                    viewModel.updateTransDetail(transSelectModel)

                }
                Code.TEXTITEM -> {
                    //viewModel.updateTransDetailItemName(transactionDetail, v)
                }
                Code.TEXTPRICE -> {
                    //viewModel.updateTransDetailItemPrice(transactionDetail, v.toInt())
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

}