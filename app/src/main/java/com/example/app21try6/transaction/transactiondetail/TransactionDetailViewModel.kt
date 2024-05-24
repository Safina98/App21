package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.Payment

import com.example.app21try6.database.PaymentDao
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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class TransactionDetailViewModel (application: Application,
                                  private val datasource1: TransSumDao,
                                  private val datasource2: TransDetailDao,
                                  private val datasource3: SummaryDbDao,
                                  private val datasource4: PaymentDao,
                                  private val datasource5: SubProductDao,
                                  var id:Int):AndroidViewModel(application){

    val transDetail = datasource2.selectATransDetail(id)
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit
    val trans_sum = datasource1.getTransSum(id)
    val trans_total_ = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> = Transformations.map(trans_total_) { formatRupiah(it).toString() }
    private val _sendReceipt = MutableLiveData<Boolean>()
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode

    var bayar :LiveData<String> = Transformations.map(trans_sum) {item->
       val a = item.total_trans.let { total ->
           item.paid.toDouble().let { paid ->
               total - paid
           } ?: total
       }
        formatRupiah(a.toDouble()).toString()
    }
    var isn: LiveData<Boolean> = Transformations.map(trans_sum) { item ->
        if(item?.sum_note.isNullOrEmpty()) false else true
    }
    var txtNote =MutableLiveData<String?>()
    //var bayar = formatRupiah(trans_sum.value?.paid?.toDouble())
    val sendReceipt:LiveData<Boolean> get() = _sendReceipt

    val paymentModel = datasource4.selectPaymentModelBySumId(id)

    private var _isBtnBayarClicked = MutableLiveData<Boolean>(false)
    val isBtnBayarCLicked :LiveData<Boolean> get() = _isBtnBayarClicked

    private var _isBtnpaidOff = MutableLiveData<Boolean>()
    val isBtnpaidOff :LiveData<Boolean> get() = _isBtnpaidOff

    private var _isCardViewShow = MutableLiveData<Boolean>()
    val isCardViewShow :LiveData<Boolean> get() = _isCardViewShow

    private var _isTxtNoteClick =MutableLiveData<Boolean>()
    val isTxtNoteClick :LiveData<Boolean> get() = _isTxtNoteClick

    init{
        Log.i("NOTEPROB"," init isn ${isn.value}")
    }
    fun onNavigateToEdit(){
        _navigateToEdit.value = id
    }
    fun onNavigatedToEdit(){this._navigateToEdit.value = null}
    private val _booleanValue = MutableLiveData<Boolean>()
    val decimalFormat = DecimalFormat("#.##")

    fun setUiMode(mode:Int){
        _uiMode.value = mode
    }

    fun updateBooleanValue() {
       viewModelScope.launch {
           var transSum = trans_sum.value
          transSum?.is_taken_ = transSum?.is_taken_?.not() ?: true
           transSum?.let { updataTransSumDB(it) }
       }
    }
    fun setTxtNoteValue(note:String?){
        txtNote.value = note
    }
    fun onTxtNoteClick(){
        _isTxtNoteClick.value = _isTxtNoteClick.value?.not() ?: true
        //Log.i("NOTEPROB"," onTXTCLICK ${_isCardViewShow.value}")
    }
    fun onTxtNoteOkClicked(){
        viewModelScope.launch {
            onTxtNoteClick()
            var transum = trans_sum.value
            transum?.sum_note = txtNote.value
            updateTransSumDB(transum!!)
        }
    }
    fun onBtnNoteClick(){
        _isCardViewShow.value = _isCardViewShow.value?.not() ?: true
    }
    fun onBtnNoteClikced(){_isCardViewShow.value = false}
    fun updateIsPaidOffValue() {
        viewModelScope.launch {
            var transSum = trans_sum.value
            transSum?.is_paid_off = transSum?.is_paid_off?.not() ?: true
            transSum?.let { updataTransSumDB(it) }
            onImageClicked(transSum!!.is_paid_off)
        }
    }
    fun onImageClicked(bool:Boolean){
        _isBtnpaidOff.value = bool
    }

    fun bayar(num:Int){
        viewModelScope.launch {
            val bayar = Payment()
            bayar.payment_ammount= num
            bayar.payment_date =Date()
            bayar.sum_id = trans_sum.value?.sum_id ?:-1
            bayar.payment_ref = UUID.randomUUID().toString()
            insertPaymentToDB(bayar)
            updateTransSum()
        }
    }
    fun updateTransSum(){
        viewModelScope.launch {
            var bayar = withContext(Dispatchers.IO){
                datasource4.selectSumFragmentBySumId(trans_sum.value!!.sum_id)
            }
            var transSum = trans_sum.value!!
           transSum.paid= bayar
            updateTransSumDB(transSum)
        }
    }
    private suspend fun updateTransSumDB(transSum: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transSum)
        }
    }

    private suspend fun insertPaymentToDB(payment: Payment){
        withContext(Dispatchers.IO){
            datasource4.insert(payment)
        }
    }



    fun onBtnBayarClick(){
        _isBtnBayarClicked.value = true
    }
    fun onBtnBayarClicked(){
        _isBtnBayarClicked.value = false
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
        val payments = paymentModel.value
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
                val price  = if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"
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
                builder.append(String.format("%-24s%14s\n", "Bayar:", formatRupiah(p.payment_ammount?.toDouble())))
                if (transsum.total_trans!!>paymentAmmountSum)
                {
                    builder.append(String.format("%-24s%14s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-24s%14s\n", "Kembalian:", formatRupiah(sisa)))
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
        val items = transDetail.value
        val payments = paymentModel.value
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
                val itemTotalPrice = item.qty * item.trans_price*item.unit_qty

                builder.append(String.format("%-24s\n", item.trans_item_name))
                //builder.append(String.format("%3.2f   %-3s %3s\n",  item.qty, x, if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                if (item.unit ==null){
                    builder.append(String.format("%3.2f %10s %3s\n",  item.qty, x, if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                }
                else{
                    if (item.unit_qty!=1.0)
                    {
                        builder.append(String.format("%2s %1s %1s %-2s %1s\n",  decimalFormat.format(item.qty),item.unit,if (item.unit_qty!=1.0) "(${item.unit_qty})" else "   ", x,  if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
                    }else{
                        builder.append(String.format("%12s %1s  %3s %10s\n",  decimalFormat.format(item.qty),item.unit, x,  if(item.qty>=1)formatRupiah(item.trans_price.toDouble()) else "-"))
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
                if (transsum.total_trans!!>paymentAmmountSum)
                {
                    builder.append(String.format("%-10s%19s\n", "Sisa:", formatRupiah(sisa)))
                }
                else{
                    builder.append(String.format("%-10s%19s\n", "Kembalian:", formatRupiah(sisa)))
                }

            }
        }

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