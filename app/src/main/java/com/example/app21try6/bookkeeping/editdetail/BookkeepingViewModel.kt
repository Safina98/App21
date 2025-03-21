package com.example.app21try6.bookkeeping.editdetail

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Summary
import com.example.app21try6.formatRupiah
import com.example.app21try6.getDateFromComponents
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/*
val database: SummaryDbDao,
                           val database2: ProductDao,
 */

class BookkeepingViewModel(
                            private val bookRepo:BookkeepingRepository,
                            private val stockRepo:StockRepositories,
                           application: Application,
                           ): AndroidViewModel(application) {
private val tagg="ProfitProbs"

    private val months = arrayOf("all","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    private val _date = MutableLiveData(arrayOf("0","","0"))
    val date: LiveData<Array<String>> = _date
    //val dayly_sells = database.getToday(_date.value!![0].toInt(), _date.value!![1], _date.value!![2].toInt())
    val daylySells: LiveData<List<Summary>> = date.switchMap { date ->
        bookRepo.getDailySells(date)
    }
    private val totalToday:LiveData<Double> =
        date.switchMap { date ->
            bookRepo.getDailyTotal(date)
        }
    val playerName: LiveData<String> = totalToday.map { formatRupiah(it).toString() }
    private val inFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(date.value!![2]+"-"+months.indexOf(date.value!![1]).toString()+"-"+date.value!![0])) as String
    val dayString :LiveData<String> =date.map{ date->
        day+date[2]+date[1]+date[0] ?: ""
   }
    var all_item_from_db = stockRepo.getAllProduct()
    private val _navigateToVendible = MutableLiveData<Boolean>()
    val navigateToVendible:LiveData<Boolean> get() = _navigateToVendible
    private val _addItem = MutableLiveData<Boolean>()
    val addItem:LiveData<Boolean> get() = _addItem
    ///////////////////////////////////Summary variables////////////////////////////////////////////
    private val _insertionCompleted = MutableLiveData<Boolean>()
    val insertionCompleted: LiveData<Boolean>
        get() = _insertionCompleted

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    //Variables
    val months_list = arrayOf("All","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    val year_list = arrayOf("2021","2022","2023","2024","2025","2026","2027","2028","2029","2030","2031")
    val year = Calendar.getInstance().get(Calendar.YEAR)
    //to create pdf

    val allItemFromSummary = bookRepo.getAllSummary()
    //selected spinner
    private val _selectedYear= MutableLiveData<Int>(year)
    val selectedYear:LiveData<Int>get() = _selectedYear
    private val _selectedMonth= MutableLiveData<String>("All")
    val selectedMonth :LiveData<String>get() = _selectedMonth
    //recyclerview daya
    private val _recyclerViewData = MutableLiveData<List<ListModel>>()
    val recyclerViewData: LiveData<List<ListModel>> get() = _recyclerViewData
    //navigation
    private val _navigateBookKeeping = MutableLiveData<Array<String>>()
    val navigateBookKeeping get() = _navigateBookKeeping


    //////////////////////////////////////BookKeeping funtions////////////////////////////////////////////////
    //Get Best Selling
    fun onAddClicked(){
        val list_item_to_add_new =all_item_from_db.value
        viewModelScope.launch {
            val summary = Summary()
            val inFormat  = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(_date.value!![2]+"-"+months.indexOf(_date.value!![1]).toString()+"-"+_date.value!![0])) as String
            if (list_item_to_add_new != null) {
                for(v in list_item_to_add_new){
                    if (v.bestSelling){
                        summary.year= _date.value!![0].toInt()
                        summary.month = _date.value!![1]
                        summary.month_number = months.indexOf(_date.value!![1])
                        summary.day = _date.value!![2].toInt()
                        summary.day_name = day
                        summary.item_name = v.product_name
                        summary.price = v.product_price.toDouble()
                        bookRepo.insertItemToSummary(summary)
                        }
                }
            }
        }
    }

    //fun clearProduct(){ viewModelScope.launch { clear()} }
    //delete today
    fun clearSummary(){
   viewModelScope.launch { bookRepo.clearToday(date.value!![0].toInt(), date.value!![1], date.value!![2].toInt())}
    }
    fun deleteItemSummary(item_name: Summary){
      viewModelScope.launch { bookRepo.deleteItemSummary(item_name.year,item_name.month, item_name.day,item_name.id_m)}
    }
    fun addBtnClicked(summary: Summary){
        viewModelScope.launch {
            summary.item_sold = summary.item_sold+1
            summary.total_income = summary.item_sold*summary.price
            bookRepo.update(summary)
        }
    }
    fun btnLongClick(summary: Summary, number:Double, code:Int){
        viewModelScope.launch {
            if (code == 1){
                summary.item_sold =summary.item_sold+number
            }else if(code ==2){
                summary.item_sold = summary.item_sold-number
            }else if(code ==3){
                summary.price = number
                summary.total_income = summary.price*summary.item_sold
            }
            summary.total_income = summary.item_sold*summary.price
            bookRepo.update(summary)
        }
    }
    fun subsBtnClicked(summary: Summary){
        viewModelScope.launch {
            summary.item_sold = summary.item_sold-1
            summary.total_income = summary.item_sold*summary.price
            bookRepo.update(summary)
        }
    }
    fun onAddItem(){
        _addItem.value = true
    }
    fun onItemAdded(){
        _addItem.value = false
    }
    fun onNavigateToVendible(){
        _navigateToVendible.value = true
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedToVendivle() {
        _navigateToVendible.value  = false
    }

    ////////////////////////////////////Summary Functions////////////////////////////////////
@RequiresApi(Build.VERSION_CODES.O)
fun onRvClick(listModel: ListModel){
        if (selectedMonth.value!="All"){
            val clickedDate = arrayOf(listModel.year_n.toString(),listModel.month_n,listModel.day_n.toString())
            onDayClick(clickedDate)
        }else{
            setSelectedMonth(listModel.month_n)
            setSelectedYear(listModel.year_n.toString())
        }
    }
    fun getSummaryWithNullProductId(){
        viewModelScope.launch {
            val list = bookRepo.getMothlyProfit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedYear(year:String){
        _selectedYear.value = year.toInt()
        updateRvNew()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedMonth(month:String){
        _selectedMonth.value = month
        updateRvNew()
    }
    fun monthToNumber(monthString: String?):Int{
        val monthNumbers = mapOf(
            "Januari" to 1,
            "Februari" to 2,
            "Maret" to 3,
            "April" to 4,
            "Mei" to 5,
            "Juni" to 6,
            "Juli" to 7,
            "Agustus" to 8,
            "September" to 9,
            "Oktober" to 10,
            "November" to 11,
            "Desember" to 12
        )
        val month = monthNumbers[monthString]
            ?: throw IllegalArgumentException("Invalid month name: $monthString")
        return month
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNumberOfDaysInMonth(year: Int, month: Int): Int {
        val yearMonth = YearMonth.of(year, month)
        return yearMonth.lengthOfMonth()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayName(year: Int, month: Int, day: Int): String {
        val date = LocalDate.of(year, month, day)
        val localeIndonesia = Locale("id", "ID") // Specify Indonesian locale
        return date.dayOfWeek.getDisplayName(TextStyle.FULL, localeIndonesia)
    }
    fun initialRv(){

        val initialList = mutableListOf<ListModel>()
        for(i in months_list){
            if(i!="All"){
                initialList.add(ListModel(0,i,0,0,i,"",0.0,0.0))
            }
        }
        _recyclerViewData.value = initialList

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRvNew() {
        viewModelScope.launch {
            val currentList = mutableListOf<ListModel>()
            if (selectedMonth.value == "All") {
                initialRv()
                val filteredData = bookRepo.getMonthlyData(_selectedYear.value?:year)
                updateListWithFilteredData(currentList, filteredData)
            } else {
                val filteredData = bookRepo.getDailyDataForSelectedMonth(_selectedYear.value?:year,selectedMonth.value!!)
                updateListForSelectedMonth(currentList, filteredData)
            }
            _recyclerViewData.value = currentList
        }
    }

    private suspend fun updateListWithFilteredData(currentList: MutableList<ListModel>, filteredData: List<ListModel>) {
        withContext(Dispatchers.IO){
            currentList.addAll(_recyclerViewData.value.orEmpty())
            val filteredMap = filteredData.associateBy { it.month_n }
            currentList.forEach { currentItem ->
                filteredMap[currentItem.month_n]?.let { filteredItem ->
                    currentItem.total = filteredItem.total
                    currentItem.year_n = filteredItem.year_n
                    currentItem.month_n = filteredItem.month_n
                    currentItem.nama = filteredItem.nama
                    currentItem.monthly_profit=filteredItem.monthly_profit-11700000.0
                }
            }

        }

    }

    private suspend fun updateListForSelectedMonth(currentList: MutableList<ListModel>, filteredData: List<ListModel>) {
        withContext(Dispatchers.IO){
            val monthNumber = monthToNumber(selectedMonth.value)
            val numberOfDays = getNumberOfDaysInMonth(selectedYear.value ?: year, monthNumber)
            for (day in 1..numberOfDays) {
                val dayName = getDayName(selectedYear.value!!, monthNumber, day)
                currentList.add(ListModel(selectedYear.value!!, selectedMonth.value ?: "", monthNumber, day, day.toString(), dayName, 0.0,0.0))
            }
            filteredData.forEach { filteredItem ->
                currentList.find { it.day_n == filteredItem.day_n }?.apply {
                    total = filteredItem.total
                    year_n = filteredItem.year_n
                    month_n = filteredItem.month_n
                    monthly_profit=filteredItem.monthly_profit-450000.0
                }
            }
        }

    }

    fun writeCSV(file: File) {
        try {
            val content = "Tahun,Bulan,Bulan, Tanggal, Hari,Nama Produk,Harga,Jumlah,Total,capital,profit"
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            for (j in allItemFromSummary.value!!){
                if (j.item_name!="empty") {
                    bw.newLine()
                    val sumLine = "${j.year},${j.month},${j.month_number},${j.day},${j.day_name},${j.item_name},${j.price},${j.item_sold},${j.total_income},${j.product_capital},${(j.price-j.product_capital)*j.item_sold}"
                    Log.i("new_", j.toString())
                    bw.write(sumLine)
                }
            }
            bw.close()
            Toast.makeText(getApplication(),"Success",Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    //fun onClear() { viewModelScope.launch {clear() } }
    //suspend fun clear() { withContext(Dispatchers.IO) { database.clear() } }
    fun onDayClick(id: Array<String>){
        viewModelScope.launch {
            _navigateBookKeeping.value = id
            _date.value = id
        }}

fun insertCSVBatch(tokensList: List<List<String>>) {
    viewModelScope.launch {
        try {
            _isLoading.value = true
            bookRepo.insertCSVBatch(tokensList)
            _insertionCompleted.value = true
        } catch (e: Exception) {
            Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
        }finally {
            _isLoading.value = false // Hide loading indicator
        }
    }
}

    @SuppressLint("NullSafeMutableLiveData")
    fun onDayNavigated() { _navigateBookKeeping.value  = null }


    companion object {

        @JvmStatic
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
               // val savedStateHandle = extras.createSavedStateHandle()

                val repository = StockRepositories(application)
                val sumRepo = BookkeepingRepository(application)
                return BookkeepingViewModel(
                    sumRepo,repository,application,
                ) as T
            }
        }
    }
}
