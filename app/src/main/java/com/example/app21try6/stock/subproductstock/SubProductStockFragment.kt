    package com.example.app21try6.stock.subproductstock

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentSubProductStockBinding
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText

class SubProductStockFragment : Fragment() {
    private lateinit var binding: FragmentSubProductStockBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_product_stock,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource3 = VendibleDatabase.getInstance(application).transDetailDao
        val id = arguments?.let { SubProductStockFragmentArgs.fromBundle(it).productId }
        (activity as AppCompatActivity).supportActionBar?.title = id?.last()
        id?.set(4,"0")
        val id_ = id?.map { it.toInt() }?.toTypedArray()
        val viewModelFactory = SubViewModelFactory(dataSource2,application,id_!!,dataSource3, 0)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory).get(SubViewModel::class.java)
        binding.subViewModel = viewModel
        binding.reset.setOnClickListener {
            DialogUtils.showDeleteDialog(requireContext(),this, viewModel, SubProduct(), { vm, item -> (vm as SubViewModel).resetAllSubProductStock() })
        }
        val adapter = SubAdapter(id_[3],
                CheckBoxListenerSub({view:View,subProduct: SubProduct ->
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
                DialogUtils.updateDialog(
                    context = requireContext(),
                    viewModel = viewModel, // Replace with your ViewModel instance
                    model = null,         // Replace with your model instance
                    title = "Update Brand",
                    getBrandName = { (it as SubProduct).sub_name },
                    setBrandName = { it, name -> (it as SubProduct).sub_name = name },
                    updateFunction = { vm, item -> (vm as SubViewModel).updateSubProduct(item as SubProduct,"",1) },
                    insertFunction = { vm, name -> (vm as SubViewModel).insertAnItemSubProductStock(name as String) }
                )
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



    private fun showDialog(subProduct: SubProduct, i: Int, viewModel: SubViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Chosoe Action")
            .setCancelable(true)
            .setPositiveButton("Update") { dialog, id ->
                updateDialog(subProduct,i,viewModel)
            }
            .setNegativeButton("Delete") { dialog, id ->
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, subProduct, { vm, item -> (vm as SubViewModel).deleteSubProduct(item as SubProduct) })
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))

    }


    private fun updateDialog(subProduct: SubProduct, i: Int, viewModel: SubViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.pop_up_update, null)

        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        val lv= view.findViewById<LinearLayout>(R.id.lbl_id)
        val tVId=view.findViewById<TextView>(R.id.txt_id)
        var text=""
        if (i==1){
            text = subProduct.sub_name.toString()
            lv.visibility=View.VISIBLE
            tVId.setText(subProduct.sub_id.toString())
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
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.dialogbtncolor))
    }


}