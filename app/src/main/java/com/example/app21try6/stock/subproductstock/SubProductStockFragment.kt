    package com.example.app21try6.stock.subproductstock

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentSubProductStockBinding
import com.example.app21try6.databinding.PopUpUpdateBayarBinding
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText

class SubProductStockFragment : Fragment() {
    private lateinit var binding: FragmentSubProductStockBinding
    private lateinit var viewModel: SubViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sub_product_stock,container,false)
        val application = requireNotNull(this.activity).application

        val stockRepo = StockRepositories(application)
        val transRepo=TransactionsRepository(application)
        val id = arguments?.let { SubProductStockFragmentArgs.fromBundle(it).productId }
        (activity as AppCompatActivity).supportActionBar?.title = id?.get(4)
        //id?.set(4,"0")
        val productId=id?.get(0)?.toLong()
        val brandId=id?.get(1)?.toLong()
        val ctgId=id?.get(2)?.toLong()
        val tSId=id?.get(3)?.toLong()
        val productName=id?.get(4).toString()
        val sPId=id?.get(5)?.toInt()


        Log.i("SUBPROBLEM","id[0] ${id?.get(0)}  id[1] ${id?.get(1)}  id[2] ${id?.get(2)}  id[3] ${id?.get(3)} id[4] ${id?.get(4)} ")
      //  val id_ = id?.map { it.toInt() }?.toTypedArray()

       val viewModelFactory = SubViewModelFactory(stockRepo,transRepo,productId!!,brandId!!,ctgId!!,tSId!!, sPId!!,0L,application)
        binding.lifecycleOwner =this
        viewModel = ViewModelProvider(this,viewModelFactory).get(SubViewModel::class.java)
        binding.subViewModel = viewModel
        binding.reset.setOnClickListener {
            DialogUtils.showDeleteDialog(requireContext(),this, viewModel, SubProduct(), { vm, item -> (vm as SubViewModel).resetAllSubProductStock() })
        }

        val isSubIdExist = sPId
        if (isSubIdExist!=-1){
            if (binding.rvSubDetail.visibility==View.GONE){
                setDetailRvVisibility(true)
            }
            viewModel.getSubProductById(isSubIdExist)
        }

        val adapter = SubAdapter(tSId,
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
            Toast.makeText(context,subProduct.sub_name+"tambah 1",Toast.LENGTH_SHORT).show()
        }, SubsStokListener {
            subProduct ->
            viewModel.subsStockClicked(subProduct)
            Toast.makeText(context,subProduct.sub_name+"kurang 1",Toast.LENGTH_SHORT).show()
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
            }, DeleteDetailWarnaListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as SubViewModel).deleteDetailWarna(item as DetailMerchandiseModel) })

            }, EditDetailWarnaListener {
                Toast.makeText(context,"edited",Toast.LENGTH_SHORT).show()
            },TrackDetailWarnaListener {
                 viewModel.trackDetailWarna(it)
            }
        )
        val retailAdapter=DetailWarnaAdapter(DetailWarnaLongListener {

        }, DeleteDetailWarnaListener {
            //delete, show pop up delete
            DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as SubViewModel).deleteRetail(item as DetailMerchandiseModel) })
        }, EditDetailWarnaListener {
            //add show pop up
            showUpdateQtyDialog(it,Constants.Code.LONGPLUS.text,Constants.Code.LONGPLUS)
        }, TrackDetailWarnaListener {
            //substract, show pop up
            showUpdateQtyDialog(it,Constants.Code.LONGSUBS.text,Constants.Code.LONGSUBS)
        })

        binding.rvSubProduct.adapter = adapter
        binding.rvSubDetail.adapter=detailWarnaAdapter
        binding.rvRetail.adapter=retailAdapter
        binding.searchBarSub.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { query ->
                    viewModel.allProductFromDb.observe(viewLifecycleOwner) { list ->
                        list?.let { items ->
                            val filteredList = items.filter { item ->
                                item.sub_name.contains(query, ignoreCase = true)
                            }
                            adapter.submitList(filteredList)
                        }
                    }
                }
                return true
            }
        })

        viewModel.allProductFromDb.observe(viewLifecycleOwner) {
            it?.let {
      //          adapter.submitList(it.sortedBy { it.sub_name })
        //        adapter.notifyDataSetChanged()
            }
        }
        viewModel.subProductFromDb.observe(viewLifecycleOwner){
            it?.let {
                Log.i("SubList","$it")
                adapter.submitList(it.sortedBy { it.sub_name })
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.brandId.observe(viewLifecycleOwner){
            Log.i("ShowSubProbs","BrandId: $it")
        }

        viewModel.selectedSubProduct.observe(viewLifecycleOwner){
            viewModel.getDetailWarnaList(it?.sub_id)
            viewModel.getRetailList(it?.sub_id)
            adapter.selectedItemId = it?.sub_id  // Pass the selected ID to the adapter
            adapter.notifyDataSetChanged()
        }

        viewModel.detailWarnaList.observe(viewLifecycleOwner){it?.let {
            detailWarnaAdapter.submitList(it)

            }
        }

        viewModel.retailList.observe(viewLifecycleOwner){it?.let {
            retailAdapter.submitList(it)

        }}

        viewModel.addItem.observe(viewLifecycleOwner) {
            if (it == true) {
                if (viewModel.selectedSubProduct.value == null) {
                    DialogUtils.updateDialog(
                        context = requireContext(),
                        viewModel = viewModel, // Replace with your ViewModel instance
                        model = null,         // Replace with your model instance
                        title = "Update Brand",
                        getBrandName = { (it as SubProduct).sub_name },
                        setBrandName = { it, name -> (it as SubProduct).sub_name = name },
                        updateFunction = { vm, item ->
                            (vm as SubViewModel).updateSubProduct(
                                item as SubProduct,
                                "",
                                1
                            )
                        },
                        insertFunction = { vm, name ->
                            (vm as SubViewModel).insertAnItemSubProductStock(
                                name as String
                            )
                        }
                    )
                } else {
                    showAddDetailWarnaDialog()
                }
                viewModel.onItemAdded()
            }
        }
        viewModel.navigateProduct.observe(viewLifecycleOwner) { id ->
            id?.let {
                this.findNavController().navigate(
                    SubProductStockFragmentDirections.actionSubProductStockFragmentToDetailFragment(
                        id
                    )
                )
                viewModel.onBrandNavigated()
            }
        }
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
        }else if(i==2){text = subProduct.warna
        }else{text = subProduct.ket}
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
        txtBatchCount.requestFocus()
        txtBatchCount.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        txtNet.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
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
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        dialog.show()
    }
    fun setDetailRvVisibility(isVisible:Boolean){
        if (isVisible){
            binding.rvSubProduct.visibility=View.GONE
            binding.rvSubDetail.visibility=View.VISIBLE
            binding.rvRetail.visibility=View.VISIBLE
            binding.txtSubProduct?.visibility=View.VISIBLE
            binding.line1.visibility=View.VISIBLE
        }else{
            binding.rvSubProduct.visibility=View.VISIBLE
            binding.rvSubDetail.visibility=View.GONE
            binding.rvRetail.visibility=View.GONE
            binding.txtSubProduct?.visibility=View.GONE
            binding.line1.visibility=View.GONE
        }
    }
    fun handleBackPress(): Boolean {
        if (binding.rvSubDetail.visibility == View.VISIBLE) {
            // Toggle RecyclerViews visibility
            binding.rvSubDetail.visibility = View.GONE
            binding.rvRetail.visibility=View.GONE
            binding.line1.visibility=View.GONE
            binding.rvSubProduct.visibility = View.VISIBLE
            viewModel.toggleSelectedSubProductId(null)
            return true // Handled, so don't exit fragment
        }
        return false // Not handled, let activity navigate back
    }
    fun showUpdateQtyDialog(model:DetailMerchandiseModel,title:String,code: Constants.Code){
        DialogUtils.updateDialogQty<SubViewModel, DetailMerchandiseModel>(
            context = requireContext(),
            viewModel = viewModel,
            item=model,
            title = title,
            setBrandName = { it, number ->
                val model = it as DetailMerchandiseModel
                if (code == Constants.Code.LONGPLUS) {
                    model.net=  model.net + number.toDouble()
                } else if (code == Constants.Code.LONGSUBS){
                    val newQty=model.net-number
                    if (newQty>=0){
                        model.net= model.net - number.toDouble()
                    }else{
                        DialogUtils.showFailedWarning(requireContext(),model.net.toString())
                    }
                }
            },
            onUpdate = { vm, item ->
                (vm as SubViewModel).updateRetail(item as DetailMerchandiseModel)
            },
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear previous menu if needed
                menu.clear()
                // Inflate your custom menu
                menuInflater.inflate(R.menu.show_subproduct_by_product_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.my_toolbar_icon -> {
                        // Handle click
                        viewModel.onShowByBrandId()
                        //Toast.makeText(requireContext(), "Icon clicked!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



}