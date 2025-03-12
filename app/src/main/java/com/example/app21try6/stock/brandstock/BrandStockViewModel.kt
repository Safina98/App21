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
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/*
val database1: CategoryDao,
    val database2: BrandDao,
    val database3: ProductDao,
    val database4: SubProductDao,
    val discountDao: DiscountDao,
 */
class BrandStockViewModel(
    private val repository: StockRepositories,
    private val discountRepository:DiscountRepository,

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
    //category recyclerview data
    val cathList = repository.getCategoryModelLiveData()
    //spinner entries
    val cathList_ = repository.getCategoryNameLiveData()

    var _all_brand_from_db = MutableLiveData<List<BrandProductModel>>()
    val all_brand_from_db :LiveData<List<BrandProductModel>> get()= _all_brand_from_db

    var kategori_id = MutableLiveData<Int>(-1)
    var selectedBrand = MutableLiveData<BrandProductModel?>()

    private var checkedItemList = mutableListOf<Category>()
    //val all_brand = database2.getAllBrand()
    val all_item = repository.getExportedStockData()
    //val all_product = database3.getAllProduct()
    //val all_sub = database4.getAllSub()
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
    val allDiscountFromDB=discountRepository.getAllDicountName()
    val discountName=MutableLiveData<String?>("")
    val _product=MutableLiveData<Product?>()


    fun setSelectedKategoriValue(value: String) {
        viewModelScope.launch {
            var id = repository.getCategoryIdByName(value)
            kategori_id.value = id
            _selectedKategoriSpinner.value = value
        }

    }

    fun updateRv(){
        viewModelScope.launch {
            val brandlist = repository.getBrandByCategoryId(kategori_id.value ?:0)
            _all_brand_from_db.value = brandlist
        }
    }

    //toggle selectedBrand value, null/brandModel
    fun getBrandIdByName(branModel:BrandProductModel?){
        viewModelScope.launch {
            if (selectedBrand.value?.id==branModel?.id){
                selectedBrand.value=null
            }else {selectedBrand.value=branModel}
            //Log.i("BRANDPROBS","${selectedBrand.value}")
        }
    }

    fun deleteCategory(category:CategoryModel){
        viewModelScope.launch {
            repository.deleteCategory(category.id)
        }
    }



    fun onCheckBoxClicked(category: Category, bool:Boolean){ if(bool){ checkedItemList.add(category)} else{ checkedItemList.remove(category) } }
    /*
    fun deleteDialog(){
        uiScope.launch {
            for(v in checkedItemList){
               deleteC(v.category_name)
            }
            clearCheckedItemList()
        }
    }

     */
   // private suspend fun deleteC(categoryName: String){ withContext(Dispatchers.IO){ database1.clear(categoryName) }}
    fun insertAnItemBrandStock(brand_name:String){
        uiScope.launch {
            if (brand_name!="") {
                val brand = Brand()
                brand.brand_name = brand_name
                brand.cath_code = repository.getCategoryIdByName(selectedKategoriSpinner.value ?: "")
                repository.insertBrand(brand)
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
                    repository.insertCategory(category)
                } catch (e: SQLiteException) {
                    Toast.makeText(getApplication(), "InsertItemCath", Toast.LENGTH_LONG).show()
                    Log.e("CSV Import", "InsertItemCath $e")
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
                //bw.newLine()   0                  1        2           3           4                   5           6               7           8               9          10              11          12          13              14              15                      16
                var content = "${j.category},${j.brand},${j.product},${j.price},${j.bestSelling},${j.subProduct},${j.roll_u},${j.roll_b_t},${j.roll_s_t},${j.roll_k_t},${j.roll_b_g},${j.roll_s_g},${j.roll_k_g},${j.capital},${j.defaultNet},${j.alternate_capital},${j.alternate_price}"
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
                repository.insertCSVBatch(tokensList)
                _insertionCompleted.value = true
            } catch (e: Exception) {
                Log.e("CSV Import", "InsertCsvBatch $e")
                Toast.makeText(getApplication(), "InsertCsvBatch $e", Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCath(categoryModel: CategoryModel){
        uiScope.launch {
        val category= Category()
        category.category_id=categoryModel.id
        category.category_name=categoryModel.categoryName
        repository.updateCategory(category)
        }
    }
    fun updateBrand(brandPm: BrandProductModel){ uiScope.launch {
        val brand=Brand()
        brand.brand_name=brandPm.name
        brand.brand_id=brandPm.id
        brand.cath_code=brandPm.parentId!!
        repository.updateBrand(brand)
        updateRv()
    } }

    fun deleteBrand(brand: BrandProductModel){ uiScope.launch {
        repository.deleteBrand(brand.id)
        updateRv()
    } }

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
            val list =if (brandId!=null) repository.getProductModel(brandId) else listOf()
            _all_product_from_db.value=list
        }
    }
    fun getLongClickedProduct(id:Int){
        viewModelScope.launch {
            val product= repository.getProductById(id)
            _product.value=product
            getDiscNameById(product.discountId)
        }
    }
    fun updateProduct(product: Product, discName:String){
        uiScope.launch{
            product.discountId=discountRepository.getDiscountIdByName(discName)
            repository.updateProduct(product)
            _product.value=null
            updateProductRv(product.brand_code)
        }
    }
    fun insertAnItemProductStock(product_name:String,price:Int,capital:Int,capital2:Double,modusNet:Double,discName: String,purchasePrice:Int?,purcaseUnit:String?){
        uiScope.launch {
            if (product_name!="") {
                val product = Product()
                product.brand_code = selectedBrand.value!!.id
                product.product_name = product_name
                product.product_price = price
                product.cath_code = kategori_id.value?:0
                product.product_capital = price
                product.discountId=discountRepository.getDiscountIdByName(discName)
                product.product_capital=capital
                product.alternate_capital=capital2
                product.default_net=modusNet
                product.purchasePrice=purchasePrice
                product.puchaseUnit=purcaseUnit
                repository.insertProduct(product)
                updateProductRv(selectedBrand.value?.id)
            }
        }
    }
    fun getDiscNameById(id:Int?){
        viewModelScope.launch {
            val disc = discountRepository.getDiscountNameById(id)
            discountName.value=disc
        }
    }
    fun onProductAdded(){ _addProduct.value = false}

    fun deleteProduct(product: BrandProductModel){
        uiScope.launch {
           repository.deleteProduct(product.id)
            updateProductRv(product.parentId)

        } }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}