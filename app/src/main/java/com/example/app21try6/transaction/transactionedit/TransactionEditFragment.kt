package com.example.app21try6.transaction.transactionedit


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.Code
import com.example.app21try6.R
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentTransactionEditBinding
import com.example.app21try6.databinding.PopUpUnitBinding
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText



class TransactionEditFragment : Fragment() {
    private lateinit var binding:FragmentTransactionEditBinding
    private lateinit var adapter:TransactionEditAdapter
    val viewModel:TransactionEditViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_edit, container, false)

        val application = requireNotNull(this.activity).application

        val stockRepositories= StockRepositories(application)
        val transRepositories= TransactionsRepository(application)

        val discountRepository= DiscountRepository(application)
        val id= arguments?.let{TransactionEditFragmentArgs.fromBundle(it).id}

        val viewModelFactory = TransactionEditViewModelFactory(stockRepositories,transRepositories,discountRepository,application, id!!)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TransactionEditViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        var code:Code = Code.ZERO
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.calculateDiscA().also {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })

       val adapter = TransactionEditAdapter(TransEditClickListener {
           showDialog(it,viewModel,Code.TEXTITEM)
       }, SubsTransClickListener {item->
           var qty=item.qty-1
           if (qty>=0){
               viewModel.updateTransDetail(item, -1.0)
           }else{
               DialogUtils.showFailedWarning(requireContext(),item.trans_item_name)
           }

       }, PlusTransClickListener {
           viewModel.updateTransDetail(it, 1.0)
       }, SubsTransLongListener {
           code = Code.LONGSUBS
           showDialog(it,viewModel,Code.LONGSUBS)
       }, PlusTransLongListener {
           code = Code.LONGPLUS
           showDialog(it,viewModel,Code.LONGPLUS)
       }, TransEditDeleteListener {
           DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as TransactionEditViewModel).delete(it.trans_detail_id) })
       }, TransEditPriceClickListener {
           showDialog(it,viewModel,Code.TEXTPRICE)
       }, UnitTransTextCliked{ edit_trans ->
               showInputCoiceDialog(edit_trans)
       }, TransEditUnitQtyClickListener {
               showDialog(it,viewModel,Code.UNITQTY)
       }, viewModel.updatePositionCallback
          )


       // drag recyclerview item
       val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
           override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
               val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
               return makeMovementFlags(dragFlags, 0)
           }
           override fun onMove(
               recyclerView: RecyclerView,
               viewHolder: RecyclerView.ViewHolder,
               target: RecyclerView.ViewHolder
           ): Boolean {
               val fromPosition = viewHolder.adapterPosition
               val toPosition = target.adapterPosition
               adapter.moveItem(fromPosition, toPosition)
               return true
           }

           override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               // No swipe action
               }
           override fun isLongPressDragEnabled(): Boolean {
               return true
           }
           override fun isItemViewSwipeEnabled(): Boolean {
               return false
           }
           override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
               super.clearView(recyclerView, viewHolder)
               adapter.updatePositionCallback()
           }
       }
       binding.recyclerViewEditTrans.adapter = adapter
       val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
       itemTouchHelper.attachToRecyclerView(binding.recyclerViewEditTrans)

        val autoCompleteTextView: AutoCompleteTextView = binding.custNameEdit

        //val adapterr: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        //autoCompleteTextView.setAdapter(adapterr)

       viewModel.itemTransDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               adapter.submitList(it)
               adapter.setItemsValue(it)
           }
       })
        viewModel.transDetailWithProduct.observe(viewLifecycleOwner, Observer {
            it?.let {
            }
        })
        viewModel.allCustomerTable.observe(viewLifecycleOwner, Observer { customerList ->
            val customerNames = customerList.map { it.customerBussinessName }
            val adapterr = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, customerNames)
            autoCompleteTextView.setAdapter(adapterr)
        })

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateCustNameSum()
            }
        })


       viewModel.showDialog.observe(viewLifecycleOwner, Observer {
           if (it != null) {
               showDialog(it, viewModel, code)
           } else {
               // Hide the keyboard.
           }
       })
       viewModel.totalSum.observe(viewLifecycleOwner, Observer {
           it?.let {
               viewModel.updateTotalSum()
           }
       })
       viewModel.custName.observe(viewLifecycleOwner, Observer {
           it?.let{

           }
       })
       viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionDetailFragment(it))
               viewModel.onNavigatedtoDetail()
           }
       })

       viewModel.navigateToVendible.observe(viewLifecycleOwner, Observer {
           if (it != null) {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionProductFragment(it))
               viewModel.onNavigatedtoVendible()

           }
       })

       // Inflate the layout for this fragment
       return binding.root
    }



    private fun showDialog(transactionDetail: TransactionDetail, viewModel: TransactionEditViewModel, code: Code) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(code.text)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(com.example.app21try6.R.layout.pop_up_update, null)
        val textKet = view.findViewById<TextInputEditText>(com.example.app21try6.R.id.textUpdateKet)
        when (code) {
            Code.TEXTITEM -> {
                textKet.setText(transactionDetail.trans_item_name)
            }
            Code.TEXTPRICE -> {
                textKet.setText(transactionDetail.trans_price.toString())
                textKet.inputType = InputType.TYPE_CLASS_NUMBER
            }
           else->{
               if (transactionDetail.unit_qty!=1.0){
                   textKet.setText(transactionDetail.unit_qty.toString())
               }
               textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
               textKet.keyListener = DigitsKeyListener.getInstance("-0123456789.")

           }
        }

        textKet.requestFocus()

        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val v = textKet.text.toString().uppercase().trim()
            when (code) {
                Code.LONGSUBS -> {
                    if ((transactionDetail.qty-v.toDouble())>=0){
                        viewModel.updateTransDetail(transactionDetail, -1.0)
                    }else{
                        DialogUtils.showFailedWarning(requireContext(),transactionDetail.trans_item_name)
                    }

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
                }Code.UNITQTY->{
                    viewModel.updateUitQty(transactionDetail,v.toDouble())
                }else->{
                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
                }
            }
          //  imm.hideSoftInputFromWindow(view?.windowToken, 0)
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

    fun showInputCoiceDialog(item: TransactionDetail) {
        val binding :PopUpUnitBinding = PopUpUnitBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context).setTitle("PILIH").setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val radioButtonId = binding.radioGroup.checkedRadioButtonId
                val text: String? = if (radioButtonId == -1) {
                    null
                } else {
                    val radioButton = binding.radioGroup.findViewById<RadioButton>(radioButtonId)
                    radioButton.text.toString()
                }
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                viewModel.updateUnitTransDetail(text,item)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onResume() {
        super.onResume()
        viewModel.calculateDiscA()
    }

    override fun onPause() {
        super.onPause()
    }
    fun handleBackPress(): Boolean {
       viewModel.calculateDiscA()
        return false // Not handled, let activity navigate back
    }


}