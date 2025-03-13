package com.example.app21try6.bookkeeping.vendiblelist

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.Summary
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

/*
 val database: SummaryDbDao,
    val database1: CategoryDao,
    val database2: ProductDao,
    val database3: BrandDao,
    val database4: SubProductDao,
 */
class VendibleViewModel(
    private val bookRepo: BookkeepingRepository,
    private val stockRepo: StockRepositories,
    val date:Array<String>,
    application: Application):AndroidViewModel(application){

    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    private val months = arrayOf("all","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")

    private var checkedItemList = mutableListOf<Product>()
    private val _navigateToEditDetail = MutableLiveData<Boolean>()
    val navigateToEditDetail: LiveData<Boolean>
        get() = _navigateToEditDetail
    private val _navigateToSub = MutableLiveData< Array<String>>()
    val navigateToSub: LiveData<Array<String>>
        get() = _navigateToSub

    private val _addItem = MutableLiveData<Boolean>()
    val addItem:LiveData<Boolean>
        get() = _addItem
    val cathList = stockRepo.getCategoryModelLiveData()
    val cathList_ = stockRepo.getCategoryNameLiveData()
    var _itemCathPosition = MutableLiveData<Int>(0)
    val itemCathPosition :LiveData<Int>
        get() = _itemCathPosition

    val all_item_from_db get() = itemCathPosition.value.let {position->
        val cath = cathList.value
        val selectedCath = position?.let { cath?.get(it) }
        stockRepo.getProductByCategoryId(selectedCath?.id ?: 0)
    }
    fun onCheckBoxClicked(product: Product, bool:Boolean){
        if(bool){
        checkedItemList.add(product)}
    else{
        checkedItemList.remove(product) }
    }
    fun onAddClicked(){
        uiScope.launch {
            val inFormat = SimpleDateFormat("dd-MM-yyyy")
            val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(date[2].toString()+"-"+months.indexOf(date[1]).toString()+"-"+date[0].toString())) as String
                for(v in checkedItemList){
                        val summary = Summary()
                        summary.year= date[0].toInt()
                        summary.month = date[1]
                        summary.month_number = months.indexOf(date[1])
                        summary.day = date[2].toInt()
                        summary.day_name = day
                        summary.item_name = v.product_name
                        summary.price = v.product_price.toDouble()
                       bookRepo.insertItemToSummary(summary)
            }
            checkedItemList.clear()
            _navigateToEditDetail.value = true
        }
    }
    fun insertVendible(cath:String,brand:String,product: Product){
        uiScope.launch {
            try {
                stockRepo.insertIfNotExist(brand,cath)
                stockRepo.insertTry(product,brand,cath)
            } catch (e: SQLiteException) {
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
                Log.i("tag_1", "message $e")
            }
        }
    }

    fun deleteItemVendible(item_id:Int){ uiScope.launch { stockRepo.deleteProduct(item_id)} }
    fun updateVendible(product: Product){ uiScope.launch { stockRepo.updateProduct(product) } }
    //////////////////////////////////////////////SUSPEND///////////////////////////////////////////////




    ///////////////////////////////////NAVIGATION/////////////////////////////////////////////////////////
    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false }
    fun onNavigateToSub(date: Array<String>){_navigateToSub.value = date}
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedToSub(){
        this._navigateToSub.value = null}
    fun onNavigatedToEditThings(){ _navigateToEditDetail.value = false }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}