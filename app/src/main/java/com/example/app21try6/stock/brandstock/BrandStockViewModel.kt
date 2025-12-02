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
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Product

import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.UUID

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
    val _product=MutableLiveData<Product?>()

    /////////////////////////// Produk variables///////////////////////////////////////////////////////////
    var productName = MutableLiveData<String>()
    var productPice=MutableLiveData<Int>()
    var productCapital=MutableLiveData<Int>()
    var checkBoxBoolean=MutableLiveData<Boolean>()
    var bestSelling=MutableLiveData<Boolean>()
    var defaultNet=MutableLiveData<Double>()
    var alternatePrice=MutableLiveData<Double>()
    var branName=MutableLiveData<String>()
    var ctgName=MutableLiveData<String>()
    var discountId=MutableLiveData<Int?>(null)
    var purchasePrice=MutableLiveData<Int?>(null)
    var puchaseUnit=MutableLiveData<String?>()
    var alternateCapital=MutableLiveData<Double>()

    private var _navigateBackToBrandStok=MutableLiveData<Boolean>()
    val navigateBackToBrandStok:LiveData<Boolean> get()=_navigateBackToBrandStok

    fun setSelectedKategoriValue(value: String) {
        viewModelScope.launch {
            val id = repository.getCategoryIdByName(value)
            Log.i("brandList","categoryIdfromrepo: ${ctgId.value}")
            ctgId.value = id
            Log.i("brandList","ctgId: ${ctgId.value}")
            _selectedCtgSpinner.value = value
        }

    }
    fun updateRv(){
        viewModelScope.launch {

            val brandlist = repository.getBrandByCategoryId(ctgId.value?.toLong() ?:0L)
            val allbrand = repository.getAllBrand()
            Log.i("brandList","view mode update rv $allbrand")
            allbrand.forEach {
                Log.i("brandList","$it")
            }
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

    fun deleteCategory(category:StockCategoryModel){
        viewModelScope.launch {
            repository.deleteCategory(category.id)
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
            repository.assignCloudIdToAllData()
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
//todo delete later
fun updateSubTable(){
    viewModelScope.launch {
        repository.updateSubForeignKeysFromProduct()
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
        brand.brandCloudId=brandBpModel.brandId?: System.currentTimeMillis()
        brand.cath_code=repository.getCategoryIdByName(ctgName)
        repository.updateBrand(brand)
        Log.i("UpdateBrand","${brand.brand_name} ${brand.cath_code}")
        updateRv()
    } }

    fun deleteBrand(brandBpModel: BrandProductModel){ uiScope.launch {
        repository.deleteBrand(brandBpModel.brandId?:0L)
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
    fun getLongClickedProduct(id:Int){
        viewModelScope.launch {
            val product= repository.getProductById(id)
            _product.value=product
            productName.value=product.product_name
            productPice.value=product.product_price
            productCapital.value=product.product_capital
            checkBoxBoolean.value=product.checkBoxBoolean
            bestSelling.value=product.bestSelling
            defaultNet.value=product.default_net
            alternatePrice.value=product.alternate_price
            ctgName.value= repository.getCategoryNameById(product.cath_code)//_selectedKategoriSpinner.value ?: ""
            discountId=MutableLiveData<Int?>(null)
            purchasePrice.value=product.purchasePrice
            puchaseUnit.value=product.puchaseUnit
            alternateCapital=MutableLiveData<Double>()
            discountName.value=discountRepository.getDiscountNameById(product.discountId)
            branName.value= repository.getBrandNameyId(product.brand_code)//selectedBrand.value?.name?: ""
        }
    }
    private val _brandList = MutableLiveData<List<String>>()
    val brandList: LiveData<List<String>> get() = _brandList

    fun onCategorySelected(category: String) {
        viewModelScope.launch {
            Log.i("SelectedCategory","$category")
            val catId=repository.getCategoryIdByName(category)
            val brands = repository.getBrandNameListByCategoryName(catId) // query from DB
            brands.forEach {
                Log.i("SelectedCategory","$it")
            }
            _brandList.postValue(brands)
        }
    }
    fun onBtnSimpanClick(){
        viewModelScope.launch {
            val product=Product()
            product.product_name=(productName.value?:"").uppercase().trim()
            product.brand_code = selectedBrandBpModel.value!!.brandId?:0L
            product.product_price = productPice.value?:0
            product.cath_code = ctgId.value?.toLong()?:0L
            product.product_capital = productCapital.value?:0
            product.discountId=discountRepository.getDiscountIdByName((discountName.value?:"").uppercase().trim())
            product.alternate_capital=alternateCapital.value ?: 0.0
            product.default_net=defaultNet.value ?: 0.0
            product.purchasePrice=purchasePrice.value ?: 0
            product.puchaseUnit=(puchaseUnit.value)?.uppercase()?.trim()
            product.alternate_price=alternatePrice.value ?: 0.0
            product.cath_code=repository.getCategoryIdByName(ctgName.value ?: "")
            product.brand_code=repository.getBrandIdByName(branName.value?:"",product.cath_code)?:0
            if (product.brand_code!=0L && product.cath_code!=0L) {
                if (_product.value==null){
                     repository.insertProduct(product)
                }
                else{
                    product.product_id=_product.value!!.product_id
                    updateProduct(product,"")
                }
                updateProductRv(selectedBrandBpModel.value?.brandId)
                onNavigateBackToBrandStock()
            }else{
                Toast.makeText(getApplication(),"kategori atau brand tidak valid",Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun updateProduct(product: Product, discName:String){
        uiScope.launch{
           // product.discountId=discountRepository.getDiscountIdByName(discName)
            repository.updateProduct(product)
            _product.value=null
            //updateProductRv(product.brand_code)
        }
    }

    fun onProductAdded(){ _addProduct.value = false}

    fun deleteProduct(product: BrandProductModel){
        uiScope.launch {
           repository.deleteProduct(product.id)
        //todo uncomment
        //updateProductRv(product.parentId)

        } }
    fun onNavigateBackToBrandStock(){
        _navigateBackToBrandStok.value=true
    }
    fun onNavigatedBackToBrandStock(){
        _navigateBackToBrandStok.value=false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}