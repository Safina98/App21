    package com.example.app21try6.stock.subproductstock

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentSubProductStockBinding
import com.example.app21try6.databinding.PopUpUpdateBayarBinding
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText

class SubProductStockFragment : Fragment() {
    private lateinit var binding: FragmentSubProductStockBinding
    private lateinit var viewModel: SubViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_product_stock,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).subProductDao
        val productDao=VendibleDatabase.getInstance(application).productDao
        val dataSource3 = VendibleDatabase.getInstance(application).transDetailDao
        val dataSource4 = VendibleDatabase.getInstance(application).detailWarnaDao
        val id = arguments?.let { SubProductStockFragmentArgs.fromBundle(it).productId }
        (activity as AppCompatActivity).supportActionBar?.title = id?.last()
        id?.set(4,"0")
        val id_ = id?.map { it.toInt() }?.toTypedArray()
        val viewModelFactory = SubViewModelFactory(dataSource2,productDao,application,id_!!,dataSource3,dataSource4, 0)
        binding.lifecycleOwner =this
        viewModel = ViewModelProvider(this,viewModelFactory).get(SubViewModel::class.java)
        binding.subViewModel = viewModel
        binding.reset.setOnClickListener {
            DialogUtils.showDeleteDialog(requireContext(),this, viewModel, SubProduct(), { vm, item -> (vm as SubViewModel).resetAllSubProductStock() })
        }
        // Handle back button press

        val adapter = SubAdapter(id_[3],
            null,
        CheckBoxListenerSub{view:View,subProduct: SubProduct ->
            val cb = view as CheckBox
            subProduct.is_checked = cb.isChecked
            viewModel.onCheckBoxClicked(subProduct,cb.isChecked)
        }, SubStokLongListener{
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
        }, KetStokListener {
            subProduct ->
            updateDialog(subProduct,  3, viewModel)
        }, SubListener {
            if (binding.rvSubDetail.visibility==View.GONE){
                setDetailRvVisibility(true)
            }
            viewModel.toggleSelectedSubProductId(it)
        },requireContext())
        val detailWarnaAdapter=DetailWarnaAdapter(DetailWarnaLongListener {

        })
        binding.rvSubProduct.adapter = adapter
        binding.rvSubDetail?.adapter=detailWarnaAdapter
        binding.searchBarSub.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    viewModel.allProductFromDb.observe(viewLifecycleOwner, Observer { list ->
                        list?.let { items ->
                            val filteredList = items.filter { item ->
                                item.sub_name.contains(query, ignoreCase = true)
                            }
                            adapter.submitList(filteredList)
                        }
                    })
                }
                return true
            }
        })
        viewModel.allProductFromDb.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedBy { it.sub_name })
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.selectedSubProduct.observe(viewLifecycleOwner){

            viewModel.getDetailWarnaList(it?.sub_id)
            adapter.selectedItemId = it?.sub_id  // Pass the selected ID to the adapter
            adapter.notifyDataSetChanged()
        }
        viewModel.detailWarnaList.observe(viewLifecycleOwner){it?.let {
            detailWarnaAdapter.submitList(it)
           // adapter.notifyDataSetChanged()
            }
        }
        viewModel.addItem.observe(viewLifecycleOwner, Observer {
            if (it==true){
                if (viewModel.selectedSubProduct.value==null){
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
                }else{
                    showAddDetailWarnaDialog()
                }
                viewModel.onItemAdded()
            }
        })
        viewModel.navigateProduct.observe(viewLifecycleOwner, Observer {id->
            id?.let {
                this.findNavController().navigate(SubProductStockFragmentDirections.actionSubProductStockFragmentToDetailFragment(id))
                viewModel.onBrandNavigated()
            }
        })
        binding.txtSubProduct?.setOnClickListener {
            if (binding.rvSubProduct.visibility==View.GONE) {
                viewModel.toggleSelectedSubProductId(null)
                setDetailRvVisibility(false)

            }
        }
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
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))

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
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }

    fun showAddDetailWarnaDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val binding = PopUpUpdateBayarBinding.inflate(inflater)
        val txtBatchCount=binding.textUpdateDate
        val txtNet=binding.textUpdatePrice
        val ilBatchCount=binding.ilUpdateDate
        val ilNet=binding.ilHarga
        ilBatchCount.hint="Jumlah"
        ilNet.hint="Isi"
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Update Bayar")
            .setPositiveButton("Save") { dialogInterface, _ ->
                val batchCount=txtBatchCount.text.toString().trim().toDouble()
                val net=txtNet.text.toString().trim().toDouble()
                viewModel.insertDetailWarna(batchCount,net)
                // Handle the save action (e.g., pass data to ViewModel or update UI)
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }
    fun setDetailRvVisibility(isVisible:Boolean){
        if (isVisible){
            binding.rvSubProduct.visibility=View.GONE
            binding.rvSubDetail.visibility=View.VISIBLE
            binding.txtSubProduct?.visibility=View.VISIBLE
        }else{
            binding.rvSubProduct.visibility=View.VISIBLE
            binding.rvSubDetail.visibility=View.GONE
            binding.txtSubProduct?.visibility=View.GONE
        }
    }
    fun handleBackPress(): Boolean {
        if (binding.rvSubDetail.visibility == View.VISIBLE) {
            // Toggle RecyclerViews visibility
            binding.rvSubDetail.visibility = View.GONE
            binding.rvSubProduct.visibility = View.VISIBLE
            viewModel.toggleSelectedSubProductId(null)
            return true // Handled, so don't exit fragment
        }
        return false // Not handled, let activity navigate back
    }

}