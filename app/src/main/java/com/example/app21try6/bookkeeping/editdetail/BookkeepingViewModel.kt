package com.example.app21try6.bookkeeping.editdetail

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.app21try6.database.*
import com.example.app21try6.formatRupiah
//import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookkeepingViewModel(val database: SummaryDbDao,
                           val database2:ProductDao,
                           application: Application,
                           val date:Array<String>): AndroidViewModel(application) {


    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    private val months = arrayOf("all","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    val dayly_sells = database.getToday(date[0].toInt(), date[1], date[2].toInt())
    val totalToday = database.getTotalToday(date[0].toInt(), date[1], date[2].toInt())

    val playerName: LiveData<String> =
            Transformations.map(totalToday) { formatRupiah(it).toString() }
    val inFormat = SimpleDateFormat("dd-MM-yyyy")
    val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(date[2].toString()+"-"+months.indexOf(date[1]).toString()+"-"+date[0].toString())) as String
    val day_string = day +", "+date[2]+" "+date[1]+", "+date[0]

    var all_item_from_db = database2.getAllProduct()

    private val _navigateToVendible = MutableLiveData<Boolean>()
    val navigateToVendible:LiveData<Boolean>
        get() = _navigateToVendible
    private val _addItem = MutableLiveData<Boolean>()
    val addItem:LiveData<Boolean>
        get() = _addItem

    //Get Best Selling
    fun onAddClicked(){
        val list_item_to_add_new =all_item_from_db.value
        uiScope.launch {
            //val inFormat = SimpleDateFormat("dd-MM-yyyy")
            //val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(date[2].toString()+"-"+months.indexOf(date[1]).toString()+"-"+date[0].toString())) as String
            val summary = Summary()
            if (list_item_to_add_new != null) {
                for(v in list_item_to_add_new){
                    if (v.bestSelling){
                        summary.year= date[0].toInt()
                        summary.month = date[1]
                        summary.month_number = months.indexOf(date[1])
                        summary.day = date[2].toInt()
                        summary.day_name = day
                        summary.item_name = v.product_name
                        summary.price = v.product_price.toDouble()
                        insertItemToSummary_(summary)
                        }
                }
            }
        }
    }
    private suspend fun insertItemToSummary_(summary: Summary){
        withContext(Dispatchers.IO) {
            database.insert(summary)
        }
    }

    fun checkVendible(){
        Toast.makeText(getApplication(),all_item_from_db.value.toString(),Toast.LENGTH_SHORT).show()
    }
    fun checkSummary(){
        //Toast.makeText(getApplication(),dayly_sells.value.toString(),Toast.LENGTH_SHORT).show()
        val inFormat = SimpleDateFormat("dd-MM-yyyy")
        val input = date[2].toString()+"-"+months.indexOf(date[1]).toString()+"-"+date[0].toString()
        val datee: Date = inFormat.parse(input)
        //Toast.makeText(getApplication(),date[0],Toast.LENGTH_SHORT).show()
        Toast.makeText(getApplication(),android.text.format.DateFormat.format("EEEE", datee) as String,Toast.LENGTH_SHORT).show()
    }

    fun insertProduct() {
        uiScope.launch {
        val Product = Product()
            /*
        for (v in item_list) {
            Product.brand = v.brand
            Product.product = v.product
            Product.price = v.price
            Product.bestSelling = v.bestSelling
            //Toast.makeText(getApplication(),v.product,Toast.LENGTH_SHORT).show()

           insert(Product)
        }*/
            Toast.makeText(getApplication(),"Insert Finnished",Toast.LENGTH_SHORT).show()
        }

    }
    private  suspend fun insert(Product: Product){
        withContext(Dispatchers.IO) {
            database2.insert(Product)
        }

    }
    fun clearProduct(){
        uiScope.launch {
      clear()}
    }
    suspend fun clear() {
        withContext(Dispatchers.IO) {
           //database2.deleteAll()
        }
    }
    fun clearSummary(){
        uiScope.launch {
        clearToday(date[0].toInt(), date[1], date[2].toInt())}
    }

    suspend fun clearToday(year:Int, month:String, day: Int) {
        withContext(Dispatchers.IO) {
            database.clearToday(year, month, day)
        }
    }
    fun deleteItemSummary(item_name: Int){
        uiScope.launch {
            deleteItemSummary_(date[0].toInt(), date[1], date[2].toInt(),item_name)}
    }

    suspend fun deleteItemSummary_(year:Int, month:String, day: Int, item_name: Int){
        withContext(Dispatchers.IO){
            database.deleteItemSummary(year,month,day,item_name)
        }
    }

    fun addBtnClicked(summary: Summary){
        uiScope.launch {
            summary.item_sold = summary.item_sold+1
            summary.total_income = summary.item_sold*summary.price
            update(summary)
        }
    }
    fun btnLongClick(summary: Summary,number:Double,code:Int){
        uiScope.launch {
            if (code == 1){
                summary.item_sold =summary.item_sold+number
            }else if(code ==2){
                summary.item_sold = summary.item_sold-number
            }else if(code ==3){
                summary.price = number
                summary.total_income = summary.price*summary.item_sold
            }
            summary.total_income = summary.item_sold*summary.price
            update(summary)
        }
    }
    fun onPriceClick(summary: Summary,price:Double,code: Int){
        uiScope.launch {
            summary.price = price
            summary.total_income = summary.price*summary.item_sold
            update(summary)
        }
    }
    suspend fun update(summary: Summary){
        withContext(Dispatchers.IO){
        database.update(summary)}
        //Toast.makeText(getApplication(),summary.item_sold.toString(),Toast.LENGTH_SHORT).show()
    }
    fun subsBtnClicked(summary: Summary){
        uiScope.launch {
            summary.item_sold = summary.item_sold-1
            summary.total_income = summary.item_sold*summary.price
            update(summary)
        }
    }
    fun onNavigateToVendible(){
        _navigateToVendible.value = true
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedToVendivle() {
        _navigateToVendible.value  = false
    }
    fun onAddItem(){
        _addItem.value = true
        Toast.makeText(getApplication(),addItem.value.toString(),Toast.LENGTH_SHORT).show()
    }
    fun onItemAdded(){
        _addItem.value = false
        Toast.makeText(getApplication(),addItem.value.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
