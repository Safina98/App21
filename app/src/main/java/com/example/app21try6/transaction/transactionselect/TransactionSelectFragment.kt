package com.example.app21try6.transaction.transactionselect

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentTransactionSelectBinding
import com.example.app21try6.transaction.transactionedit.Code
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
        val stockRepo= StockRepositories(application)
        val transRepo= TransactionsRepository(application)
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }

        var datee  = date!!.toMutableList()

        viewModel = ViewModelProvider(requireActivity(), TransactionSelectViewModelFactory(stockRepo,transRepo,date[0].toInt()!!,date,application))
            .get(TransactionSelectViewModel::class.java)
        var i = date!![1].toInt()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.resetTransModel()
            findNavController().popBackStack()
        }
        //viewModel.setProductId(i)
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
                clearSearchQuery()
            },
            SubsSelectLongListener {
                code = Code.LONGSUBS
                viewModel.onShowDialog(it)
                clearSearchQuery()
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
           // viewModel.getTransModel(it)
        }
        }
        viewModel.showDialog.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                showDialog(it,viewModel,code)
            }
            else{
               // adapter.notifyDataSetChanged()
            }
        })
        return binding.root
    }


    private fun showDialog(transSelectModel: TransSelectModel, viewModel: TransactionSelectViewModel, code: Code) {
        val builder = AlertDialog.Builder(context)
        if (code==Code.ZERO) builder.setTitle(transSelectModel.item_name) else builder.setTitle(code.text)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        textKet.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        when (code) {
            Code.TEXTITEM -> {
                textKet.setText(transSelectModel.item_name)
            }
            Code.TEXTPRICE -> {
                textKet.setText(transSelectModel.item_price.toString())
                textKet.inputType = InputType.TYPE_CLASS_NUMBER
            }
            else->{
//                textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
        }
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val v = textKet.text.toString().uppercase().trim()
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

                else -> {}
            }

            viewModel.onCloseDialog()
        }
        builder.setNegativeButton("No") { dialog, which ->
            viewModel.onCloseDialog()
        }
        builder.setOnCancelListener {
            viewModel.onCloseDialog()
        }
        val alert = builder.create()
        alert.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))

    }
    fun clearSearchQuery() {
       // binding.searchBarSub.setQuery("", false)
        binding.searchBarSub.clearFocus()
    }



}