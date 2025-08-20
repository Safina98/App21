package com.example.app21try6.stock.brandstock


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.app21try6.Constants
import com.example.app21try6.R
import com.example.app21try6.ToolbarUtil
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentBrandStockBinding
import com.example.app21try6.stock.productstock.ProductStockFragmentDirections
import com.example.app21try6.utils.CsvHandler
import com.example.app21try6.utils.DatabaseBackupHelper
import com.example.app21try6.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class BrandStockFragment : Fragment() {
    private lateinit var binding: FragmentBrandStockBinding
    private val PERMISSION_REQUEST_CODE = 200
    private lateinit var csvHandler: CsvHandler
    val requestcode = 1
    private val viewModel:BrandStockViewModel by viewModels()
    private var list= mutableListOf<String>()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            csvHandler.handleCsvResult(result) { tokensList ->
                viewModel.insertCSVBatch(tokensList)  // Example: Using viewModel1
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_brand_stock,container,false)
        val application = requireNotNull(this.activity).application
        val repository = StockRepositories(application)
        val discountRepository=DiscountRepository(application)
        val viewModelFactory = BrandStockViewModelFactory(repository,discountRepository,application)
        binding.lifecycleOwner =this
        val layoutOneViews = listOf(
            binding.spinnerM,
            binding.rvBrandStock,
        )
        csvHandler = CsvHandler(requireContext())
        val viewModel = ViewModelProvider(this,viewModelFactory).get(BrandStockViewModel::class.java)
        binding.brandStockViewModel = viewModel

                ////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////Exporting CSV////////////////////////////////////////////////////////////////////////////
        if (checkPermission()) {
        //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission()
        }
        ////////////////////////////////custom on back pressed////////////////////////////////
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val layoutOneViews = listOf(
                    binding.spinnerM,
                    binding.rvBrandStock,
                )
                Log.i("CustomBackProbs","if condition met")
                if (layoutOneViews[0].visibility == View.GONE && binding.rvProductStock.visibility==View.VISIBLE && binding.rvCat.visibility==View.GONE) {
                    Log.i("CustomBackProbs","if condition met")
                    viewModel.getBrandIdByName(null)
                    layoutOneViews.forEach { it.visibility = View.VISIBLE }
                    binding.rvProductStock.visibility = View.GONE
                    binding.txtBrand?.visibility = View.GONE
                    binding.btnEditEcNew.visibility = View.VISIBLE
                    binding.cardView.visibility = View.GONE
                    return
                }else if(layoutOneViews[0].visibility == View.VISIBLE && binding.rvProductStock.visibility==View.VISIBLE&&viewModel.selectedBrand.value!=null){
                    Log.i("CustomBackProbs"," first if else condition met")
                    viewModel.getBrandIdByName(null)
                   // binding.rvProductStock.visibility = View.INVISIBLE
                }else if(layoutOneViews[0].visibility == View.GONE && binding.rvCat.visibility==View.VISIBLE){
                   // binding.btnEditEcNew.visibility = View.VISIBLE
                    Log.i("CustomBackProbs"," second if else condition met")
                    binding.cardView.visibility = View.GONE
                    if(binding.txtBrand==null){
                        binding.rvProductStock.visibility=View.VISIBLE
                    }
                    layoutOneViews.forEach { it.visibility = View.VISIBLE }

                }

                else {
                    Log.i("CustomBackProbs","else condition met")
                    isEnabled = false // Disable callback temporarily
                    requireActivity().onBackPressed() // Trigger default back behavior
                }

            }
        })



        /////////////////////////////////////Initalizing Adapter/////////////////////////////////
        //BRAND ADAPTER
        val adapter = BrandStockAdapter(
            BrandStockListener {
                viewModel.getBrandIdByName(it)

            },BrandStockLongListener {
                showDialogBox(viewModel,it,Constants.MODELTYPE.BRAND)
            },null,requireContext())

        //Kategori adapter
        val adapterCat = CategoryAdapter(
            UpdateListener {
                DialogUtils.updateDialog(
                    context = requireContext(),
                    viewModel = viewModel,
                    model = it,
                    title = "Update Kategori",
                    getBrandName = { (it as CategoryModel).categoryName },
                    setBrandName = { it, name -> (it as CategoryModel).categoryName = name },
                    updateFunction = { vm, item -> (vm as BrandStockViewModel).updateCath(item as CategoryModel) },
                    insertFunction = { vm, name -> (vm as BrandStockViewModel).insertItemCath(name as String) }
                )
            },DeleteListener {
                DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as BrandStockViewModel).deleteCategory(item as CategoryModel) })
            })

        //Product adapter
        val adapterProduct = BrandStockAdapter(
            BrandStockListener {
                viewModel.onProductCLick(arrayOf(it.id.toString(),it.parentId.toString(),viewModel.selectedBrand.value?.parentId.toString(),"0",it.name))
            }, BrandStockLongListener {
                viewModel.getLongClickedProduct(it.id)
                showDialogBox(viewModel,it,Constants.MODELTYPE.PRODUCT)
            },null,requireContext())


        //////////////////////////////bindings//////////////////////////////////////////////////
        //Recyler views
        binding.rvBrandStock.adapter = adapter
        binding.rvCat.adapter = adapterCat
        binding.rvProductStock.adapter = adapterProduct

        //Spinner
        binding.spinnerM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.setSelectedKategoriValue(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        //Buttons
        binding.btnEditEcNew.setOnClickListener {
            if (layoutOneViews[0].visibility == View.VISIBLE) {
                layoutOneViews.forEach { it.visibility = View.GONE }
                binding.cardView.visibility = View.VISIBLE


            } else {
                layoutOneViews.forEach { it.visibility = View.VISIBLE }
                binding.cardView.visibility = View.GONE
            }
        }
        binding.txtBrand?.setOnClickListener {
            if (layoutOneViews[0].visibility == View.GONE) {
                layoutOneViews.forEach { it.visibility = View.VISIBLE }
                binding.rvProductStock.visibility = View.GONE
                binding.txtBrand?.visibility=View.GONE
                binding.btnEditEcNew.visibility=View.VISIBLE
            }
        }
        ///////////////////////////////Observers///////////////////////////////////////////////
        //write csv
        //viewModel.all_brand.observe(viewLifecycleOwner, Observer {})
        viewModel.all_item.observe(viewLifecycleOwner, Observer {})

        //Spinner
        viewModel.cathList_.observe(viewLifecycleOwner){entries->
            val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, entries)
            binding.spinnerM.adapter = adapter1
        }
        viewModel.selectedKategoriSpinner.observe(viewLifecycleOwner) {
            viewModel.updateRv()
        }
        //Kategori adapter
        viewModel.cathList.observe(viewLifecycleOwner, Observer {
            it.let {
                adapterCat.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })
        //Brand adapter
        viewModel.all_brand_from_db.observe(viewLifecycleOwner){
            adapter.submitList(it.sortedBy { it.name})
            adapter.notifyDataSetChanged()
        }
        // product adapter
        viewModel.all_product_from_db.observe(viewLifecycleOwner, Observer {
                adapterProduct.submitList(it.sortedBy { it.name})
                adapterProduct.notifyDataSetChanged()
        })

        //to toggle selected recyclerview
        viewModel.selectedBrand.observe(viewLifecycleOwner){
            if (binding.linear1!=null){
                layoutOneViews.forEach { it.visibility = View.GONE }
                binding.btnEditEcNew.visibility=View.GONE
                binding.txtBrand?.visibility=View.VISIBLE
                binding.rvProductStock.visibility=View.VISIBLE
            }
            viewModel.updateProductRv(it?.id)
            adapter.selectedItemId = it?.id  // Pass the selected ID to the adapter
            adapter.notifyDataSetChanged()
        }
        //Obsserve add fab
        viewModel.addItem.observe(viewLifecycleOwner,Observer{
            if (it==true){
                if (layoutOneViews[0].visibility==View.VISIBLE){
                    if (viewModel.selectedBrand.value==null){
                        if (layoutOneViews[0].visibility == View.VISIBLE) {
                            DialogUtils.updateDialog(
                                context = requireContext(),
                                viewModel = viewModel, // Replace with your ViewModel instance
                                model = null,         // Replace with your model instance
                                title = "Brand Baru",
                                getBrandName = { (it as Brand).brand_name },
                                setBrandName = { it, name -> (it as Brand).brand_name = name },
                                updateFunction = { vm, item -> (vm as BrandStockViewModel).updateBrand(item as BrandProductModel) },
                                insertFunction = { vm, name -> (vm as BrandStockViewModel).insertAnItemBrandStock(name as String) }
                            )
                        }

                    }else{
                        DialogUtils.updateDialog(requireContext(),viewModel,list)

                    }
                }else{
                    DialogUtils.updateDialog(
                        context = requireContext(),
                        viewModel = viewModel, // Replace with your ViewModel instance
                        model = null,         // Replace with your model instance
                        title = "Kategori Baru",
                        getBrandName = { (it as CategoryModel).categoryName },
                        setBrandName = { it, name -> (it as CategoryModel).categoryName = name },
                        updateFunction = { vm, item -> (vm as BrandStockViewModel).updateCath(item as CategoryModel) },
                        insertFunction = { vm, name -> (vm as BrandStockViewModel).insertItemCath(name as String) }
                    )

                }
                viewModel.onItemAdded()
            }
        })


        viewModel.allDiscountFromDB.observe(viewLifecycleOwner, Observer {discounts ->
            if(discounts!=null){
                list=discounts.toMutableList()
            }
        })
        viewModel.addProduct.observe(viewLifecycleOwner, Observer {
            if (it==true){
                val list=viewModel.allDiscountFromDB.value
                DialogUtils.updateDialog(requireContext(),viewModel,list)
                viewModel.onProductAdded()
            }
        })

        viewModel.navigateProduct.observe(viewLifecycleOwner, Observer {id ->
            id?.let {
                //ToolbarUtil.hideToolbarButtons(requireActivity())
                this.findNavController().navigate(BrandStockFragmentDirections.actionBrandStockFragmentToSubProductStockFragment(id))
                viewModel.onBrandNavigated()
            } })
        setHasOptionsMenu(true)
        return binding.root
    }



    private fun showDialogBox(viewModel: BrandStockViewModel, vendible: BrandProductModel,modelType:String) {
        val builder = AlertDialog.Builder(context)

        builder.setMessage("Chosoe Action")
            .setCancelable(true)
            .setPositiveButton("Update") { dialog, id ->
                if(modelType==Constants.MODELTYPE.BRAND){
                    DialogUtils.updateDialog(
                        context = requireContext(),
                        viewModel = viewModel, // Replace with your ViewModel instance
                        model = vendible,         // Replace with your model instance
                        title = "Update Brand",
                        getBrandName = { (it as BrandProductModel).name },
                        setBrandName = { it, name -> (it as BrandProductModel).name = name },
                        updateFunction = { vm, item -> (vm as BrandStockViewModel).updateBrand(item as BrandProductModel)},
                        insertFunction = { vm, name -> (vm as BrandStockViewModel).insertAnItemBrandStock(name as String) }
                    )
                }else{
                    DialogUtils.updateDialog(requireContext(),viewModel,list)
                }

            }
            .setNegativeButton("Delete") { dialog, id ->
                DialogUtils.showDeleteDialog(
                    requireContext(),
                    this,
                    viewModel,
                    vendible, { vm, item ->
                        if(modelType==Constants.MODELTYPE.BRAND)
                        (vm as BrandStockViewModel).deleteBrand(item as BrandProductModel)
                        else
                            (vm as BrandStockViewModel).deleteProduct(item as BrandProductModel)
                    })

            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
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
                exportCSV()
                return true
            }
            R.id.menu_import->{
                importCSV()
                return  true
            }
            R.id.menu_export_database->{
                shareDatabaseBackup()
                return true
            }
            R.id.menu_import_database->{
                importZipFile()
                return true
            }
        }
        return super.onOptionsItemSelected(item) // important line
    }

    private fun shareDatabaseBackup() {
        loading()
        DatabaseBackupHelper.shareDatabaseBackup(requireContext()) {
            loaded()
        }
    }
    private fun importZipFile() {
        loading()
        DatabaseBackupHelper.importZipFile(requireContext(), resultLauncherNew)
    }

    private val resultLauncherNew = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        DatabaseBackupHelper.handleZipImportResult(requireContext(), uri) {
            loaded()
        }
    }

    fun loading(){
        binding.rvBrandStock.visibility = View.GONE
        binding.linear1?.visibility=View.GONE
        binding.btnAddNewBrand.visibility = View.GONE

        binding.progressBar.visibility=View.VISIBLE
    }
    fun loaded(){
        binding.rvBrandStock.visibility = View.VISIBLE
        binding.linear1?.visibility=View.VISIBLE
        binding.btnAddNewBrand.visibility = View.VISIBLE

        binding.progressBar.visibility=View.GONE
    }
    private fun importCSV() {
        csvHandler.importCSVStock(resultLauncher)
    }

    private fun exportCSV() {
        csvHandler.exportStockCSV("Stock_Export") { file ->
            viewModel.writeCSV(file)  // Example: Using viewModel2
        }
    }
    override fun onPause() {
        super.onPause()
        ToolbarUtil.hideToolbarButtons(requireActivity())
    }
    fun handleBackPress(): Boolean {
        val layoutOneViews = listOf(
            binding.spinnerM,
            binding.rvBrandStock,
        )
        viewModel.getBrandIdByName(null)
        if (layoutOneViews[0].visibility == View.GONE) {
            layoutOneViews.forEach { it.visibility = View.VISIBLE }
            binding.rvProductStock.visibility = View.GONE
            binding.txtBrand?.visibility=View.GONE
            binding.btnEditEcNew.visibility=View.VISIBLE
            binding.rvCat.visibility=View.GONE
            return true
        }
        return false // Not handled, let activity navigate back
    }
}
