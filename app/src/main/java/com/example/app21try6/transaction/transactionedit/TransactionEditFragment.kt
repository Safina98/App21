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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionEditBinding
import com.example.app21try6.databinding.PopUpUnitBinding
import com.google.android.material.textfield.TextInputEditText


enum class Code(val text: String) {ZERO(""),LONGPLUS("Tambah"),LONGSUBS("Kurang"),TEXTITEM("Update Nama Barang"),TEXTPRICE("Update Harga barang"),UNITQTY("ISI")}
class TransactionEditFragment : Fragment() {
    private lateinit var binding:FragmentTransactionEditBinding
    private lateinit var adapter:TransactionEditAdapter
    val viewModel:TransactionEditViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       binding = DataBindingUtil.inflate(inflater, com.example.app21try6.R.layout.fragment_transaction_edit, container, false)

        val application = requireNotNull(this.activity).application

        val datasource1 = VendibleDatabase.getInstance(application).transSumDao
        val datasource2 = VendibleDatabase.getInstance(application).transDetailDao
        val datasource3 = VendibleDatabase.getInstance(application).discountDao
        val datasource4 = VendibleDatabase.getInstance(application).customerDao
        val datasource5 = VendibleDatabase.getInstance(application).discountTransDao
        val id= arguments?.let{TransactionEditFragmentArgs.fromBundle(it).id}

        val viewModelFactory = TransactionEditViewModelFactory(application, datasource1, datasource2,datasource3,datasource5,datasource4, id!!)
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
       }, TransEditDeleteListener {
           deleteDialog(it, viewModel)
       }, TransEditPriceClickListener {
           showDialog(it,viewModel,Code.TEXTPRICE)
       }, UnitTransTextCliked{ edit_trans ->
               showInputCoiceDialog(edit_trans)
       }, TransEditUnitQtyClickListener {
               showDialog(it,viewModel,Code.UNITQTY)
       }, viewModel.updatePositionCallback
          )
       //TODO create custom adapter that shows or hide checkbox for delete purpose

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
        val suggestions = arrayOf(
            "Asia Jok", "Alyka Jok","Aisya Jok","Auto 354","AMV","Akbar Sengkang","Asep Ramlan","Anugrah Mebel","AT Jok",
            "Bandung Jok","Bandung Jok Gowa","Bagus Jok","Beo","Berkah Variasi",
            "Cahaya Variasi",
            "dr Jok","Dyna Jok","D'fun Kendari","Densus 99",
            "Eka Jok","Evolution",
            "Fiesta Jok","Fakhri Jok",
            "Green Design",
            "HSR Auto",
            "Jabal","Jok 88",
            "King Variasi","Karya Jok","Kubis Mebel",
            "Makassar Variasi","Mega Buana","Mas Tono",
            "Laquna",
            "Pak Maliang", "Pak Ilham", "Pak Ramli Sidrap","Pak Ibet","Pak Agus Saputra","Pattalassang Variasi","Prima leather","Pak Alim",
            "Rajawali Motor Timika","Rumah Kursi","Rumah Sofa Kolut","RGARAGE","Rezky Jok","Riski Jok",
            "Sun Variasi","Susan Jok", "Sumber Jok","Surabaya Motor","Selayar","SKAAD Bintuni","Sam & Sons",
            "Terminal Jok",
            "Unang",
            "Variasi 77",
            "Wendy",
            "Yusdar Motor")
        //val adapterr: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        //autoCompleteTextView.setAdapter(adapterr)

       viewModel.itemTransDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               adapter.submitList(it)

               adapter.setItemsValue(it)
           }
       })

        viewModel.allCustomerTable.observe(viewLifecycleOwner, Observer { customerList ->

            val customerNames = customerList.map { it.customerBussinessName }
            val adapterr = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, customerNames)
            autoCompleteTextView.setAdapter(adapterr)
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
               viewModel.setCustomerName()
           }
       })
       viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
           it?.let {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionDetailFragment(it))
               viewModel.onNavigatedtoDetail()
           }
       })
        viewModel.transDetailWithProduct.observe(viewLifecycleOwner, Observer {
            it?.let {
            }
        })
       viewModel.navigateToVendible.observe(viewLifecycleOwner, Observer {
           if (it != null) {
               this.findNavController().navigate(TransactionEditFragmentDirections.actionTransactionEditFragmentToTransactionProductFragment(it))
               viewModel.setCustomerName()
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
               textKet.setText(transactionDetail.unit_qty.toString())
               textKet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
           }
        }
        textKet.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
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
                }Code.UNITQTY->{
                    viewModel.updateUitQty(transactionDetail,v.toDouble())
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
    fun showInputCoiceDialog(item:TransactionDetail) {
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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onPause() {
        viewModel.updateCustNameSum()
        super.onPause()
    }


}