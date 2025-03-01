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
import com.example.app21try6.MODELTYPE
import com.example.app21try6.R
import com.example.app21try6.ToolbarUtil
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.databinding.FragmentBrandStockBinding
import com.example.app21try6.stock.productstock.ProductStockFragmentDirections
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
    val requestcode = 1
    private val viewModel:BrandStockViewModel by viewModels()
    private var list= mutableListOf<String>()
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.i("Insert Csv", "result Launcher")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var isFirstLine = true
            Log.i("InsertCsv", "result Launcher if " + data?.data?.path.toString())
            val tokensList = mutableListOf<List<String>>()
            try {
                context?.contentResolver?.openInputStream(data!!.data!!)?.bufferedReader()
                    ?.forEachLine { line ->
                        if (!isFirstLine) {
                            val tokens: List<String> = line.split(",")
                            tokensList.add(tokens)
                        }
                        isFirstLine = false
                    }
                viewModel.insertCSVBatch(tokensList)
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Insert Csv", "Error reading CSV: $e")
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
        val dataSource5 = VendibleDatabase.getInstance(application).discountDao
        val viewModelFactory = BrandStockViewModelFactory(dataSource1,dataSource2,dataSource3,dataSource4,dataSource5,application)
        binding.lifecycleOwner =this
        val layoutOneViews = listOf(
            binding.spinnerM,
            binding.rvBrandStock,
        )
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
                viewModel.getBrandIdByName(null)
                if (layoutOneViews[0].visibility == View.GONE) {
                    layoutOneViews.forEach { it.visibility = View.VISIBLE }
                    binding.rvProductStock.visibility = View.GONE
                    binding.txtBrand.visibility = View.GONE
                    binding.btnEditEcNew.visibility = View.VISIBLE
                    binding.rvCat.visibility = View.GONE

                    return
                }
            }
        })



        /////////////////////////////////////Initalizing Adapter/////////////////////////////////
        //BRAND ADAPTER
        val adapter = BrandStockAdapter(
            BrandStockListener {
                viewModel.getBrandIdByName(it)


            },BrandStockLongListener {
                showDialogBox(viewModel,it,MODELTYPE.brand)
            },null)

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
                showDialogBox(viewModel,it,MODELTYPE.Product)
            },null)


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
        binding.txtBrand.setOnClickListener {
            if (layoutOneViews[0].visibility == View.GONE) {
                layoutOneViews.forEach { it.visibility = View.VISIBLE }
                binding.rvProductStock.visibility = View.GONE
                binding.txtBrand.visibility=View.GONE
                binding.btnEditEcNew.visibility=View.VISIBLE
            }
        }
        ///////////////////////////////Observers///////////////////////////////////////////////
        //write csv
        viewModel.all_brand.observe(viewLifecycleOwner, Observer {})
        viewModel.all_item.observe(viewLifecycleOwner, Observer {})
        viewModel.all_product.observe(viewLifecycleOwner, Observer {})
        viewModel.all_sub.observe(viewLifecycleOwner, Observer {})

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

        viewModel.selectedBrand.observe(viewLifecycleOwner){
            if (binding.linear1!=null){
                layoutOneViews.forEach { it.visibility = View.GONE }
                binding.btnEditEcNew.visibility=View.GONE
                binding.txtBrand.visibility=View.VISIBLE
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
                if(modelType==MODELTYPE.brand){
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
                        if(modelType==MODELTYPE.brand)
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
                exportStockCSV()
                return true
            }
            R.id.menu_import->{
                importCSVStock()
                return  true
            }
            R.id.menu_export_database->{
                shareDatabaseBackup(requireContext())
                return true
            }
            R.id.menu_import_database->{
                importZipFile()
                return true
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

    private fun shareDatabaseBackup(context: Context) {
        //viewModel.writingInProgress()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Perform background work
                val zipFile = withContext(Dispatchers.IO) {
                    zipDatabaseFiles(context, "vendible_table")
                }
                // Update UI with the result
                val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", zipFile)
                val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    type = "application/zip"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share database file"))

            } catch (e: Exception) {
                Log.e("ZipDB", "Error sharing database file: ${e.localizedMessage}", e)
            } finally {
                // Ensure UI is updated whether success or failure
                loaded()
            }
        }
        // Show loading UI
        loading()
    }
    fun zipDatabaseFiles(context: Context, databaseName: String): File {
        val dbPath = context.getDatabasePath(databaseName).absolutePath
        Log.i("ZipDB","zipFile ${dbPath}.db")
        val walPath = "$dbPath-wal"
        Log.i("ZipDB","walPath ${walPath}")
        val shmPath = "$dbPath-shm"
        Log.i("ZipDB","shmPath ${shmPath}")
        val zipFile = File(context.externalCacheDir, "database_backup.zip")
        Log.i("ZipDB","zipFile ${zipFile.absolutePath}")
        Log.i("ZipDB","zipFile ${zipFile.name}")
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            addFileToZip(zipOut, File(dbPath), "")
            addFileToZip(zipOut, File(walPath), "")
            addFileToZip(zipOut, File(shmPath), "")
        }
        Log.i("ZipDB","zipFile ${zipFile.absolutePath}")
        Log.i("ZipDB","zipFile ${zipFile.name}")
        // viewModel.writingDone()
        return zipFile
    }
    private fun addFileToZip(zipOut: ZipOutputStream, file: File, parentDir: String) {
        FileInputStream(file).use { fis ->
            val zipEntry = ZipEntry("$parentDir${file.name}")
            zipOut.putNextEntry(zipEntry)
            fis.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }

    private fun importZipFile() {
        loading()
        Log.i("ZipDB", "importZipFile started")
        val fileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/zip"
        }
        try {
            resultLauncherNew.launch(fileIntent.type)
        } catch (e: Exception) {
            loaded()
            Toast.makeText(context, "Error selecting file", Toast.LENGTH_SHORT).show()
        }
    }

    private val resultLauncherNew = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            Toast.makeText(context,"result launcher",Toast.LENGTH_SHORT).show()
            Log.i("ZipDB", "result Launcher new")
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val tempFile = readFileFromUri(requireContext(), uri)
                    Log.i("ZipDB", "file ${tempFile?.name}")
                    Log.i("ZipDB", "file path ${tempFile?.absolutePath}")
                    if (tempFile?.exists() == true) {
                        try {
                            extractZipFile(tempFile)
                        } catch (e: IOException) {
                            Log.e("ZipDB", "Error extracting zip file", e)
                            withContext(Dispatchers.Main) {
                                loaded()
                                Toast.makeText(requireContext(), "Error extracting file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            loaded()
                            Toast.makeText(requireContext(), "File does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // Ensure progress bar is hidden after processing
                withContext(Dispatchers.Main) {
                    loaded()
                }
            }
        } else {
            // User didn't pick any file and went back to the app
            Log.i("ZipDB", "No file selected")
            loaded() // Hide loading indicator
            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readFileFromUri(context: Context, uri: Uri): File? {
        return try {
            // Get the content resolver to open an input stream from the URI
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Create a temporary file to store the contents
                val tempFile = File.createTempFile("imported_db", ".zip", context.cacheDir)
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile
            }
        } catch (e: SecurityException) {

            Log.e("FileReadError", "SecurityException while reading file from URI: ${e.localizedMessage}", e)
            null
        } catch (e: IOException) {

            Log.e("FileReadError", "IOException while reading file from URI: ${e.localizedMessage}", e)
            null
        } catch (e: Exception) {

            Log.e("FileReadError", "Exception while reading file from URI: ${e.localizedMessage}", e)
            null
        }
    }
    private fun extractZipFile(zipFile: File) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val zipInputStream = ZipInputStream(FileInputStream(zipFile))
                var zipEntry: ZipEntry? = zipInputStream.nextEntry
                while (zipEntry != null) {
                    val outputFile = File(requireContext().getDatabasePath(zipEntry.name).parent, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        FileOutputStream(outputFile).use { outputStream ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while (zipInputStream.read(buffer).also { length = it } > 0) {
                                outputStream.write(buffer, 0, length)
                            }
                        }
                    }
                    zipEntry = zipInputStream.nextEntry
                }
                zipInputStream.closeEntry()
                zipInputStream.close()
                Log.i("ZipDB", "Files extracted successfully")
                withContext(Dispatchers.Main) {
                    loaded()
                    Toast.makeText(requireContext(), "Files extracted successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("ZipDB", "Error extracting zip file", e)
                withContext(Dispatchers.Main) {
                    loaded()
                    Toast.makeText(requireContext(), "Error extracting file", Toast.LENGTH_SHORT).show()
                }
            }
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
            binding.txtBrand.visibility=View.GONE
            binding.btnEditEcNew.visibility=View.VISIBLE
            binding.rvCat.visibility=View.GONE

            return true
        }
        return false // Not handled, let activity navigate back
    }
}
