package com.example.app21try6.bookkeeping.editdetail

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.PDFGenerator
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.database.*
import com.example.app21try6.formatRupiah
import com.example.app21try6.getDateFromComponents
//import com.google.firebase.database.DatabaseReference
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
import kotlin.collections.ArrayList

class BookkeepingViewModel(val database: SummaryDbDao,
                           val database2:ProductDao,
                           application: Application,
                           val dare:Array<String>): AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val _insertionStatus = MutableLiveData<Boolean>()
    val insertionStatus: LiveData<Boolean> = _insertionStatus
    //ui scope for coroutines
    //private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    private val months = arrayOf("all","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    private val _date = MutableLiveData<Array<String>>(arrayOf("0","","0"))
    val date: LiveData<Array<String>> = _date
    //val dayly_sells = database.getToday(_date.value!![0].toInt(), _date.value!![1], _date.value!![2].toInt())
    val dayly_sells: LiveData<List<Summary>> = Transformations.switchMap(date) { date ->
        database.getToday(date[0].toInt(),date[1],date[2].toInt())
    }
    val totalToday:LiveData<Double> =
        Transformations.switchMap(date) { date ->
            database.getTotalToday(date[0].toInt(), date[1], date[2].toInt())
        }
    val playerName: LiveData<String> = Transformations.map(totalToday) { formatRupiah(it).toString() }
    val inFormat = SimpleDateFormat("dd-MM-yyyy")
    val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(date.value!![2].toString()+"-"+months.indexOf(date.value!![1]).toString()+"-"+date.value!![0].toString())) as String
    val day_string :LiveData<String> = Transformations.map(date){date->
        day+date[2]+date[1]+date[0]
   }
    var all_item_from_db = database2.getAllProduct()
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
    var _itemPosition = MutableLiveData<Int>(year_list.indexOf(year.toString()))
    val allItemFromSummary = database.getAllSummary()
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
            val inFormat = SimpleDateFormat("dd-MM-yyyy")
            val day = android.text.format.DateFormat.format("EEEE",inFormat.parse(_date.value!![2].toString()+"-"+months.indexOf(_date.value!![1]).toString()+"-"+_date.value!![0].toString())) as String
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
                        insertItemToSummary_(summary)
                        }
                }
            }
        }
    }

    fun clearProduct(){
        viewModelScope.launch { clear()} }
    //delete today
    fun clearSummary(){
   viewModelScope.launch { clearToday(date.value!![0].toInt(), date.value!![1], date.value!![2].toInt())}
    }
    fun deleteItemSummary(item_name: Summary){
      viewModelScope.launch { deleteItemSummary_(item_name.year,item_name.month, item_name.day,item_name.id_m)}
    }
    fun addBtnClicked(summary: Summary){
        viewModelScope.launch {
            summary.item_sold = summary.item_sold+1
            summary.total_income = summary.item_sold*summary.price
            update(summary)
        }
    }
    fun btnLongClick(summary: Summary,number:Double,code:Int){
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
            update(summary)
        }
    }
    fun subsBtnClicked(summary: Summary){
        viewModelScope.launch {
            summary.item_sold = summary.item_sold-1
            summary.total_income = summary.item_sold*summary.price
            update(summary)
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
    suspend fun update(summary: Summary){
        withContext(Dispatchers.IO){
            database.update(summary)}
    }
    suspend fun deleteItemSummary_(year:Int, month:String, day: Int, item_name: Int){
        withContext(Dispatchers.IO){
            database.deleteItemSummary(year,month,day,item_name)
        }
    }
    suspend fun clearToday(year:Int, month:String, day: Int) {
        withContext(Dispatchers.IO) {
            database.clearToday(year, month, day)
        }
    }
    suspend fun clear() {
        withContext(Dispatchers.IO) {
            //database2.deleteAll()
        }
    }
    private suspend fun insertItemToSummary_(summary: Summary){
        withContext(Dispatchers.IO) {
            database.insert(summary)
        }
    }
/*
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

 */
    ////////////////////////////////////Summary Functions////////////////////////////////////
    fun onRvClick(listModel: ListModel){
        if (selectedMonth.value!="All"){
            var clickedDate = arrayOf(listModel.year_n.toString(),listModel.month_n,listModel.day_n.toString())
            onDayClick(clickedDate)
        }else{
            setSelectedMonth(listModel.month_n)
            setSelectedYear(listModel.year_n.toString())
        }
    }
    fun setSelectedYear(year:String){
        _selectedYear.value = year.toInt()
    }
    fun setSelectedMonth(month:String){
        _selectedMonth.value = month
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
        return month ?: 0
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNumberOfDaysInMonth(year: Int, month: Int, locale: Locale = Locale("id")): Int {
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
        var initialList = mutableListOf<ListModel>()
        for(i in months_list){
            if(i!="All"){
                initialList.add(ListModel(0,i,0,0,i,"",0.0))
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
                val filteredData = getMonthlyData()
                updateListWithFilteredData(currentList, filteredData)
            } else {
                val filteredData = getDailyDataForSelectedMonth()
                updateListForSelectedMonth(currentList, filteredData)
            }
            _recyclerViewData.value = currentList
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getMonthlyData(): List<ListModel> {
        return withContext(Dispatchers.IO) {
            database.getMonthlyData(_selectedYear.value ?: year)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun getDailyDataForSelectedMonth(): List<ListModel> {
        return withContext(Dispatchers.IO) {
            database.getDailyData(_selectedYear.value ?: year, selectedMonth.value!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateListWithFilteredData(currentList: MutableList<ListModel>, filteredData: List<ListModel>) {
        currentList.addAll(_recyclerViewData.value.orEmpty())
        val filteredMap = filteredData.associateBy { it.month_n }
        currentList.forEach { currentItem ->
            filteredMap[currentItem.month_n]?.let { filteredItem ->
                currentItem.total = filteredItem.total
                currentItem.year_n = filteredItem.year_n
                currentItem.month_n = filteredItem.month_n
                currentItem.nama = filteredItem.nama
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateListForSelectedMonth(currentList: MutableList<ListModel>, filteredData: List<ListModel>) {
        val monthNumber = monthToNumber(selectedMonth.value)
        val numberOfDays = getNumberOfDaysInMonth(selectedYear.value ?: year, monthNumber)
        for (day in 1..numberOfDays) {
            val dayName = getDayName(selectedYear.value!!, monthNumber, day)
            currentList.add(ListModel(selectedYear.value!!, selectedMonth.value ?: "", monthNumber, day, day.toString(), dayName, 0.0))
        }
        filteredData.forEach { filteredItem ->
            currentList.find { it.day_n == filteredItem.day_n }?.apply {
                total = filteredItem.total
                year_n = filteredItem.year_n
                month_n = filteredItem.month_n
            }
        }
    }

    fun onClear() { viewModelScope.launch {clear() } }
    //suspend fun clear() { withContext(Dispatchers.IO) { database.clear() } }

    fun insertCSV(token: List<String>){
        viewModelScope.launch {
            try {

                val summary = Summary()
                summary.year = token[0].toInt()
                summary.month = token[1]
                summary.month_number = token[2].toInt()
                summary.day_name= token[4]
                summary.day = token[3].toInt()
                summary.item_name = token[5]
                summary.item_sold = token[7].toDouble()
                summary.price = token[6].toDouble()
                summary.total_income = token[8].toDouble()
                Log.i("Insert Csv", "viewModel try $summary")
                insert(summary)
            } catch (e: SQLiteException) {
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
                Log.i("Insert Csv", "viewModel catch $e")
            }
        }
    }
    fun writeCSV(file: File) {
        try {
            val content = "Tahun,Bulan,Bulan, Tanggal, Hari,Nama Produk,Harga,Jumlah,Total"
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            for (j in allItemFromSummary.value!!){
                if (j.item_name!="empty") {
                    bw.newLine()
                    var content = "${j.year},${j.month},${j.month_number},${j.day},${j.day_name},${j.item_name},${j.price},${j.item_sold},${j.total_income}"
                    Log.i("new_", j.toString())
                    bw.write(content)
                }
            }
            bw.close()
            Toast.makeText(getApplication(),"Success",Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show()
        }

    }
    private  suspend fun insert(summary: Summary){
        withContext(Dispatchers.IO) {
            database.insert(summary) }
    }
    //fun onClear() { viewModelScope.launch {clear() } }
    //suspend fun clear() { withContext(Dispatchers.IO) { database.clear() } }
    fun onDayClick(id: Array<String>){
        viewModelScope.launch {
            _navigateBookKeeping.value = id
            _date.value = id
        }}
/*
    fun insertCSVData(data: List<List<String>>) {
            viewModelScope.launch {
                try {
                    repository.batchInsertCSV(data)
                    _insertionStatus.value = true
                } catch (e: Exception) {
                    Log.e("Insert CSV", "Error inserting CSV data: $e")
                    _insertionStatus.value = false
                }
            }
        }

 */
fun insertCSVBatch(tokensList: List<List<String>>) {
    viewModelScope.launch {
        try {
            _isLoading.value = true
            database.performTransaction {
                val batchSize = 1000 // Define your batch size here
                for (i in 0 until tokensList.size step batchSize) {
                    val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                    insertBatch(batch)
                }
            }
            _insertionCompleted.value = true
        } catch (e: Exception) {
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
        val summary = Summary().apply {
            year = token[0].toInt()
            month = token[1]
            month_number = token[2].toInt()
            day_name= token[4]
            day = token[3].toInt()
            date = getDateFromComponents(year, month, month_number, day, day_name)
            item_name = token[5]
            item_sold = token[7].toDouble()
            price = token[6].toDouble()
            total_income = token[8].toDouble()
        }
        database.insert(summary)
    }



    @SuppressLint("NullSafeMutableLiveData")
    fun onDayNavigated() { _navigateBookKeeping.value  = null }

    fun generatePDF(file: File){
    }
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
                val savedStateHandle = extras.createSavedStateHandle()
                val dataSource = VendibleDatabase.getInstance(application).summaryDbDao
                val dataSource2 = VendibleDatabase.getInstance(application).productDao

                return BookkeepingViewModel(
                    dataSource,dataSource2,application, arrayOf("0","0","0")
                ) as T
            }
        }
    }
}
