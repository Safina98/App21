package com.example.app21try6.bookkeeping.summary

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteException
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.Summary
import com.example.app21try6.database.SummaryDbDao
import kotlinx.coroutines.*
import java.io.*
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*


class SummaryViewModel (val database: SummaryDbDao, application: Application):AndroidViewModel(application){


    //devine an instance JOB
    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    //pdf
    var pageHeight = 842
    var pagewidth = 595

    // creating a bitmap variable
    // constant code for runtime permissions
    private val PERMISSION_REQUEST_CODE = 200

    //Variables
    val months_list = arrayOf("All","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    val year_list = arrayOf("2021","2022","2023","2024","2025","2026","2027","2028","2029","2030","2031")
    val year = Calendar.getInstance().get(Calendar.YEAR)
    var _itemPosition = MutableLiveData<Int>(year_list.indexOf(year.toString()))
    var _itemMonthPosition = MutableLiveData<Int>()

    //variable thisMonth to hold the current month
    private var this_month = MutableLiveData<Summary?>()

    //variable all Month  to hold value of getAllMonth from the data base
    //var _months = database.getAllMonth(2021)
    val __months get() = _itemPosition.value.let {year_id->
        database.getAllMonthNew(year_list.get(year_id!!).toInt())
    }
   val __days get() = _itemPosition.value?.let {year_id->
        _itemMonthPosition.value?.let {month_id->
            if (month_id==0){
                database.getAllMonthNew(year_list.get(year_id).toInt())
            }else{
                database.getAllDayNew(year_list.get(year_id).toInt(), months_list.get(month_id))
            }
        }
    }
    val allItemFromSummary = database.getAllSummary()

    private val _selectedYear= MutableLiveData<Int>(year)
    val selectedYear
        get() = _selectedYear
    private val _selectedMonth= MutableLiveData<String>("All")
    val selectedMonth
        get() = _selectedMonth
    private val _recyclerViewData = MutableLiveData<List<ListModel>>()
    val recyclerViewData: LiveData<List<ListModel>>
        get() = _recyclerViewData

    private val _navigateBookKeeping = MutableLiveData<Array<String>>()
    val navigateBookKeeping
        get() = _navigateBookKeeping

    private val _is_month = MutableLiveData<Boolean>(true)
    val is_month
        get() = _is_month

    @RequiresApi(Build.VERSION_CODES.O)
    fun onStartTracking() {
        uiScope.launch {
            val summary = Summary()
            val inFormat = SimpleDateFormat("dd-MM-yyyy")
            for (year in year_list){
            for(m in 1..12){
                val yearMonthObject = YearMonth.of(year.toInt(),m)
                for(d in 1..yearMonthObject.lengthOfMonth() ){
                    val input = d.toString()+"-"+m.toString()+"-"+year.toString()
                    val date: Date = inFormat.parse(input)
                    summary.year= year.toInt()
                    summary.month = months_list.get(m)
                    summary.month_number = m
                    summary.day = d
                    summary.day_name = android.text.format.DateFormat.format("EEEE", date) as String
                    insert(summary)
                }
            }
                Toast.makeText(getApplication(),year,Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(getApplication(),"Finnised",Toast.LENGTH_SHORT).show()
        }
    }
    fun onRvClick(listModel: ListModel){
        if (selectedMonth.value!="All"){
            var clickedDate = arrayOf(listModel.year_n.toString(),listModel.month_n,listModel.day_n.toString())
            Log.i("BOOKEEPING",clickedDate.toString())
            onDayClick(clickedDate)
        }else{
            setSelectedMonth(listModel.month_n)
            setSelectedYear(listModel.year_n.toString())}
    }
    fun setSelectedYear(year:String){
        _selectedYear.value = year.toInt()
    }
    fun setSelectedMonth(month:String){
        selectedMonth.value = month
    }
    fun monthToNumber(monthString: String):Int{
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
        val monthNumber = monthToNumber(selectedMonth.value!!)
        val numberOfDays = getNumberOfDaysInMonth(selectedYear.value ?: year, monthNumber)
        for (day in 1..numberOfDays) {
            currentList.add(ListModel(selectedYear.value!!, selectedMonth.value ?: "", monthNumber, day, day.toString(), "", 0.0))
        }
        filteredData.forEach { filteredItem ->
            currentList.find { it.day_n == filteredItem.day_n }?.apply {
                total = filteredItem.total
                nama = filteredItem.nama
                day_n = filteredItem.day_n
                year_n = filteredItem.year_n
                month_n = filteredItem.month_n
            }
        }
    }
    fun insertCSV(token: List<String>){
        uiScope.launch {
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
                insert(summary)
            } catch (e: SQLiteException) {
                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show()
                //Log.i("tag_1", "message ${e.toString()}")
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
    private  suspend fun insert(summary: Summary){ withContext(Dispatchers.IO) { database.insert(summary) } }
    fun onClear() { uiScope.launch {clear() } }
    suspend fun clear() { withContext(Dispatchers.IO) { database.clear() } }
    fun onDayClick(id: Array<String>){ _navigateBookKeeping.value = id }
    @SuppressLint("NullSafeMutableLiveData") fun onDayNavigated() { _navigateBookKeeping.value  = null }
    fun onMontSelected(){ _is_month.value = false }
    fun onMonthDoneSelected(){ _is_month.value =true }

    fun generatePDF(file: File){
        val listBulanan = allItemFromSummary.value
        var pdfDocument = PdfDocument()
        val title = Paint()
        val month_style =Paint()
        var pageNumber =1
        var mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
        var myPage = pdfDocument.startPage(mypageInfo)
        var canvas: Canvas = myPage.canvas
        title.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        title.textSize = 45F
        title.color = ContextCompat.getColor(getApplication(), R.color.black)
        month_style.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        month_style.textSize = 30F
        month_style.color = ContextCompat.getColor(getApplication(), R.color.black)
        month_style.textAlign = Paint.Align.CENTER
        val year = year_list.get(_itemPosition.value!!)
        canvas.drawText("LAPORAN KEUANGAN", 60F, 260F, title)
        canvas.drawText("TOKO 21", 200F, 330F, title)
        canvas.drawText("TAHUN $year", 160F, 390F, title)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.color = ContextCompat.getColor(getApplication(), R.color.black)
        title.textSize = 15F
        var this_month = ""
        var x = 50F
        var y =220F
        var totalIncome = mutableListOf<Double>()
        if (listBulanan != null) {
            for (v in listBulanan){
                if(v.year==year.toInt()) {
                        if (v.month != this_month) {
                            if (this_month!="") {
                                canvas.drawText("Total", 400F, y, title)
                                canvas.drawText(totalIncome.sum().toInt().toString(), 500F, y, title) }
                            totalIncome.clear()
                            y = 120F
                            this_month = v.month
                            pdfDocument.finishPage(myPage)
                            pageNumber += 1
                            mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
                            myPage = pdfDocument.startPage(mypageInfo)
                            canvas = myPage.canvas
                            canvas.drawText(this_month, 270F, y-60F, month_style)
                            canvas.drawText("Tanggal", 20F, y-20, title)
                            canvas.drawText("Barang", 120F, y-20, title)
                            canvas.drawText("Jumlah", 300F, y-20, title)
                            canvas.drawText("Harga Satuan", 400F, y-20, title)
                            canvas.drawText("Total", 500F, y-20, title)
                            title.textAlign = Paint.Align.LEFT
                    }
                            if (y>750F){
                               pdfDocument.finishPage(myPage)
                                pageNumber+=1
                                mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
                                myPage = pdfDocument.startPage(mypageInfo)
                                canvas = myPage.canvas
                                y = 120f
                            }
                    if(v.item_name!="empty") {
                        canvas.drawText(v.day_name.toString() + ", " + v.day.toString(), 20F, y, title)
                        canvas.drawText(v.item_name.toString(), 120F, y, title)
                        canvas.drawText(v.item_sold.toString(), 300F, y, title)
                        canvas.drawText(v.price.toInt().toString(), 400F, y, title)
                        canvas.drawText(v.total_income.toInt().toString(), 500F, y, title)
                        totalIncome.add(v.total_income)
                        y = y + 20F
                    }
                }
           }
            canvas.drawText("Total", 400F, y, title)
            canvas.drawText(totalIncome.sum().toInt().toString(), 500F, y, title)
        }else{Toast.makeText(getApplication(),"SUmary null", Toast.LENGTH_SHORT).show()}
        pdfDocument.finishPage(myPage)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this.getApplication(), "PDF file generated succesfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this.getApplication(), "Failed to create PDF", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        pdfDocument.close()


    }

    //cancel all coroutines
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

