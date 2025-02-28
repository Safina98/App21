package com.example.app21try6.utils

import android.util.Log
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.DISCTYPE
import com.example.app21try6.SIMPLE_DATE_FORMAT
import com.example.app21try6.SIMPLE_DATE_FORMATTER
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatRupiah
import com.example.app21try6.database.models.PaymentModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TextGenerator(
    var transDetail:List<TransactionDetail>?,
    var transSum: TransactionSummary?,
    var paymentModel: List<PaymentModel>?,
    var discountTransaction: List<PaymentModel>?
                    ) {
    //Make today amazing!
    //Rise, shine, repeat!
    //"The best is yet to come! 🌟"
    val message="Stay positive, shine on!"
    val emoji="\uD83C\uDF1F"
    val decimalFormat = DecimalFormat("#.##")
    fun getPadding(value:String, position:String,constant:Int):Int {
        val stringLength = value.length
        return if(position=="Middle")  ((constant - stringLength) / 2) else (constant - value.length)
    }
    fun generateReceiptTextWa(): String {
        val builder = StringBuilder()
        val items = transDetail
        val transsum = transSum
        val payments = paymentModel
        // Store information
        val storeName = "Toko 21"
        val storeAddress = "Jl Sungai Limboto No 110, Makassar"
        val storePhone = "Phone: 081343713281"
        val paddingStoreName = "-".repeat(getPadding(storeName,"Middle",50))
        // Get current date

        val currentDate = SIMPLE_DATE_FORMATTER.format(transsum?.trans_date)


        var totalTransaction= transsum!!.total_trans

        // Receipt header
        //builder.append("------------------ $storeName ------------------\n")
        builder.append("$paddingStoreName $storeName $paddingStoreName\n")
        builder.append("$storeAddress\n")
        builder.append("$storePhone\n")
        builder.append("Receipt: ${transsum.sum_id}" +" ".repeat(getPadding("Receipt Receipt :${transsum.sum_id} ${transsum.sum_id} Date : $currentDate","Right",50))+ "Date: $currentDate\n")
        builder.append("${transsum?.cust_name}\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        //builder.append("Barang  Jumlah  Harga  Total\n")
        //builder.append("-".repeat(getPadding("","Left",50))+"\n")

        decimalFormat.isDecimalSeparatorAlwaysShown = false
        // Receipt items
        val x ="x"
        if (items != null) {
            for (item in items) {
                val itemTotalPrice = item.qty * item.trans_price * item.unit_qty
                val unit_qty = if (item.unit_qty!=1.0) "(${item.unit_qty})" else "   "
                val price  = if(item.qty>=1) formatRupiah(item.trans_price.toDouble()) else "-"
                builder.append(String.format("%-24s\n", item.trans_item_name))
                if (item.unit ==null){
                    builder.append(String.format("%16.2f %7s %10s\n",  item.qty, x, price))
                }
                else{
                    if (item.unit_qty!=1.0)
                    {
                        builder.append(String.format("%2s %1s %1s %-4s %1s\n",  decimalFormat.format(item.qty),item.unit, unit_qty, x, price))
                    }else{
                        builder.append(String.format("%12s %1s  %3s %10s\n",  decimalFormat.format(item.qty),item.unit, x, price))
                    }
                }
                builder.append(String.format(" %44s\n", formatRupiah(itemTotalPrice)))
            }
        }
        // Receipt footer
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append(String.format("%-24s%14s\n", "Total:", formatRupiah(transsum.total_trans)))
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        if (!discountTransaction.isNullOrEmpty()){
            for (d in discountTransaction!!){
                if (d.discountType?.replace(" ", "") != DISCTYPE.CashbackNotPrinted.replace(" ", ""))  {
                   val a = getPadding("${d.name} ${d.payment_ammount}","left",50)-10
                    Log.i("DiscProbs","get padding $a")
                    builder.append(String.format("%-${a}s%4s\n", d.name, formatRupiah(d.payment_ammount?.toDouble())))
                    totalTransaction=totalTransaction-d.payment_ammount!!
                }
            }
            builder.append("-".repeat(getPadding("","Left",50))+"\n")
            builder.append(String.format("%-24s%14s\n", "Total:", formatRupiah(totalTransaction)))
        }

        if (payments!=null){
            var paymentAmmountSum :Int = 0
            for (p in payments){
                paymentAmmountSum +=p.payment_ammount?:0
                val sisa = totalTransaction - paymentAmmountSum
                builder.append(String.format("%-22s%14s\n", "Bayar :", formatRupiah(p.payment_ammount?.toDouble())))
                builder.append("-".repeat(getPadding("","Left",50))+"\n")
                if (totalTransaction>paymentAmmountSum) {
                    builder.append(String.format("%-24s%14s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-24s%14s\n", "Kembalian:", formatRupiah(abs(sisa))))
                }
            }
        }

        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("      $message\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")

        return builder.toString()
    }
    fun generateReceiptTextNew(): String {

        val builder = StringBuilder()
        val items = transDetail
        val payments = paymentModel
        val transsum = transSum
        val c = 30
        // Store information
        val storeName = "Toko 21"
        val storeAddress = "Jl Sungai Limboto No 110,\nMakassar"
        val storePhone = "Phone: 081343713281"
        val paddingStoreName = "-".repeat(getPadding(storeName,"Middle",c))
        // Get current date
        val currentDate = SIMPLE_DATE_FORMATTER.format(transsum?.trans_date)
        var totalTransaction= transsum!!.total_trans
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
                val itemTotalPrice = item.qty * item.trans_price*item.unit_qty
                builder.append(String.format("%-24s\n", item.trans_item_name))
                //builder.append(String.format("%3.2f   %-3s %3s\n",  item.qty, x, if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                if (item.unit ==null){
                    builder.append(String.format("%3.2f %10s %3s\n",  item.qty, x, if(item.qty>=1) formatRupiah(item.trans_price.toDouble()) else "-"))
                }
                else{
                    if (item.unit_qty!=1.0)
                    {
                        builder.append(String.format("%2s %1s %1s %-2s %1s\n",  decimalFormat.format(item.qty),item.unit,if (item.unit_qty!=1.0) "(${item.unit_qty})" else "   ", x,  if(item.qty>=1) formatRupiah(item.trans_price.toDouble()) else "-"))
                    }else{
                        builder.append(String.format("%12s %1s  %3s %10s\n",  decimalFormat.format(item.qty),item.unit, x,  if(item.qty>=1) formatRupiah(item.trans_price.toDouble()) else "-"))
                    }
                }
                builder.append(String.format(" %27s\n", formatRupiah(itemTotalPrice)))
            }
        }
        // Receipt footer
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append(String.format("%-10s%19s\n", "Total:", formatRupiah(transsum.total_trans)))
        if (!discountTransaction.isNullOrEmpty()){
            for (d in discountTransaction!!){
                if (d.discountType?.replace(" ", "") != DISCTYPE.CashbackNotPrinted.replace(" ", ""))  {
                    val a = getPadding("${d.name} ${d.payment_ammount}","left",c)+3
                    Log.i("DiscProbs","get padding ${c-a}")
                    val padding = " ".repeat(((c - d.name!!.length - formatRupiah(d.payment_ammount?.toDouble())!!.length)-1).coerceAtLeast(0))
                    val paddedString = "${d.name}$padding${formatRupiah(d.payment_ammount?.toDouble())}"

                    builder.append("$paddedString\n")
                    //builder.append(String.format("%-${b}s%4s\n", d.name, formatRupiah(d.payment_ammount?.toDouble())))
                    //Log.i("DiscProbs", "Formatted line: '${builder.toString()}'")
                    totalTransaction=totalTransaction-d.payment_ammount!!
                }
            }
            builder.append("-".repeat(getPadding("","Left",c))+"\n")
            builder.append(String.format("%-10s%19s\n", "Total:", formatRupiah(totalTransaction)))
        }
        if (payments!=null){
            var paymentAmmountSum :Int = 0
            for (p in payments){
                paymentAmmountSum +=p.payment_ammount?:0
                val sisa = totalTransaction - paymentAmmountSum
                builder.append(String.format("%-10s%19s\n", "Bayar:", formatRupiah(p.payment_ammount?.toDouble())))
                builder.append("-".repeat(getPadding("","Left",c))+"\n")
                if (totalTransaction>paymentAmmountSum) {
                    builder.append(String.format("%-10s%19s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-10s%19s\n", "Kembalian:", formatRupiah(abs(sisa))))
                }
            }
        }
        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("   $message!\n\n\n\n")
        Log.i("DiscProbs", "Formatted line: '${builder.toString()}'")
        return builder.toString()
    }
}