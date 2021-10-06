package com.example.app21try6.stock.productstock

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.textfield.TextInputEditText

class   ProductStockFragment : Fragment() {
    private lateinit var binding: FragmentProductStockBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_product_stock,container,false)

        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).productDao
        var id = arguments?.let { ProductStockFragmentArgs.fromBundle(it).id }
        var new_id = id?.map { it }?.toMutableList()
        var title = new_id?.get(new_id.size-1)
        new_id?.remove(title)
        (activity as AppCompatActivity).supportActionBar?.title = title
        val id_ = new_id?.map { it.toInt() }?.toTypedArray()
        val viewModelFactory = ProductViewModelFactory(dataSource2,application,id_!!)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory)
                .get(ProductViewModel::class.java)
        binding.productViewModel = viewModel

        //Adapter
        val adapter = ProductStockAdapter(ProductStockListener {
            viewModel.onBrandCLick(arrayOf(it.product_id.toString(),it.brand_code.toString(),it.cath_code.toString(),"0",it.product_name))
        }, ProductStockLongListener {
            showDialogBox(viewModel,it)
        })
        binding.rvProductStock.adapter = adapter
        viewModel.all_product_from_db.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedBy { it.product_name})
                adapter.notifyDataSetChanged()
            }
        })

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
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_product_dialog, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdatePrice)
        textKet.setText(vendible.product_name.toString())
        textPrice.setText(vendible.product_price.toString())
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            if(textPrice.text.toString().toIntOrNull()!=null){
            vendible.product_price = textPrice.text.toString().toInt()}
            vendible.product_name = textKet.text.toString().toUpperCase().trim()
            if (vendible.product_name!=""){viewModel.updateProduct(vendible)}
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
    }


    fun showAddDialog(viewModel: ProductViewModel){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_product_dialog, null)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val textPrice = view.findViewById<TextInputEditText>(R.id.textUpdatePrice)
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