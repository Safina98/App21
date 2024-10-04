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
        val discName=viewModel.discountName.value
        if (discName!=null) textDisc.setText(discName)
        textKet.requestFocus()

        // Show the soft keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // Use dialogBinding.root instead of view
        builder.setView(dialogBinding.root)
        builder.setPositiveButton("Update") { dialog, which ->
            val priceString = textPrice.text.toString()
            val capitalString = textCapital.text.toString()

            val price = priceString.toIntOrNull()
            val capital = capitalString.toIntOrNull()
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
            val discName = textDisc.text.toString().uppercase().trim()
            if (vendible.product_name.isNotEmpty()) {
                viewModel.updateProduct(vendible, discName)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()
    }



    fun showAddDialog(viewModel: ProductViewModel){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update_product_dialog, null)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdatePrice)
        val txtDiscount =view.findViewById<AutoCompleteTextView>(R.id.text_discount)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            discountNames
        )

        txtDiscount.setAdapter(adapter)
        Log.i("DiscProbs", "Adapter set with discounts: $discountNames")
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val product_name = textBrand.text.toString().toUpperCase().trim()
            var product_price :Int=0
            if(textPrice.text.toString().toIntOrNull()!=null){
                product_price = textPrice.text.toString().toInt()}
            viewModel.insertAnItemProductStock(product_name,product_price)
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()

    }
}