package com.example.app21try6.stock.productstock

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.Product
import com.example.app21try6.database.ProductDao
//import com.example.app21try6.database.ProductDao
import kotlinx.coroutines.*

class ProductViewModel (
        private val database2: ProductDao,
        application: Application,
        val brand_id:Array<Int>
): AndroidViewModel(application){
    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    val all_product_from_db = database2.getAll(brand_id[0])
    private val _addItem = MutableLiveData<Boolean>()
    val addItem: LiveData<Boolean>
        get() = _addItem
    private val _navigateProduct = MutableLiveData<Array<String>>()
    val navigateProduct:LiveData<Array<String>>
        get() = _navigateProduct
    fun insertAnItemProductStock(product_name:String,price:Int){
        uiScope.launch {
            if (product_name!="") {
                val product = Product()
                product.brand_code = brand_id[0]
                product.product_name = product_name
                product.product_price = price
                product.cath_code = brand_id[1]
                product.product_capital = price
                insert(product)
            }
        }
    }
    fun updateProduct(product:Product){uiScope.launch{ update(product)}}
    private suspend fun update(product:Product){ withContext(Dispatchers.IO){ database2.update(product) } }
    fun deleteProduct(product:Product){ uiScope.launch { delete(product) } }
    private suspend fun delete(product:Product){ withContext(Dispatchers.IO){ database2.delete(product.product_id) } }
    private suspend fun insert(product:Product){ withContext(Dispatchers.IO){ database2.insert(product) } }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false}
    fun onLongClick(v: View): Boolean { return true}
    fun onBrandCLick(id: Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }
    //logic goes here
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}