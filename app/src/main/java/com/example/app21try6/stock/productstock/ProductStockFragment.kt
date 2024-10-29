package com.example.app21try6.stock.productstock

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
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.Product
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentProductStockBinding
import com.example.app21try6.databinding.PopUpUpdateProductDialogBinding
import com.google.android.material.textfield.TextInputEditText

class   ProductStockFragment : Fragment() {
    private lateinit var binding: FragmentProductStockBinding
    private lateinit var discountNames: List<String>
    private lateinit var viewModel: ProductViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_product_stock,container,false)

        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).productDao
        val dataSource1 = VendibleDatabase.getInstance(application).discountDao
        val id = arguments?.let { ProductStockFragmentArgs.fromBundle(it).id }
        val new_id = id?.map { it }?.toMutableList()
        val title = new_id?.get(new_id.size-1)
        new_id?.remove(title)
        (activity as AppCompatActivity).supportActionBar?.title = title
        val id_ = new_id?.map { it.toInt() }?.toTypedArray()
        val viewModelFactory = ProductViewModelFactory(dataSource2,dataSource1,application,id_!!)
        binding.lifecycleOwner =this
       viewModel = ViewModelProvider(this,viewModelFactory)
                .get(ProductViewModel::class.java)
        binding.productViewModel = viewModel

        //Adapter
        val adapter = ProductStockAdapter(ProductStockListener {
            viewModel.onBrandCLick(arrayOf(it.product_id.toString(),it.brand_code.toString(),it.cath_code.toString(),"0",it.product_name))
        }, ProductStockLongListener {
            viewModel.getDiscNameById(it.discountId)
            showDialogBox(viewModel,it)
        })
        binding.rvProductStock.adapter = adapter
        viewModel.all_product_from_db.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedBy { it.product_name})
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.allDiscountFromDB.observe(viewLifecycleOwner, Observer {discounts ->
            Log.i("DiscProbs", "Discounts loaded: $discounts")
            if (discounts != null && discounts.isNotEmpty()) {
                discountNames = discounts // Store the discounts list
            } else {
                discountNames = emptyList()
            }
        })
       // Log.i("DiscProbs", "fragment: ${discountNames.toString()}")
        viewModel.addItem.observe(viewLifecycleOwner, Observer {
            if (it==true){
                showAddDialog(viewModel)
                viewModel.onItemAdded()
            }
        })
        viewModel.navigateProduct.observe(viewLifecycleOwner, Observer {id->
            id?.let {
            this.findNavController().navigate(ProductStockFragmentDirections.actionProductStockFragmentToSubProductStockFragment(id))
            viewModel.onBrandNavigated()
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showDialogBox(viewModel: ProductViewModel, vendible: Product) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Chosoe Action")
                .setCancelable(true)
                .setPositiveButton("Update") { dialog, id ->
                    updateDialog(viewModel,vendible)
                }
                .setNegativeButton("Delete") { dialog, id ->
                    deleteDialog(builder, viewModel,vendible)
                }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    private fun deleteDialog(builder: AlertDialog.Builder, viewModel: ProductViewModel, vendible: Product) {
        builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    viewModel.deleteProduct(vendible)
                    Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show() }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss() }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }

    private fun updateDialog(viewModel: ProductViewModel, vendible: Product) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        Log.i("DiscProbs", "updateDialogShows")
        Log.i("DiscProbs", "updatedialog: discountnames $discountNames")

        // Inflate the custom view for the dialog
        val dialogBinding = DataBindingUtil.inflate<PopUpUpdateProductDialogBinding>(
            LayoutInflater.from(context), R.layout.pop_up_update_product_dialog, null, false
        )

        // Initialize views from the binding
        val textKet = dialogBinding.textUpdateKet
        val textPrice = dialogBinding.textUpdatePrice
        val textCapital = dialogBinding.textCapital
        val textDisc = dialogBinding.textDiscount
        val textCapital2=dialogBinding.textCapital2
        val textModusNoet=dialogBinding.defaultNet

        dialogBinding.ilCapital2.hint="Modal 2"
        dialogBinding.ilDefaultNet.hint="net modus"
        dialogBinding.ilKet.hint="Product"
        dialogBinding.ilPrice.hint="Harga"
        dialogBinding.ilCapital.hint="Modal"
        dialogBinding.ilDisc.hint="Diskon"



        // Set up the AutoCompleteTextView with a mutable adapter
        val merkAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        textDisc.setAdapter(merkAdapter)

        // Observe the ViewModel LiveData and update the adapter
        viewModel.allDiscountFromDB.observe(viewLifecycleOwner) { allMerk ->
            allMerk?.let {
                Log.i("DiscProbs", "observer in dialog: $allMerk")
                merkAdapter.clear() // Clear the adapter's data
                merkAdapter.addAll(allMerk.sortedBy { it }) // Add the sorted data to the adapter
                merkAdapter.notifyDataSetChanged() // Notify the adapter about the data change
            }
        }

        // Set the data for the dialog fields
        textKet.setText(vendible.product_name.toString())
        textPrice.setText(vendible.product_price.toString())
        textCapital.setText(vendible.product_capital.toString())
        textCapital2.setText(vendible.alternate_price.toString())
        textModusNoet.setText(vendible.default_net.toString())
        val discName=viewModel.discountName.value
        if (discName!=null) textDisc.setText(discName)
        textKet.requestFocus()


        // Use dialogBinding.root instead of view
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Update") { dialog, which ->
            val priceString = textPrice.text.toString()
            val capitalString = textCapital.text.toString()

            val price = priceString.toIntOrNull()
            val capital = capitalString.toIntOrNull()
            val alternateCapital =textCapital2.text.toString().toDoubleOrNull()
            val modusNet =textModusNoet.text.toString().toDoubleOrNull()
            Log.i("capitalErr", "Price :$capital")


            if (price != null) {
                vendible.product_price = price
            } else {
                Log.i("capitalErr", "else Price :$price")
            }

            if (capital != null) {
                vendible.product_capital = capital
            } else {
                Log.i("capitalErr", "else Price :$capital")
            }
            vendible.product_name = textKet.text.toString().uppercase().trim()
            vendible.alternate_price=alternateCapital?:0.0
            vendible.default_net=modusNet ?: 0.0
            val discName = textDisc.text.toString().uppercase().trim()
            if (vendible.product_name.isNotEmpty()) {
                viewModel.updateProduct(vendible, discName)
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



    fun showAddDialog(viewModel: ProductViewModel) {
        val context = requireContext() // Ensure you have a valid context
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val dialogBinding = DataBindingUtil.inflate<PopUpUpdateProductDialogBinding>(
            LayoutInflater.from(context), R.layout.pop_up_update_product_dialog, null, false
        )

        // Initialize views from the binding
        val textKet = dialogBinding.textUpdateKet
        val textPrice = dialogBinding.textUpdatePrice
        val textCapital = dialogBinding.textCapital
        val textDisc = dialogBinding.textDiscount
        val textCapital2 = dialogBinding.textCapital2
        val textModusNoet = dialogBinding.defaultNet
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            discountNames
        )
        dialogBinding.ilCapital2.hint="Modal 2"
        dialogBinding.ilDefaultNet.hint="net modus"
        dialogBinding.ilKet.hint="Product"
        dialogBinding.ilPrice.hint="Harga"
        dialogBinding.ilCapital.hint="Modal"
        dialogBinding.ilDisc.hint="Diskon"

        textDisc.setAdapter(adapter)
        Log.i("DiscProbs", "Adapter set with discounts: $discountNames")

        // Set the dialog view
        builder.setView(dialogBinding.root) // Use dialogBinding.root

        builder.setPositiveButton("OK") { dialog, which ->
            val product_name = textKet.text.toString().uppercase().trim() // Use uppercase() instead of toUpperCase()
            var product_price: Int = 0
            textPrice.text.toString().toIntOrNull()?.let { price ->
                product_price = price
            }
            viewModel.insertAnItemProductStock(product_name, product_price)
        }

        builder.setNegativeButton("No") { dialog, which -> }

        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(context, R.color.dialogbtncolor))
    }

}