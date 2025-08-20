package com.example.app21try6.transaction.transactionselect

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.vendiblelist.VendibleFragmentArgs
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentTransactionSelectBinding
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale


class TransactionSelectFragment : Fragment() {
    private lateinit var binding:FragmentTransactionSelectBinding
    private lateinit var viewModel: TransactionSelectViewModel
    private lateinit var adapter: TransactionSelectAdapter
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
        var code: Constants.Code = Constants.Code.ZERO
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
         adapter = TransactionSelectAdapter(
            PlusSelectListener {
                it.qty = it.qty+1
                viewModel.updateTransDetail(it)

        }, SubsSelectListener {
                val number = it.qty-1
                if(number>=0){
                    it.qty =number
                    viewModel.updateTransDetail(it)
                }else{
                    DialogUtils.showFailedWarning(requireContext(),it.item_name)
                }
        },
            CheckBoxSelectListener{view:View, trans:TransSelectModel ->
                val cb = view as CheckBox
                viewModel.onCheckBoxClicked(trans,cb.isChecked)
            },
            PlusSelectLongListener {
                code = Constants.Code.LONGPLUS
                onLongSubsOrPlusClick(it,code.text,code)
                //viewModel.onShowDialog(it)
                clearSearchQuery()
            },
            SubsSelectLongListener {
                code = Constants.Code.LONGSUBS
                onLongSubsOrPlusClick(it,code.text,code)
                //viewModel.onShowDialog(it)
                clearSearchQuery()
            },
            SelectLongListener {
                it.trans_detail_id = 0L
                //showDialog(it,viewModel,Code.DUPLICATE)
                code = Constants.Code.DUPLICATE
                onLongSubsOrPlusClick(it,it.item_name,code)
               // viewModel.insertDuplicateSubProduct(it)
            }
        )

        binding.transselectRv.adapter = adapter
        (binding.transselectRv.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        viewModel.trans_select_model?.observe(viewLifecycleOwner, Observer {it?.let {

        }
        })
        binding.searchBarSub.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    val input = query.lowercase(Locale.getDefault()).trim().replace(" ","")
                    viewModel.trans_select_modelNew.observe(viewLifecycleOwner, Observer { list ->
                        list?.let { items ->
                            val filteredList = items.filter { item ->
                                val name = item.item_name.lowercase(Locale.getDefault()).trim().replace(" ","")
                                name.contains(input, ignoreCase = true)
                            }
                            adapter.submitList(filteredList)
                        }
                    })
                }
                return true
            }
        })
        viewModel.productId.observe(viewLifecycleOwner){it?.let {

             }
        }
        viewModel.trans_select_modelNew.observe(viewLifecycleOwner){list-> if(list!=null){
            adapter.submitList(list)
            adapter.notifyDataSetChanged()

            }
        }
        viewModel.showDialog.observe(viewLifecycleOwner, Observer {

        })
        return binding.root
    }


    fun onLongSubsOrPlusClick(model:TransSelectModel,title:String,code: Constants.Code) {
        DialogUtils.updateDialogQty<TransactionSelectViewModel, TransSelectModel>(
            context = requireContext(),
            viewModel = viewModel,
            item=model,
            title = title,
            setBrandName = { it, number ->
                val model = it as TransSelectModel
                if (code == Constants.Code.LONGPLUS) {
                    model.qty=  model.qty + number.toDouble()
                 } else if (code == Constants.Code.LONGSUBS){
                     val newQty=model.qty-number
                    if (newQty>=0){
                        model.qty= model.qty - number.toDouble()
                    }else{
                        DialogUtils.showFailedWarning(requireContext(),model.item_name)
                    }
                }else
                     model.qty  = number.toDouble()
            },
            onUpdate = { vm, item ->
                (vm as TransactionSelectViewModel).updateTransDetail(item as TransSelectModel)
                       },
        )
    }
    fun clearSearchQuery() {
        binding.searchBarSub.clearFocus()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        adapter.submitList(emptyList()) // Clear existing data
    }

    /*
        private fun showDialog(transSelectModel: TransSelectModel, viewModel: TransactionSelectViewModel, code: Code) {
            val builder = AlertDialog.Builder(context)
            if (code==Code.ZERO) builder.setTitle(transSelectModel.item_name) else builder.setTitle(code.text)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.pop_up_update, null)
            val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
            textKet.requestFocus()
            when (code) {
                Code.TEXTITEM -> {
                    textKet.setText(transSelectModel.item_name)
                }
                else->{
                    textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    textKet.keyListener = DigitsKeyListener.getInstance("-0123456789.")
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
                    Code.DUPLICATE -> {
                        transSelectModel.qty  = v.toDouble()
                        viewModel.insertDuplicateSubProduct(transSelectModel)
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

     */

}