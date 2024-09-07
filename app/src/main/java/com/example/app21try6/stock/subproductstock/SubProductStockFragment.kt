    package com.example.app21try6.stock.subproductstock

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.SubProduct
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentSubProductStockBinding
import com.google.android.material.textfield.TextInputEditText

class SubProductStockFragment : Fragment() {
    private lateinit var binding: FragmentSubProductStockBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_product_stock,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource3 = VendibleDatabase.getInstance(application).transDetailDao
        var id = arguments?.let { SubProductStockFragmentArgs.fromBundle(it).productId }
        (activity as AppCompatActivity).supportActionBar?.title = id?.last()
        id?.set(4,"0")
        val id_ = id?.map { it.toInt() }?.toTypedArray()
        Toast.makeText(context,  id?.get(0).toString() +"",Toast.LENGTH_LONG).show()
        val viewModelFactory = SubViewModelFactory(dataSource2,application,id_!!,dataSource3,
          0)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory).get(SubViewModel::class.java)
        binding.subViewModel = viewModel
        var adapter = SubAdapter(id_[3],
                CheckBoxListenerSub({view:View,subProduct:SubProduct->
                    val cb = view as CheckBox
                    subProduct.is_checked = cb.isChecked
                    viewModel.onCheckBoxClicked(subProduct,cb.isChecked)
                }),
                SubStokLongListener{
            subProduct->
            Toast.makeText(context,"long success",Toast.LENGTH_SHORT).show()
            showDialog(subProduct,1,viewModel)
        }, PlusStokListener {
            subProduct ->
            viewModel.addStockClicked(subProduct)
            Toast.makeText(context,subProduct.sub_name.toString()+"tambah 1",Toast.LENGTH_SHORT).show()
        }, SubsStokListener {
            subProduct ->
            viewModel.subsStockClicked(subProduct)
            Toast.makeText(context,subProduct.sub_name.toString()+"kurang 1",Toast.LENGTH_SHORT).show()
        }, WarnaStokListener {
            subProduct ->
            updateDialog(subProduct,2, viewModel)
        },
                KetStokListener {
                    subProduct ->
                    updateDialog(subProduct,  3, viewModel)
                }, SubListener {
            //var path_ = arrayOf(it.id,path)
            viewModel.onBrandCLick(arrayOf(it.sub_id.toString(),it.product_code.toString(),it.brand_code.toString(),it.cath_code.toString()))
        })
        binding.rvBrandStock.adapter = adapter
        viewModel.all_product_from_db.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedBy { it.sub_name })
                adapter.notifyDataSetChanged()
                Log.e("SUM","sub product list is"+it.toString())
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
                this.findNavController().navigate(SubProductStockFragmentDirections.actionSubProductStockFragmentToDetailFragment(id))
                viewModel.onBrandNavigated()
            }

        })
        // Inflate the layout for this fragment


        return binding.root
    }

    private fun showAddDialog(viewModel: SubViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val product_name = textBrand.text.toString().toUpperCase().trim()
            viewModel.insertAnItemSubProductStock(product_name)
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
    }

    private fun showDialog(subProduct: SubProduct, i: Int, viewModel: SubViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Chosoe Action")
            .setCancelable(true)
            .setPositiveButton("Update") { dialog, id ->
                updateDialog(subProduct,i,viewModel)
            }
            .setNegativeButton("Delete") { dialog, id ->
                deleteDialog(builder, viewModel,subProduct)
            }
        val alert = builder.create()
        alert.show()

    }

    private fun deleteDialog(builder: AlertDialog.Builder, viewModel: SubViewModel, subProduct: SubProduct) {
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
                viewModel.deleteSubProduct(subProduct)
                Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun updateDialog(subProduct: SubProduct, i: Int, viewModel: SubViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)

        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        var text=""
        if (i==1){ text = subProduct.sub_name.toString()
        }else if(i==2){text = subProduct.warna.toString()
        }else{text = subProduct.ket.toString()}
        if (text!="click to add"){ textKet.setText(text) }
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            val textV = textKet.text.toString().uppercase().trim()
            viewModel.updateSubProduct(subProduct, textV, i)
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()


    }


}