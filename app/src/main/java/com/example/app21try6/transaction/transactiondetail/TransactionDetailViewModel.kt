package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.ProductDao
import com.example.app21try6.database.SubProductDao
import com.example.app21try6.database.Summary
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class TransactionDetailViewModel (application: Application,
                                  private val datasource1: TransSumDao,
                                  private val datasource2: TransDetailDao,
                                  private val datasource3: SummaryDbDao,
                                  private val datasource4: ProductDao,
                                  private val datasource5: SubProductDao,
                                  var id:Int):AndroidViewModel(application){

    val transDetail = datasource2.selectATransDetail(id)
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit
    val trans_sum = datasource1.getTransSum(id)
    val trans_total_ = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> = Transformations.map(trans_total_) { formatRupiah(it).toString() }
    private val _sendReceipt = MutableLiveData<Boolean>()
    var bayar :LiveData<String> = Transformations.map(trans_sum) { formatRupiah(it.paid.toDouble()).toString() }
    //var bayar = formatRupiah(trans_sum.value?.paid?.toDouble())
    val sendReceipt:LiveData<Boolean> get() = _sendReceipt

    fun onNavigateToEdit(){
        _navigateToEdit.value = id

    }
    fun onNavigatedToEdit(){this._navigateToEdit.value = null}
    private val _booleanValue = MutableLiveData<Boolean>()

    fun updateBooleanValue() {
       viewModelScope.launch {
           var transSum = trans_sum.value
          transSum?.is_taken_ = transSum?.is_taken_?.not() ?: true
           transSum?.let { updataTransSumDB(it) }

       }
    }
    fun getYearFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun onBtnPembukuanClick(){
        viewModelScope.launch {
            val transSum = trans_sum.value
            if (transSum?.is_keeped==false){
            transSum.is_keeped = transSum.is_keeped.not() ?: true
            transSum.let { updataTransSumDB(it) }
               insertToSummary()
            }
        }
    }
    fun insertToSummary(){
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.time = trans_sum.value!!.trans_date
            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())

            transDetail.value?.forEach {it->
               val summary = Summary()
                Log.i("INSERTSUMMARYBUG","insertSummary summary ${summary}")
               summary.year= calendar.get(Calendar.YEAR)
               summary.month = dateFormat.format(trans_sum.value!!.trans_date)
               summary.month_number = calendar.get(Calendar.MONTH)+1
               summary.day = calendar.get(Calendar.DATE)
               summary.day_name = trans_sum.value!!.trans_date.toString()
               summary.item_name = getProductName(it.trans_item_name)
               summary.price = it.trans_price.toDouble()
               summary.item_sold = it.qty
               summary.total_income = it.total_price
               insertItemToSummaryDB(summary)
           }
        }
    }
    private suspend fun getProductName(subName:String):String{
        return withContext(Dispatchers.IO){
            datasource5.getProductName(subName)
        }
    }
    private suspend fun insertItemToSummaryDB(summary: Summary){
        withContext(Dispatchers.IO){
            datasource3.insertOrUpdate(summary)
        }
    }

    fun updateTransDetail(transdetail:TransactionDetail){
        viewModelScope.launch {
            updateTransDetailDB(transdetail)
            Log.i("BOOLPROB","viewModel update ${transdetail.is_prepared.toString()}")
        }
    }
    private suspend fun updateTransDetailDB(transdetail: TransactionDetail){
        withContext(Dispatchers.IO){
            datasource2.update(transdetail)
        }
    }

    private suspend fun updataTransSumDB(transSum:TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transSum)
        }
    }

    fun getPadding(value:String, position:String,constant:Int):Int
    {
        val stringLength = value.length
        return if(position=="Middle")  ((constant - stringLength) / 2) else (constant - value.length)

    }
    fun generateReceiptText(): String {
        val builder = StringBuilder()
        val items = transDetail.value
        val transsum = trans_sum.value
        // Store information
        val storeName = "Toko 21"
        val storeAddress = "Jl Sungai Limboto No 110, Makassar"
        val storePhone = "Phone: 081343713281"
        val paddingStoreName = "-".repeat(getPadding(storeName,"Middle",50))
        // Get current date
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Receipt header
        //builder.append("------------------ $storeName ------------------\n")
        builder.append("$paddingStoreName $storeName $paddingStoreName\n")
        builder.append("$storeAddress\n")
        builder.append("$storePhone\n")
        builder.append("Receipt:" +" ".repeat(getPadding("Receipt Receipt : Date : $currentDate","Right",50))+ "Date: $currentDate\n\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append("TOKO/BPK/IBU: ${transsum?.cust_name}\n")
        builder.append("Barang  Jumlah  Harga  Total\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        // Receipt items
        var x ="x"
        if (items != null) {
            for (item in items) {
                val itemTotalPrice = item.qty * item.trans_price
                //builder.append(String.format("%-24s%6.2f%14d%14.2f\n", item.trans_item_name, item.qty, item.trans_price, itemTotalPrice))
                builder.append(String.format("%-24s\n", item.trans_item_name))
                builder.append(String.format("%10.2f   %-4s %4s\n",  item.qty, x, if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                builder.append(String.format(" %44s\n", formatRupiah(itemTotalPrice)))
            }
        }
        // Receipt footer
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append(String.format("%-24s%14s\n", "Total:", formatRupiah(transsum?.total_trans)))
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("                Have a nice day!\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")

        return builder.toString()
    }
    fun generateReceiptTextNew(): String {
        val builder = StringBuilder()
        val items = transDetail.value
        val transsum = trans_sum.value
        val c = 30
        // Store information
        val storeName = "Toko 21"
        val storeAddress = "Jl Sungai Limboto No 110,\nMakassar"
        val storePhone = "Phone: 081343713281"
        val paddingStoreName = "-".repeat(getPadding(storeName,"Middle",c))
        // Get current date
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Receipt header
        //builder.append("------------------ $storeName ------------------\n")
        builder.append("$paddingStoreName $storeName $paddingStoreName\n")
        builder.append("$storeAddress\n")
        builder.append("$storePhone\n")
        builder.append("Date: $currentDate\n")
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append("TOKO/BPK/IBU: ${transsum?.cust_name}\n")
        builder.append("Barang  Jumlah  Harga  Total\n")
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        // Receipt items
        var x ="x"

        if (items != null) {
            for (item in items) {
                val itemTotalPrice = item.qty * item.trans_price
                //builder.append(String.format("%-24s%6.2f%14d%14.2f\n", item.trans_item_name, item.qty, item.trans_price, itemTotalPrice))
                builder.append(String.format("%-24s\n", item.trans_item_name))
                builder.append(String.format("%3.2f   %-3s %3s\n",  item.qty, x, if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                builder.append(String.format(" %27s\n", formatRupiah(itemTotalPrice)))
            }
        }
        // Receipt footer
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append(String.format("%-10s%19s\n", "Total:", formatRupiah(transsum?.total_trans)))
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("      Have a nice day!\n\n\n\n")

        return builder.toString()

    }


    // Calculate subtotal
    fun calculateSubtotal(items: List<TransactionDetail>?): Double {
        return items?.sumByDouble { it.qty * it.trans_price } ?: 0.0
    }

    // Calculate tax
    fun calculateTax(items: List<TransactionDetail>?): Double {
        val subtotal = calculateSubtotal(items)
        return subtotal * 0.07 // 7% tax rate
    }

    // Calculate total
    fun calculateTotal(items: List<TransactionDetail>?): Double {
        return calculateSubtotal(items) + calculateTax(items)
    }

    fun onKirimBtnClick(){
        _sendReceipt.value=true
    }
    fun onKirimBtnClicked(){
        _sendReceipt.value = false
    }

}