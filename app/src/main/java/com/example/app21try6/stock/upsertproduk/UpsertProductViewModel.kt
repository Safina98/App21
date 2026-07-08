package com.example.app21try6.stock.upsertproduk

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Product
import kotlinx.coroutines.launch

class UpsertProductViewModel(
    private val brandId: Long?,
    private val repository: StockRepositories,
    private val discountRepository:DiscountRepository,
    application: Application): AndroidViewModel(application) {


    val ctgNameList = repository.getCategoryNameLiveData()
    val brandList = repository.getBrandNameListByCategoryName() // query from DB
    val _product=MutableLiveData<Product?>()
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

    val discountList=discountRepository.getAllDicountName()
    val discountName=MutableLiveData<String?>("")

    private var _navigateBackToBrandStok=MutableLiveData<Long?>()
    val navigateBackToBrandStok:LiveData<Long?> get()=_navigateBackToBrandStok

    fun getLongClickedProduct(id:Long){
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
            //  ctgName.value= repository.getCategoryNameById(product.cath_code)//_selectedKategoriSpinner.value ?: ""
            discountId=MutableLiveData<Int?>(null)
            purchasePrice.value=product.purchasePrice
            puchaseUnit.value=product.puchaseUnit
            alternateCapital=MutableLiveData<Double>()
            discountName.value=discountRepository.getDiscountNameById(product.discountId)
            branName.value= repository.getBrandNameyId(product.brand_code)//selectedBrand.value?.name?: ""
        }
    }

    fun onBtnSimpanClick(){
        viewModelScope.launch {
            val product=Product()
            product.product_name=(productName.value?:"").uppercase().trim()
            product.brand_code = brandId?:0L
            product.product_price = productPice.value?:0
            // product.cath_code = ctgId.value?.toLong()?:0L
            product.product_capital = productCapital.value?:0
            product.discountId=discountRepository.getDiscountIdByName((discountName.value?:"").uppercase().trim())
            product.alternate_capital=alternateCapital.value ?: 0.0
            product.default_net=defaultNet.value ?: 0.0
            product.purchasePrice=purchasePrice.value ?: 0
            product.puchaseUnit=(puchaseUnit.value)?.uppercase()?.trim()
            product.alternate_price=alternatePrice.value ?: 0.0
            //product.cath_code=repository.getCategoryIdByName(ctgName.value ?: "")
            val ctgId=repository.getCategoryIdByName(ctgName.value ?: "")
            product.brand_code=repository.getBrandIdByName(branName.value?:"")?:0
            product.needsSyncs=1
            Log.i("Productprobs","Kategori ${ctgName.value} id $ctgId")
            Log.i("Productprobs","Brand ${branName.value} id ${product.brand_code}")
            if (product.brand_code!=0L ) {
                if (_product.value==null){
                    product.productCloudId=System.currentTimeMillis()
                    repository.insertProduct(product)
                }
                else{
                    product.productCloudId =_product.value!!.productCloudId
                    updateProduct(product,"")
                }
               // updateProductRv(selectedBrandBpModel.value?.id)
                onNavigateBackToBrandStock()
            }else{
                Toast.makeText(getApplication(),"kategori atau brand tidak valid",Toast.LENGTH_SHORT).show()
            }
            clearMutable()

        }
    }
    fun updateProduct(product: Product, discName:String){
        viewModelScope.launch{
            // product.discountId=discountRepository.getDiscountIdByName(discName)
            repository.updateProduct(product)
            _product.value=null
            //updateProductRv(product.brand_code)
        }
    }
    fun clearMutable(){
        productName.value=""
        //selectedBrandBpModel.value=null
        productPice.value=0
        productCapital.value=0
        discountName.value=""
        alternateCapital.value =0.0
        defaultNet.value = 0.0
        purchasePrice.value = 0
        puchaseUnit.value=null
        alternatePrice.value = 0.0
        ctgName.value = ""
        branName.value=""
        _product.value==null
    }

    fun onNavigateBackToBrandStock(){
        _navigateBackToBrandStok.value=brandId
    }
    fun onNavigatedBackToBrandStock(){
        _navigateBackToBrandStok.value=null
    }

}