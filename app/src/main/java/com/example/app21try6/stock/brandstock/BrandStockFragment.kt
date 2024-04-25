package com.example.app21try6.stock.brandstock


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.BuildConfig
import com.example.app21try6.R
import com.example.app21try6.database.Brand
import com.example.app21try6.database.Category
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentBrandStockBinding
import com.google.android.material.textfield.TextInputEditText
import java.io.*


class BrandStockFragment : Fragment() {
    private lateinit var binding: FragmentBrandStockBinding
    private val PERMISSION_REQUEST_CODE = 200
    val requestcode = 1
    private val viewModel:BrandStockViewModel by viewModels()

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var i = 1
            try {
                context?.contentResolver?.openInputStream(data!!.data!!)?.bufferedReader()?.forEachLine {
                    val tokens: List<String> = it.split(",")
                    if (i!=1) { viewModel.insertCSV(tokens) }
                    i+=1
                }
            }catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show()
                Log.e("message_e", ""+e)
            }
        }
    }
            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
                binding = DataBindingUtil.inflate(inflater,R.layout.fragment_brand_stock,container,false)
                val application = requireNotNull(this.activity).application
                val dataSource1 = VendibleDatabase.getInstance(application).categoryDao
                val dataSource2 = VendibleDatabase.getInstance(application).brandDao
                val dataSource3 = VendibleDatabase.getInstance(application).productDao
                val dataSource4 = VendibleDatabase.getInstance(application).subProductDao
                val viewModelFactory = BrandStockViewModelFactory(dataSource1,dataSource2,dataSource3,dataSource4,application)
                binding.lifecycleOwner =this
                val viewModel = ViewModelProvider(this,viewModelFactory)
                    .get(BrandStockViewModel::class.java)
                binding.brandStockViewModel = viewModel

                ////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////Exporting CSV////////////////////////////////////////////////////////////////////////////
                if (checkPermission()) {
                    //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission()
                }

                val adapter = BrandStockAdapter(BrandStockListener {  
                    viewModel.onBrandCLick(arrayOf(it.brand_id.toString(),it.cath_code.toString(),it.brand_name))
                }, BrandStockLongListener { 
                    showDialogBox(viewModel,it)
                })
                binding.rvBrandStock.adapter = adapter
                viewModel.all_brand.observe(viewLifecycleOwner, Observer {})
                viewModel.all_item.observe(viewLifecycleOwner, Observer {})
                viewModel.all_product.observe(viewLifecycleOwner, Observer {})
                viewModel.all_sub.observe(viewLifecycleOwner, Observer {})

                binding.spinnerM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        viewModel.setSelectedKategoriValue(selectedItem)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }

                viewModel.cathList_.observe(viewLifecycleOwner){entries->
                    val adapter1 = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, entries)
                    binding.spinnerM.adapter = adapter1
                }
                viewModel.selectedKategoriSpinner.observe(viewLifecycleOwner) {
                    viewModel.updateRv()
                }
                viewModel.all_brand_from_db.observe(viewLifecycleOwner){
                    adapter.submitList(it.sortedBy { it.brand_name})
                    adapter.notifyDataSetChanged()
                }
/*
              viewModel.itemCathPosition.observe(viewLifecycleOwner, Observer {t->
                  viewModel.cathList.observe(viewLifecycleOwner, Observer {})
                  h = t
                  viewModel.all_brand_from_db.observe(viewLifecycleOwner, Observer {
                      it?.let {
                          if (h==t){
                            adapter.submitList(it.sortedBy { it.brand_name})
                            adapter.notifyDataSetChanged() } } }) })

 */

                viewModel.addItem.observe(viewLifecycleOwner, Observer {
                    if (it==true){
                        showAddDialog(viewModel,1)
                        viewModel.onItemAdded() } })
                viewModel.addCath.observe(viewLifecycleOwner, Observer {
                    if (it==true){
                        showAddDialog(viewModel,2)
                        viewModel.onCathAdded() } })
                viewModel.delCath.observe(viewLifecycleOwner, Observer {
                    if (it==true){
                        showListDialog(viewModel)
                        viewModel.onDeleteCathCliked() } })
                viewModel.navigateProduct.observe(viewLifecycleOwner, Observer {id ->
                    id?.let {
                       this.findNavController().navigate(BrandStockFragmentDirections.actionBrandStockFragmentToProductStockFragment(id))
                        viewModel.onBrandNavigated() } })
                setHasOptionsMenu(true)
                return binding.root
    }

    private fun showListDialog(viewModel: BrandStockViewModel) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose to delete")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_dialog, null)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView_vendibleDialog)
        val adapter = CategoryAdapter(CheckBoxListenerDoalog{ view: View, category: Category ->
            val cb = view as CheckBox
            category.checkBoxBoolean = cb.isChecked
            viewModel.onCheckBoxClicked(category,cb.isChecked) })
        rv.adapter = adapter
        viewModel.cathList.observe(viewLifecycleOwner, Observer { it.let { adapter.submitList(it) } })
        val vendible=Brand()
        builder.setView(view)
                .setCancelable(true)
                .setPositiveButton("Update") { dialog, id ->
                   val category = viewModel.getSelectedCategory()
                    updateDialog(viewModel,vendible,category,2) }
                .setNegativeButton("Delete") { dialog, id -> deleteDialog(viewModel,vendible,2) }
                .setOnCancelListener {
                    viewModel.cathList.observe(viewLifecycleOwner, Observer {
                        it.let {
                            for (i in it){ i.checkBoxBoolean = false }
                            viewModel.clearCheckedItemList() } }) }
        val alert = builder.create()
        alert.show()
    }

    private fun showDialogBox(viewModel: BrandStockViewModel, vendible: Brand) {
        val builder = AlertDialog.Builder(context)
        val category = Category()
        builder.setMessage("Chosoe Action")
            .setCancelable(true)
            .setPositiveButton("Update") { dialog, id -> updateDialog(viewModel,vendible,category,1) }
            .setNegativeButton("Delete") { dialog, id -> deleteDialog( viewModel,vendible,1) }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteDialog(viewModel: BrandStockViewModel, vendible: Brand,code:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id -> if (code==1){ viewModel.deleteBrand(vendible)}else{viewModel.deleteDialog()} }
            .setNegativeButton("No") { dialog, id -> dialog.dismiss() }
                .setOnCancelListener { viewModel.cathList.observe(viewLifecycleOwner, Observer {
                        it.let { for (i in it){ i.checkBoxBoolean = false }
                            viewModel.clearCheckedItemList() } }) }
        val alert = builder.create()
        alert.show()
    }

    private fun updateDialog(viewModel: BrandStockViewModel, vendible: Brand,category: Category,code: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textKet = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        if (code==1){textKet.setText(vendible.brand_name.toString())}else{textKet.setText(category.category_name.toString())}
        builder.setView(view)
        builder.setPositiveButton("Update") { dialog, which ->
            if (code==1){ vendible.brand_name = textKet.text.toString().toUpperCase().trim()
                if (vendible.brand_name!="") { viewModel.updateBrand(vendible) }}
            else{ category.category_name = textKet.text.toString().toUpperCase().trim()
                if (category.category_name!="") { viewModel.updateCath(category) } } }
        builder.setNegativeButton("No") { dialog, which -> }
        builder.setOnCancelListener {
            viewModel.cathList.observe(viewLifecycleOwner, Observer {
                it.let { for (i in it){ i.checkBoxBoolean = false }
                    viewModel.clearCheckedItemList() } })
        }
        val alert = builder.create()
        alert.show()
    }
    fun showAddDialog(viewModel: BrandStockViewModel,i:Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Tambah Item")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update, null)
        val textBrand = view.findViewById<TextInputEditText>(R.id.textUpdateKet)
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            val brand = textBrand.text.toString().toUpperCase()
            if (i==1){viewModel.insertAnItemBrandStock(brand)}
            else {viewModel.insertItemCath(brand)}
        }
        builder.setNegativeButton("No") { dialog, which ->
        }
        val alert = builder.create()
        alert.show()
    }
    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(requireActivity(),arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menu_export_csv -> {
                exportStockCSV()
                return true
            }
            R.id.menu_import->{
                importCSVStock()
                return  true
            }
        }
        return super.onOptionsItemSelected(item) // important line
    }

    private fun importCSVStock() {
        var fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "text/*"
        try { resultLauncher.launch(fileIntent) }
        catch (e: FileNotFoundException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show() }
    }

    private fun exportStockCSV() {
        val fileName = "Stok 21"
        //var file:File
        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv")
        Log.i("FilePath","else path: " +file.path.toString())
        viewModel.writeCSV(file)

        val photoURI: Uri = FileProvider.getUriForFile(this.requireContext(), requireContext().applicationContext.packageName + ".provider",file)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, photoURI)
            type = "text/*"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(Intent.createChooser(shareIntent, "Stok"))
        }catch (e : java.lang.Exception){
            Log.i("error_msg",e.toString())
        }
    }


}
