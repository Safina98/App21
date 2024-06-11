package com.example.app21try6.utils

import com.example.app21try6.database.Payment
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.formatRupiah
import com.example.app21try6.transaction.transactiondetail.PaymentModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TextGenerator(
                    var transDetail:List<TransactionDetail>?,
                    var transSum:TransactionSummary?,
                    var paymentModel: List<PaymentModel>?
                    ) {
    val decimalFormat = DecimalFormat("#.##")
    fun getPadding(value:String, position:String,constant:Int):Int
    {
        val stringLength = value.length
        return if(position=="Middle")  ((constant - stringLength) / 2) else (constant - value.length)
    }
    fun generateReceiptText(): String {
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
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Receipt header
        //builder.append("------------------ $storeName ------------------\n")
        builder.append("$paddingStoreName $storeName $paddingStoreName\n")
        builder.append("$storeAddress\n")
        builder.append("$storePhone\n")
        builder.append("Receipt:" +" ".repeat(getPadding("Receipt Receipt : Date : $currentDate","Right",50))+ "Date: $currentDate\n\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append("${transsum?.cust_name}\n")
        builder.append("Barang  Jumlah  Harga  Total\n")
        builder.append("-".repeat(getPadding("","Left",50))+"\n")

        decimalFormat.isDecimalSeparatorAlwaysShown = false
        // Receipt items
        var x ="x"
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
        builder.append(String.format("%-24s%14s\n", "Total:", formatRupiah(transsum?.total_trans)))
        if (payments!=null){
            var paymentAmmountSum :Int = 0
            for (p in payments){
                paymentAmmountSum +=p.payment_ammount?:0
                var sisa = transsum!!.total_trans - paymentAmmountSum
                builder.append(String.format("%-22s%14s\n", "Bayar :", formatRupiah(p.payment_ammount?.toDouble())))
                builder.append("-".repeat(getPadding("","Left",50))+"\n")
                if (transsum.total_trans!!>paymentAmmountSum)
                {
                    builder.append(String.format("%-24s%14s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-24s%14s\n", "Kembalian:", formatRupiah(abs(sisa))))
                }

            }
        }
        builder.append("-".repeat(getPadding("","Left",50))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("                Have a nice day!\n")
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
        builder.append(String.format("%-10s%19s\n", "Total:", formatRupiah(transsum?.total_trans)))
        if (payments!=null){
            var paymentAmmountSum :Int = 0
            for (p in payments){
                paymentAmmountSum +=p.payment_ammount?:0
                var sisa = transsum!!.total_trans - paymentAmmountSum
                builder.append(String.format("%-10s%19s\n", "Bayar:", formatRupiah(p.payment_ammount?.toDouble())))
                builder.append("-".repeat(getPadding("","Left",c))+"\n")
                if (transsum.total_trans!!>paymentAmmountSum)
                {
                    builder.append(String.format("%-10s%19s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-10s%19s\n", "Kembalian:", formatRupiah(abs(sisa))))
                }

            }
        }

        builder.append("-".repeat(getPadding("","Left",c))+"\n")
        builder.append("Terimakasih atas pembelian anda\n")
        builder.append("      Have a nice day!\n\n\n\n")
        return builder.toString()

    }
}