package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.app21try6.Converter3.decimalToText
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.formatRupiah
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TransactionDetailViewModel (application: Application,
                                  private val datasource1: TransSumDao,
                                  private val datasource2: TransDetailDao
                                  , var id:Int):AndroidViewModel(application){

    val transDetail = datasource2.selectATransDetail(id)
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit
    val trans_sum = datasource1.getTransSum(id)
    val trans_total_ = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> = Transformations.map(trans_total_) { formatRupiah(it).toString() }
    private val _sendReceipt = MutableLiveData<Boolean>()
    var bayar = formatRupiah(trans_sum.value?.paid?.toDouble())
    val sendReceipt:LiveData<Boolean> get() = _sendReceipt

    fun onNavigateToEdit(){_navigateToEdit.value = id}
    fun onNavigatedToEdit(){this._navigateToEdit.value = null}

    init {

    }

/*

    fun generateReceiptText(): String {
        val items = transDetail.value
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        var summary= TransactionSummary(0,"-",0.0,0,currentDate,false,false)
        if (trans_sum.value!=null){
            summary = trans_sum.value!!
        }
        val builder = StringBuilder()
        var storeName = "TOKO  21"
        val paddingStoreName = " ".repeat(getPadding(storeName,"Middle"))
        builder.append(". $paddingStoreName$storeName$paddingStoreName\n\n")


        val datePaddingString = " ".repeat(getPadding(currentDate,"RIGHT"))
        builder.append(". ${datePaddingString}${currentDate}\n\n")
        builder.append("Receipt:\n\n")
        var totalPrice = 0.0
        if (items != null) {
            for (item in items) {
                val itemTotalPrice = item.qty * item.trans_price
                totalPrice += itemTotalPrice

                builder.append("${item.trans_item_name} x ${item.qty} : \$${item.trans_price} each = \$${itemTotalPrice}\n")
            }
        }
        builder.append("\nTotal Price: \$${totalPrice}")

        return builder.toString()
    }

 */

    fun getPadding(value:String, position:String):Int
    {
        val stringLength = value.length
        return if(position=="Middle")  ((50 - stringLength) / 2) else (50 - value.length)

    }
    fun generateReceiptText(): String {
        val builder = StringBuilder()
        val items = transDetail.value
        val transsum = trans_sum.value
        // Store information
        val storeName = "Toko 21"
        val storeAddress = "Jl Sungai Limboto No 110, Makassar"
        val storePhone = "Phone: 081343713281"
        val paddingStoreName = "-".repeat(getPadding(storeName,"Middle"))
        // Get current date
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Receipt header
        //builder.append("------------------ $storeName ------------------\n")
        builder.append("$paddingStoreName $storeName $paddingStoreName\n")
        builder.append("$storeAddress\n")
        builder.append("$storePhone\n")
        builder.append("Receipt:" +" ".repeat(getPadding("Receipt Receipt : Date : $currentDate","Right"))+ "Date: $currentDate\n\n")
        builder.append("-".repeat(getPadding("","Left"))+"\n")
        builder.append("TOKO/BPK/IBU: ${transsum?.cust_name}\n")
        builder.append("Barang  Jumlah  Harga  Total\n")
        builder.append("-".repeat(getPadding("","Left"))+"\n")
        // Receipt items
        var x ="x"
        var arp = "@"
        var rp = "Rp."
        if (items != null) {
            for (item in items) {
                val itemTotalPrice = item.qty * item.trans_price
                //builder.append(String.format("%-24s%6.2f%14d%14.2f\n", item.trans_item_name, item.qty, item.trans_price, itemTotalPrice))
                builder.append(String.format("%-24s\n", item.trans_item_name))
                builder.append(String.format("%10.2f   %-4s %4s\n",  item.qty, x, formatRupiah(item.trans_price.toDouble())))
                builder.append(String.format(" %44s\n", formatRupiah(itemTotalPrice)))
            }
        }
        // Receipt footer
        builder.append("-".repeat(getPadding("","Left"))+"\n")
        builder.append(String.format("%-24s%14s\n", "Total:", formatRupiah(transsum?.total_trans)))
        builder.append("-".repeat(getPadding("","Left"))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("                Have a nice day!\n")
        builder.append("-".repeat(getPadding("","Left"))+"\n")

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