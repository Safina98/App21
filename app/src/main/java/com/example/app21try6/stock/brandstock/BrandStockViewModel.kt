package com.example.app21try6.stock.brandstock

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class BrandStockViewModel(
    val database1: CategoryDao,
    val database2: BrandDao,
    val database3: ProductDao,
    val database4: SubProductDao,
    val discountDao: DiscountDao,
    application: Application): AndroidViewModel(application){
    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    //Navigation
    private val _navigateProduct = MutableLiveData<Array<String>>()
    val navigateProduct:LiveData<Array<String>> get() = _navigateProduct
    private val _addItem = MutableLiveData<Boolean>()
    val addItem: LiveData<Boolean> get() = _addItem

    //Display Data
    val cathList = database1.getCategoryModelList()
    val cathList_ = database1.getName()

    var _all_brand_from_db = MutableLiveData<List<BrandProductModel>>()
    val all_brand_from_db :LiveData<List<BrandProductModel>> get()= _all_brand_from_db

    var kategori_id = MutableLiveData<Int>(-1)
    var selectedBrand = MutableLiveData<Brand>()

    private var checkedItemList = mutableListOf<Category>()
    val all_brand = database2.getAllBrand()
    val all_item = database2.getExportedData()
    val all_product = database3.getAllProduct()
    val all_sub = database4.getAllSub()
    private val _selectedKategoriSpinner = MutableLiveData<String>()
    val selectedKategoriSpinner: LiveData<String> get() = _selectedKategoriSpinner

    var catId=0

    //Insert batch
    private val _insertionCompleted = MutableLiveData<Boolean>()
    val insertionCompleted: LiveData<Boolean>
        get() = _insertionCompleted

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val _all_product_from_db=MutableLiveData<List<BrandProductModel>>()
    val all_product_from_db :LiveData<List<BrandProductModel>> get() = _all_product_from_db

    private val _addProduct = MutableLiveData<Boolean>()
    val addProduct: LiveData<Boolean> get() = _addProduct
    val allDiscountFromDB=discountDao.getAllDiscountName()
    val discountName=MutableLiveData<String?>("")
    val _product=MutableLiveData<Product?>()


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
            _all_brand_from_db.value = brandlist
        }
    }
    fun getBrandIdByName(branModel:BrandProductModel){
        viewModelScope.launch {
            val brand=Brand()
            brand.brand_id=branModel.id
            brand.cath_code=branModel.parentId!!
            brand.brand_name=branModel.name
            selectedBrand.value=brand
        }
    }

    fun deleteCategory(category:CategoryModel){
        viewModelScope.launch {
            deleteCategoryToDao(category.id)
        }
    }
    private suspend fun deleteCategoryToDao(id:Int){
        withContext(Dispatchers.IO){
            database1.delete(id)
        }
    }
    private suspend fun getBrandByKatId(id:Int):List<BrandProductModel>{
        return withContext(Dispatchers.IO){
            database2.getBrandModelByCatId(id)
        }
    }

    fun onCheckBoxClicked(category: Category, bool:Boolean){ if(bool){ checkedItemList.add(category)} else{ checkedItemList.remove(category) } }
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
                insert(brand)
                updateRv()
            }
        }
    }
    fun insertItemCath(cath_name:String){
        uiScope.launch {
            if (cath_name!="") {
                val category = Category()
                category.category_name = cath_name
                try {
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
            val content = "Kategori,Brand,Product, Price, BestSelling,SubProduct,Roll Utuh,Roll Besar TK,Roll Sedang TK,Roll Kecil TK,Roll Besar GDG,Roll Sedang GDG,Roll Kecil GDG,Capital"
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.newLine()

            for (j in all_item.value!!){
                //bw.newLine()   0                  1        2           3           4                   5           6               7           8               9          10              11          12          13
                var content = "${j.category},${j.brand},${j.product},${j.price},${j.bestSelling},${j.subProduct},${j.roll_u},${j.roll_b_t},${j.roll_s_t},${j.roll_k_t},${j.roll_b_g},${j.roll_s_g},${j.roll_k_g},${j.capital}"
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
                Log.i("INSERTCSVPROB","exception: $e")
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
       // Log.i("INSERTCSVPROB","brand token: $token")
        val product= Product()
        product.product_name = token[2].uppercase().trim()
        product.product_price = token[3].toInt()
        product.bestSelling = token[4]=="TRUE"
        product.product_capital = token[13].toInt()
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
        database3.insertIfNotExist(product.product_name,product.product_price,product.product_capital,product.bestSelling,brand_name,categoryName)
        database4.insertIfNotExist(subProduct.sub_name,subProduct.warna,subProduct.ket,subProduct.roll_u,subProduct.roll_bt,subProduct.roll_st,subProduct.roll_kt,subProduct.roll_bg,subProduct.roll_sg,subProduct.roll_kg,product.product_name,brand_name,categoryName)

    }
    // Function to start a transaction
    private suspend fun insertCath(category: Category){ withContext(Dispatchers.IO){ database1.insert(category) } }

    fun updateCath(categoryModel: CategoryModel){
        uiScope.launch {
        val category= Category()
        category.category_id=categoryModel.id
        category.category_name=categoryModel.categoryName
        updateCath_(category)
        }
    }
    private suspend fun updateCath_(category: Category){withContext(Dispatchers.IO){ database1.update(category) } }

    private suspend fun insert(brand: Brand){ withContext(Dispatchers.IO){ database2.insert(brand) } }
    fun updateBrand(brandPm: BrandProductModel){ uiScope.launch {
        val brand=Brand()
        brand.brand_name=brandPm.name
        brand.brand_id=brandPm.id
        brand.cath_code=brandPm.parentId!!
        update(brand)
        updateRv()
    } }
    private suspend fun update(brand: Brand){ withContext(Dispatchers.IO){ database2.update(brand) } }
    fun deleteBrand(brand: BrandProductModel){ uiScope.launch {
        deleteBrandToDao(brand.id)
        updateRv()
    } }
    private suspend fun deleteBrandToDao(id:Int){ withContext(Dispatchers.IO){ database2.deleteBrand(id) } }
    fun clearCheckedItemList(){checkedItemList.clear()}

    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false }
    fun onLongClick(v: View): Boolean { return true }
    fun onBrandCLick(id:Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }

    //////////////////////////Product/////////////////////

    fun onProductCLick(id: Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")

    fun updateProductRv(brandId:Int?){
        viewModelScope.launch {
            var list = withContext(Dispatchers.IO){ database3.getAll(brandId)}
            _all_product_from_db.value=list
        }
    }
    fun getLongClickedProduct(id:Int){
        viewModelScope.launch {
            val product= withContext(Dispatchers.IO){database3.getProductById(id)}
            _product.value=product
            getDiscNameById(product.discountId)
        }
    }
    fun updateProduct(product: Product, discName:String){
        uiScope.launch{
            product.discountId=getDiscIdByName(discName)
            update(product)
            _product.value=null
            updateProductRv(product.brand_code)
        }
    }
    fun insertAnItemProductStock(product_name:String,price:Int,capital:Int,capital2:Double,modusNet:Double,discName: String,purchasePrice:Int?,purcaseUnit:String?){
        uiScope.launch {
            if (product_name!="") {
                val product = Product()
                product.brand_code = selectedBrand.value!!.brand_id
                product.product_name = product_name
                product.product_price = price
                product.cath_code = kategori_id.value?:0
                product.product_capital = price
                product.discountId=getDiscIdByName(discName)
                product.product_capital=capital
                product.alternate_capital=capital2
                product.default_net=modusNet
                product.purchasePrice=purchasePrice
                product.puchaseUnit=purcaseUnit
                insert(product)
                updateProductRv(selectedBrand.value?.brand_id)
            }
        }
    }
    fun getDiscNameById(id:Int?){
        viewModelScope.launch {
            val disc = withContext(Dispatchers.IO){discountDao.getDiscountNameById(id)}
            discountName.value=disc
        }
    }
    fun onProductAdded(){ _addProduct.value = false}
    private suspend fun update(product: Product){ withContext(Dispatchers.IO){ database3.update(product) } }
    fun deleteProduct(product: BrandProductModel){
        uiScope.launch {
            deleteProductToDao(product.id)
            updateProductRv(product.parentId)

        } }
    private suspend fun deleteProductToDao(id:Int){ withContext(Dispatchers.IO){ database3.delete(id) } }
    private suspend fun insert(product: Product){ withContext(Dispatchers.IO){ database3.insert(product) } }
    private suspend fun getDiscIdByName(discName:String):Int?{
        return withContext(Dispatchers.IO){
            discountDao.getDiscountIdByName(discName)
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}