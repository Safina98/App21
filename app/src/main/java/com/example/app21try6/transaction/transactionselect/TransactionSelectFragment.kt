package com.example.app21try6.transaction.transactionselect

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.editdetail.BookkeepingViewModel
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionSelectBinding
import com.example.app21try6.transaction.transactionedit.Code
import com.example.app21try6.transaction.transactionedit.TransactionEditViewModel
import com.google.android.material.textfield.TextInputEditText


class TransactionSelectFragment : Fragment() {
    private lateinit var binding:FragmentTransactionSelectBinding
    private lateinit var viewModel: TransactionSelectViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_select,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource1 = VendibleDatabase.getInstance(application).categoryDao
        val dataSource2 = VendibleDatabase.getInstance(application).productDao
        val dataSource4 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource6 = VendibleDatabase.getInstance(application).transDetailDao
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }

        var datee  = date!!.toMutableList()

        viewModel = ViewModelProvider(requireActivity(), TransactionSelectViewModelFactory(date[0].toInt()!!,dataSource1,dataSource2,dataSource4,date,dataSource6,application))
            .get(TransactionSelectViewModel::class.java)
        var i = date!![1].toInt()
        viewModel.setProductId(i)
        var code: Code = Code.ZERO
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = TransactionSelectAdapter(
            PlusSelectListener {
                it.qty = it.qty+1
                viewModel.updateTransDetail(it)

        }, SubsSelectListener {
                it.qty = it.qty-1
                viewModel.updateTransDetail(it)
        },
            CheckBoxSelectListener{view:View, trans:TransSelectModel ->
                val cb = view as CheckBox
                viewModel.onCheckBoxClicked(trans,cb.isChecked)
            },
            PlusSelectLongListener {
                code = Code.LONGPLUS
                viewModel.onShowDialog(it)
            },
            SubsSelectLongListener {
                code = Code.LONGSUBS
                viewModel.onShowDialog(it)
            },
            SelectLongListener {
                it.trans_detail_id = 0L
                showDialog(it,viewModel,Code.ZERO)
                //viewModel.insertDuplicateSubProduct(it)
            }
        )
        binding.transselectRv.adapter = adapter
        viewModel.trans_select_model.observe(viewLifecycleOwner, Observer {it?.let {
            adapter.submitList(it.sortedBy { it.item_name })
          //  Log.i("DataProb","fragment observe trans $it")
            adapter.notifyDataSetChanged()
        }
        })
        binding.searchBarSub.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    viewModel.trans_select_model.observe(viewLifecycleOwner, Observer { list ->
                        list?.let { items ->
                            val filteredList = items.filter { item ->
                                item.item_name.contains(query, ignoreCase = true)
                            }
                            adapter.submitList(filteredList)
                        }
                    })
                }
                return true
            }
        })
        viewModel.productId.observe(viewLifecycleOwner){it?.let {
            viewModel.getTransModel(it)
        }
        }
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


    private fun showDialog(transSelectModel: TransSelectModel, viewModel: TransactionSelectViewModel, code: Code) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(code.text)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textKet.requestFocus()
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
                    transSelectModel.qty = transSelectModel.qty -v.toDouble()
                    viewModel.updateTransDetail(transSelectModel)

                }
                Code.LONGPLUS -> {
                    transSelectModel.qty = transSelectModel.qty +v.toDouble()
                    viewModel.updateTransDetail(transSelectModel)

                }
                Code.ZERO -> {
                    transSelectModel.qty  = v.toDouble()
                    viewModel.insertDuplicateSubProduct(transSelectModel)
                }
                Code.TEXTPRICE -> {
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