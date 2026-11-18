package com.example.app21try6.stock.brandstock

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteException
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Brand

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
    val ctgList = repository.getCategoryModelLiveData()
    //spinner entries
    val ctgNameList = repository.getCategoryNameLiveData()
    val brandList = repository.getBrandNameListByCategoryName() // query from DB

    var _brandBpModelList = MutableLiveData<List<BrandProductModel>>()
    val brandBpModelList :LiveData<List<BrandProductModel>> get()= _brandBpModelList
    var ctgId = MutableLiveData<Long>(-1L)
    var selectedBrandBpModel = MutableLiveData<BrandProductModel?>()
    val all_item = repository.getExportedStockData()
    private val _selectedCtgSpinner = MutableLiveData<String>()
    val selectedCtgSpinner: LiveData<String> get() = _selectedCtgSpinner

    //Insert batch
    private val _insertionCompleted = MutableLiveData<Boolean>()
    val insertionCompleted: LiveData<Boolean>
        get() = _insertionCompleted

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val _productBpModelList=MutableLiveData<List<BrandProductModel>>()
    val productBpModelList :LiveData<List<BrandProductModel>> get() = _productBpModelList

    private val _addProduct = MutableLiveData<Boolean>()
    val addProduct: LiveData<Boolean> get() = _addProduct
    val discountList=discountRepository.getAllDicountName()
    val discountName=MutableLiveData<String?>("")
    val _productId=MutableLiveData<Long?>()

    val _navigateToUpsertProduct= MutableLiveData<Long?>()
    val navigateToUpsertProduct: LiveData<Long?> get() = _navigateToUpsertProduct




    /////////////////////////// Produk variables///////////////////////////////////////////////////////////

    private var _navigateBackToBrandStok=MutableLiveData<Boolean>()
    val navigateBackToBrandStok:LiveData<Boolean> get()=_navigateBackToBrandStok

    fun setSelectedKategoriValue(value: String) {
        viewModelScope.launch {
            val id = repository.getCategoryIdByName(value)

            ctgId.value = id

            _selectedCtgSpinner.value = value
        }
    }


    fun updateRv(){
        viewModelScope.launch {
            val brandlist = repository.getBrandByCategoryId(ctgId.value?.toLong() ?:0L)
            val allbrand = repository.getAllBrand()


            _brandBpModelList.value = brandlist
        }
    }

    //toggle selectedBrand value, null/brandModel
    fun getBrandIdByName(branModel:BrandProductModel?){
        viewModelScope.launch {
            if (selectedBrandBpModel.value?.id==branModel?.id){
                selectedBrandBpModel.value=null
            }else {selectedBrandBpModel.value=branModel}
            //Log.i("BRANDPROBS","${selectedBrand.value}")
        }
    }

    fun deleteCategory(ctgModel:StockCategoryModel){
        viewModelScope.launch {
            val category = repository.getCategoryById(ctgModel.id)
            category?.isDeleted=true
            if (category!=null)
                repository.deleteCategory(category.categoryCloudId)
        }
    }

   // private suspend fun deleteC(categoryName: String){ withContext(Dispatchers.IO){ database1.clear(categoryName) }}
    fun insertAnItemBrandStock(brand_name:String,categoryName: String){
        uiScope.launch {
            if (brand_name!="") {
                val brand = Brand()
                brand.brand_name = brand_name
                brand.cath_code = repository.getCategoryIdByName(categoryName)
                brand.brandCloudId= System.currentTimeMillis()
                repository.insertBrand(brand)
                updateRv()
            }
        }
    }
    fun assignCloudIdToAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            //repository.assignCloudIdToAllData()
        }
    }
    fun insertItemCtg(ctgName:String){
        uiScope.launch {
            if (ctgName!="") {
                val category = Category()
                category.category_name = ctgName
                category.categoryCloudId= System.currentTimeMillis().toLong()
                category.needsSyncs=1
                try {
                    repository.insertCategory(category)
                } catch (e: SQLiteException) {
                    Toast.makeText(getApplication(), "InsertItemCath", Toast.LENGTH_LONG).show()

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
                var content = "${j.category},${j.brand},${j.product},${j.price},${j.bestSelling},${j.subProduct},${j.roll_u},${j.capital},${j.defaultNet},${j.alternate_capital},${j.alternate_price}"
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
//todo delete later
fun updateSubTable(){
    viewModelScope.launch {
        //repository.updateSubForeignKeysFromProduct()
    }
}
    fun insertCSVBatch(tokensList: List<List<String>>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertCSVBatch(tokensList)
                _insertionCompleted.value = true
            } catch (e: Exception) {

                Toast.makeText(getApplication(), "InsertCsvBatch $e", Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCtg(ctgModel: StockCategoryModel){
        uiScope.launch {
        val category= Category()
            category.categoryCloudId=ctgModel.id
            category.category_name=ctgModel.categoryName
            category.needsSyncs=1
        repository.updateCategory(category)
        }
    }
    fun updateBrand(brandBpModel: BrandProductModel, ctgName:String){ uiScope.launch {
        val brand=Brand()
        brand.brand_name=brandBpModel.name
        brand.brandCloudId=brandBpModel.id?: System.currentTimeMillis()
        brand.cath_code=repository.getCategoryIdByName(ctgName)
        repository.updateBrand(brand)

        updateRv()
    }
    }

    fun deleteBrand(brandBpModel: BrandProductModel){ uiScope.launch {
        repository.deleteBrand(brandBpModel.id?:0L)
        updateRv()
    } }

    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false }
    fun onLongClick(v: View): Boolean { return true }
    fun onBrandCLick(id:Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }

    //////////////////////////Product/////////////////////

    fun onProductCLick(id: Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")

    fun updateProductRv(brandId:Long?){
        viewModelScope.launch {
            val list =if (brandId!=null) repository.getProductModel(brandId) else listOf()
            _productBpModelList.value=list
        }
    }

fun getLongClickedProduct(id:Long){
    _productId.value=id
}
    fun onCategorySelected(category: String) {
        viewModelScope.launch {

            val catId=repository.getCategoryIdByName(category)
            //val brands = repository.getBrandNameListByCategoryName(catId) // query from DB
            //_brandList.postValue(brands)
        }
    }


    fun onProductAdded(){ _addProduct.value = false}

    fun deleteProduct(product: BrandProductModel){
        uiScope.launch {
           repository.deleteProduct(product.id)
        updateProductRv(product.parentId)

        } }
    fun onNavigateBackToBrandStock(){
        _navigateBackToBrandStok.value=true
    }
    fun onNavigatedBackToBrandStock(){
        _navigateBackToBrandStok.value=false
    }
    fun onNavigateToUpsertProduct(id:Long){
        _navigateToUpsertProduct.value =id
    }
    fun onNavigatedToUpsertProduct(){
        _navigateToUpsertProduct.value =null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}