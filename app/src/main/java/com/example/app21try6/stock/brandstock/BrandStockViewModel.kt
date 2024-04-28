package com.example.app21try6.stock.brandstock

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.*

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class BrandStockViewModel(
        val database1:CategoryDao,
        val database2: BrandDao,
        val database3:ProductDao,
        val database4: SubProductDao,
        application: Application): AndroidViewModel(application){
    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    private val db: VendibleDatabase = VendibleDatabase.getInstance(application)

    //Navigation
    private val _navigateProduct = MutableLiveData<Array<String>>()
    val navigateProduct:LiveData<Array<String>> get() = _navigateProduct
    private val _addItem = MutableLiveData<Boolean>()
    val addItem: LiveData<Boolean> get() = _addItem
    private val _delCath = MutableLiveData<Boolean>()
    val delCath: LiveData<Boolean> get() = _delCath
    private val _addCath = MutableLiveData<Boolean>()
    val addCath: LiveData<Boolean> get() = _addCath
    //Display Data
    val cathList = database1.getAll()
    val cathList_ = database1.getName()
    var _itemCathPosition = MutableLiveData<Int>(0)
    val itemCathPosition :LiveData<Int> get() = _itemCathPosition

    var _all_brand_from_db = MutableLiveData<List<Brand>>()
    val all_brand_from_db :LiveData<List<Brand>> get()= _all_brand_from_db

    var kategori_id = MutableLiveData<Int>(-1)
/*
    val all_brand_from_db get() = itemCathPosition.value.let {position->
        val cath = cathList.value
        val selectedCath = position?.let { cath?.get(it) }
        if (selectedCath!=null){
            database2.getAll(selectedCath.category_id)}
        else{ database2.getAll(0) }
    }

 */
    private var checkedItemList = mutableListOf<Category>()
    val all_brand = database2.getAllBrand()
    val all_item = database2.getExportedData()
    val all_product = database3.getAllProduct()
    val all_sub = database4.getAllSub()
    private val _selectedKategoriSpinner = MutableLiveData<String>()
    val selectedKategoriSpinner: LiveData<String> get() = _selectedKategoriSpinner

    //Insert batch
    private val _insertionCompleted = MutableLiveData<Boolean>()
    val insertionCompleted: LiveData<Boolean>
        get() = _insertionCompleted

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    fun setSelectedKategoriValue(value: String) {
        viewModelScope.launch {
            var id = getKategoriIdByName(value)
            kategori_id.value = id
            _selectedKategoriSpinner.value = value
        }

    }
    private suspend fun getKategoriIdByName(id: String):Int{
        return withContext(Dispatchers.IO){
            database2.getKategoriIdByName(id)
        }

    }
    fun updateRv(){
        viewModelScope.launch {
            var brandlist = getBrandByKatId(kategori_id.value ?: 0)
            Log.i("BrandProb","updateRV "+brandlist)
            _all_brand_from_db.value = brandlist
        }
    }
    private suspend fun getBrandByKatId(id:Int):List<Brand>{
        return withContext(Dispatchers.IO){
            database2.getBrandByKatId(id)
        }
    }

    fun onCheckBoxClicked(category: Category,bool:Boolean){ if(bool){ checkedItemList.add(category)} else{ checkedItemList.remove(category) } }
    fun deleteDialog(){
        uiScope.launch {
            for(v in checkedItemList){
               deleteC(v.category_name)
            }
            clearCheckedItemList()
        }
    }
    private suspend fun deleteC(categoryName: String){ withContext(Dispatchers.IO){ database1.clear(categoryName) }}
    fun insertAnItemBrandStock(brand_name:String){
        uiScope.launch {
            if (brand_name!="") {
                val brand = Brand()
                brand.brand_name = brand_name
                brand.cath_code = getKategoriIdByName(selectedKategoriSpinner.value ?: "")
                Log.i("BrandProb","insertItemBrand "+brand_name)
                insert(brand)
            }
        }
    }
    fun insertItemCath(cath_name:String){
        uiScope.launch {
            if (cath_name!="") {
                val category = Category()
                category.category_name = cath_name
                try {
                    Log.i("BrandProb","insertItenCath "+category)
                    insertCath(category)
                } catch (e: SQLiteException) {
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
                    Log.i("tag_1", "message $e")
                }
            }
        }
    }





    fun writeCSV(file: File) {
        try {
            val content = "Kategori,Brand,Product, Price, BestSelling,SubProduct,Roll Utuh,Roll Besar TK,Roll Sedang TK,Roll Kecil TK,Roll Besar GDG,Roll Sedang GDG,Roll Kecil GDG"
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.newLine()
            for (j in all_item.value!!){
                //bw.newLine()
                var content = "${j.category},${j.brand},${j.product},${j.price},${j.bestSelling},${j.subProduct},${j.roll_u},${j.roll_b_t},${j.roll_s_t},${j.roll_k_t},${j.roll_b_g},${j.roll_s_g},${j.roll_k_g}"
                bw.write(content)
                bw.newLine()
            }
            bw.close()
            Toast.makeText(getApplication(),"Success",Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(getApplication(),"fAILED",Toast.LENGTH_SHORT).show()
        }
    }
    fun insertCSVO(token: List<String>){
        uiScope.launch {
            try {
                val product= Product()
                product.product_name = token[2].uppercase().trim()
                product.product_price = token[3].toInt()
                product.bestSelling = token[4]=="TRUE"
                val subProduct = SubProduct()
                subProduct.sub_name = token[5].uppercase().trim()
                subProduct.roll_u = token[6].toInt()
                subProduct.roll_bt = token[7].toInt()
                subProduct.roll_st = token[8].toInt()
                subProduct.roll_kt = token[9].toInt()
                subProduct.roll_bg = token[10].toInt()
                subProduct.roll_sg = token[11].toInt()
                subProduct.roll_kg = token[12].toInt()
                insertCathNew(token[0].uppercase().trim())
                insertBrandNew(token[1].uppercase().trim(),token[0].uppercase().trim())
                inserProductNew(product,token[1].uppercase().trim(),token[0].uppercase().trim())
                insertSubProductNew(subProduct,product.product_name,token[1].uppercase().trim(),token[0].uppercase().trim())
            } catch (e: SQLiteException) {
                Log.i("message_e", "message: $e")
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    fun insertCSV(token: List<String>){
        uiScope.launch {
            try {
                val product= Product()
                product.product_name = token[2].uppercase().trim()
                product.product_price = token[3].toInt()
                product.bestSelling = token[4]=="TRUE"
                val subProduct = SubProduct()
                subProduct.sub_name = token[5].uppercase().trim()
                subProduct.roll_u = token[6].toInt()
                subProduct.roll_bt = token[7].toInt()
                subProduct.roll_st = token[8].toInt()
                subProduct.roll_kt = token[9].toInt()
                subProduct.roll_bg = token[10].toInt()
                subProduct.roll_sg = token[11].toInt()
                subProduct.roll_kg = token[12].toInt()
                // Start transaction
                //startTransaction()
                insertCathNew(token[0].uppercase().trim())
                insertBrandNew(token[1].uppercase().trim(),token[0].uppercase().trim())
                inserProductNew(product,token[1].uppercase().trim(),token[0].uppercase().trim())
                insertSubProductNew(subProduct,product.product_name,token[1].uppercase().trim(),token[0].uppercase().trim())
                // Commit transaction
               //commitTransaction()
            } catch (e: SQLiteException) {
                // Rollback transaction in case of exception
                //rollbackTransaction()
                Log.i("message_e", "message: $e")
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

    fun insertCSVBatch(tokensList: List<List<String>>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                database1.performTransaction {
                    val batchSize = 100 // Define your batch size here
                    for (i in 0 until tokensList.size step batchSize) {
                        val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                        insertBatch(batch)
                    }
                }
                _insertionCompleted.value = true
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
            }finally {
                _isLoading.value = false // Hide loading indicator
            }
        }
    }

    private suspend fun insertBatch(batch: List<List<String>>) {
        batch.forEach { tokens ->
            insertCSVN(tokens)
        }
    }
    private suspend fun insertCSVN(token: List<String>) {
        val product= Product()
        product.product_name = token[2].uppercase().trim()
        product.product_price = token[3].toInt()
        product.bestSelling = token[4]=="TRUE"
        val subProduct = SubProduct()
        subProduct.sub_name = token[5].uppercase().trim()
        subProduct.roll_u = token[6].toInt()
        subProduct.roll_bt = token[7].toInt()
        subProduct.roll_st = token[8].toInt()
        subProduct.roll_kt = token[9].toInt()
        subProduct.roll_bg = token[10].toInt()
        subProduct.roll_sg = token[11].toInt()
        subProduct.roll_kg = token[12].toInt()
        var brand_name = token[1].uppercase().trim()
        var categoryName = token[0].uppercase().trim()
        database1.insertIfNotExist(categoryName)
        database2.insertIfNotExist(brand_name,categoryName)
        database3.insertIfNotExist(product.product_name,product.product_price,product.bestSelling,brand_name,categoryName)
        database4.insertIfNotExist(subProduct.sub_name,subProduct.warna,subProduct.ket,subProduct.roll_u,subProduct.roll_bt,subProduct.roll_st,subProduct.roll_kt,subProduct.roll_bg,subProduct.roll_sg,subProduct.roll_kg,product.product_name,brand_name,categoryName)

    }
    // Function to start a transaction



    private suspend fun insertCath(category: Category){ withContext(Dispatchers.IO){ database1.insert(category) } }
    private suspend fun insertCathNew(categoryName: String){ withContext(Dispatchers.IO) { database1.insertIfNotExist(categoryName) }}
    private suspend fun insertBrandNew(brand_name: String,categoryName: String){ withContext(Dispatchers.IO) { database2.insertIfNotExist(brand_name, categoryName) }}
    private suspend fun inserProductNew(product: Product,brand_name: String,categoryName: String){ withContext(Dispatchers.IO){ database3.insertIfNotExist(product.product_name,product.product_price,product.bestSelling,brand_name,categoryName)}}
    private suspend fun insertSubProductNew(subProduct: SubProduct,product_name:String,brand_name: String,categoryName: String){
        withContext(Dispatchers.IO){
            database4.insertIfNotExist(subProduct.sub_name,subProduct.warna,subProduct.ket,subProduct.roll_u,subProduct.roll_bt,subProduct.roll_st,subProduct.roll_kt,subProduct.roll_bg,subProduct.roll_sg,subProduct.roll_kg,product_name,brand_name,categoryName)
        } }
    fun updateCath(category: Category){ uiScope.launch { updateCath_(category) } }
    private suspend fun updateCath_(category: Category){withContext(Dispatchers.IO){ database1.update(category) } }
    fun getSelectedCategory():Category{ return checkedItemList.get(0) }
    private suspend fun insert(brand: Brand){ withContext(Dispatchers.IO){ database2.insert(brand) } }
    fun updateBrand(brand: Brand){ uiScope.launch { update(brand) } }
    private suspend fun update(brand:Brand){ withContext(Dispatchers.IO){ database2.update(brand) } }
    fun deleteBrand(brand: Brand){ uiScope.launch { delete(brand) } }
    private suspend fun delete(brand: Brand){ withContext(Dispatchers.IO){ database2.deleteBrand(brand.brand_id) } }
    fun clearCheckedItemList(){checkedItemList.clear()}
    fun onDeleteCathClick(){ _delCath.value = true }
    fun onDeleteCathCliked(){_delCath.value = false}
    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false }
    fun onAddCath(){ _addCath.value = true }
    fun onCathAdded(){ _addCath.value = false }
    fun onLongClick(v: View): Boolean { return true }
    fun onBrandCLick(id:Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }




}