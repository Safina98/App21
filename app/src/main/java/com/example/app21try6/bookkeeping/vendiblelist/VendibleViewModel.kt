package com.example.app21try6.bookkeeping.vendiblelist

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent.getActivity
import android.database.sqlite.SQLiteException
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.app21try6.database.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class VendibleViewModel(val database: SummaryDbDao,
                        val database1:CategoryDao,
                        val database2: ProductDao,
                        val database3: BrandDao,
                        val database4: SubProductDao,
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
    val cathList = database1.getAll()
    val cathList_ = database1.getName()
    var _itemCathPosition = MutableLiveData<Int>(0)
    val itemCathPosition :LiveData<Int>
        get() = _itemCathPosition
    val all_item_from_db_new = Transformations.switchMap(itemCathPosition){
        val cath = cathList.value
        val selectedCath = it?.let { cath?.get(it) }
        database2.getCategoriedProduct(selectedCath?.category_id ?: 0)
    }
    val all_item_from_db get() = itemCathPosition.value.let {position->
        val cath = cathList.value
        val selectedCath = position?.let { cath?.get(it) }
        database2.getCategoriedProduct(selectedCath?.category_id ?: 0)
        /*
        if (selectedCath!=null){
            database2.getCategoriedProduct(selectedCath.category_id)}
        else{
            database2.getCategoriedProduct(0)
        }
         */
    }
    fun onCheckBoxClicked(product: Product,bool:Boolean){
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
                        insertItemToSummary_(summary)
            }
            checkedItemList.clear()
            _navigateToEditDetail.value = true
        }
    }
    fun insertVendible(cath:String,brand:String,product:Product){
        uiScope.launch {
            try {
                insertCath(cath)
                insert(brand,cath)
                insertTry(product,brand,cath)
            } catch (e: SQLiteException) {
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
                Log.i("tag_1", "message ${e.toString()}")
            }
        }
    }

    fun deleteItemVendible(item_id:Int){ uiScope.launch { deleteItemVendible_(item_id)} }
    fun updateVendible(product: Product){ uiScope.launch { update(product) } }
    //////////////////////////////////////////////SUSPEND////////////////////////////////////////////////
    private suspend fun insertItemToSummary_(summary: Summary){ withContext(Dispatchers.IO) { database.insert(summary) } }
    private suspend fun insertCath(category: String){ withContext(Dispatchers.IO){  database1.insertIfNotExist(category) } }
    private suspend fun update(product: Product) { withContext(Dispatchers.IO){ database2.update(product) } }
    private suspend fun deleteItemVendible_(item_id:Int){ withContext(Dispatchers.IO){ database2.delete(item_id) } }
    private suspend fun insertTry(product: Product,brand: String,cath: String){ withContext(Dispatchers.IO){ database2.inserProduct(product.product_name,product.product_price,product.bestSelling,brand,cath) } }
    private suspend fun insert(brand: String,cath: String){ withContext(Dispatchers.IO){
        database3.insertIfNotExist(brand,cath) } }
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