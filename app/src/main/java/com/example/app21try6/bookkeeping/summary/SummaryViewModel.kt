package com.example.app21try6.bookkeeping.summary

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.Product
import com.example.app21try6.database.SubProduct
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
    val months_list = arrayOf("all","Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember")
    val year_list = arrayOf("2021","2022","2023","2024","2025","2026","2027","2028","2029","2030","2031")

    var _itemPosition = MutableLiveData<Int>(0)
    var _itemMonthPosition = MutableLiveData<Int>()

    //variable thisMonth to hold the current month
    private var this_month = MutableLiveData<Summary?>()

    //variable all Month  to hold value of getAllMonth from the data base
    //var _months = database.getAllMonth(2021)
    val __months get() = _itemPosition.value.let {year_id->
        database.getAllMonthNew(year_list?.get(year_id!!).toInt())
    }


   val __days get() = _itemPosition.value?.let {year_id->
        _itemMonthPosition.value?.let {month_id->
            if (month_id==0){
                database.getAllMonthNew(year_list?.get(year_id).toInt())

            }else{
                database.getAllDayNew(year_list?.get(year_id).toInt(),months_list?.get(month_id))
            }

        }
    }
    val allItemFromSummary = database.getAllSummary()

    private val _navigateBookKeeping = MutableLiveData<Array<String>>()
    val navigateBookKeeping
        get() = _navigateBookKeeping

    private val _is_month = MutableLiveData<Boolean>(true)
    val is_month
        get() = _is_month

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
                    summary.year= year!!.toInt()
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
    fun onDayClicked(id: Array<String>){ _navigateBookKeeping.value = id }
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

        title.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD))
        title.setTextSize(45F)
        title.setColor(ContextCompat.getColor(getApplication(), R.color.black))

        month_style.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD))
        month_style.setTextSize(30F)
        month_style.setColor(ContextCompat.getColor(getApplication(), R.color.black))
        month_style.setTextAlign(Paint.Align.CENTER)
        val year = year_list?.get(_itemPosition.value!!)
        canvas.drawText("LAPORAN KEUANGAN", 60F, 260F, title)
        canvas.drawText("TOKO 21", 200F, 330F, title)
        canvas.drawText("TAHUN $year", 160F, 390F, title)

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(getApplication(), R.color.black))
        title.setTextSize(15F)
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
                            pdfDocument.finishPage(myPage);
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
                            title.setTextAlign(Paint.Align.LEFT)
                    }
                            if (y>750F){
                               pdfDocument.finishPage(myPage);
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

