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
import com.example.app21try6.database.SubProductDao
import com.example.app21try6.database.Summary
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.formatRupiah
import com.example.app21try6.utils.TextGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val transSum = datasource1.getTransSum(id)
    val transTotalDouble = datasource2.getTotalTrans(id)
    val transTotal: LiveData<String> = Transformations.map(transTotalDouble) { formatRupiah(it).toString() }
   //To update icons color on night mode or liht mode
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode
    //Text Generator to send receipt
    private lateinit var textGenerator: TextGenerator

    // trans sum total - trans sum paid
    var bayar :LiveData<String> = Transformations.map(transSum) { item->
       val a = item.total_trans.let { total ->
           item.paid.toDouble().let { paid ->
               total - paid
           } ?: total
       }
        formatRupiah(a).toString()
    }
    var txtNote =MutableLiveData<String?>()
    val paymentModel = datasource4.selectPaymentModelBySumId(id)

    //Toggle boolean
    private var _isBtnBayarClicked = MutableLiveData<Boolean>(false)
    val isBtnBayarCLicked :LiveData<Boolean> get() = _isBtnBayarClicked

    private var _isBtnpaidOff = MutableLiveData<Boolean>()
    val isBtnpaidOff :LiveData<Boolean> get() = _isBtnpaidOff

    private var _isCardViewShow = MutableLiveData<Boolean>()
    val isCardViewShow :LiveData<Boolean> get() = _isCardViewShow

    private var _isTxtNoteClick =MutableLiveData<Boolean>()
    val isTxtNoteClick :LiveData<Boolean> get() = _isTxtNoteClick

    private val _sendReceipt = MutableLiveData<Boolean>()
    val sendReceipt:LiveData<Boolean> get() = _sendReceipt
    var isn: LiveData<Boolean> = Transformations.map(transSum) { item ->
        if(item?.sum_note.isNullOrEmpty()) false else true
    }

    //Navigations
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit



    /******************************************** CRUD **************************************/
    //Set ui to change icons color when night mode or day mode on
    fun setUiMode(mode:Int){
        _uiMode.value = mode
    }
    //Toggle and update Transaction Summary is_taken value when btn_is_taken clicked
    fun updateBooleanValue() {
       viewModelScope.launch {
           val transSum = transSum.value
           transSum?.is_taken_ = transSum?.is_taken_?.not() ?: true
           transSum?.let { updataTransSumDB(it) }
       }
    }
    //Set mutable txt Note value after trans_sum.value updated
    fun setTxtNoteValue(note:String?){
        txtNote.value = note
    }
    //Toggle is_note Clicked value when Text View note_Textview is clicked
    fun onTxtNoteClick(){
        _isTxtNoteClick.value = _isTxtNoteClick.value?.not() ?: true
    }
    //Toggle is_note Clicked value and update transSum note value when Text View ok_Textview is clicked
    fun onTxtNoteOkClicked(){
        viewModelScope.launch {
            onTxtNoteClick()
            val transum = transSum.value
            transum?.sum_note = txtNote.value
            updateTransSumDB(transum!!)
        }
    }
    //Hide and show card view on click
    fun onBtnNoteClick(){
        _isCardViewShow.value = _isCardViewShow.value?.not() ?: true
    }
    //Toggle and update TransSUm is paid off on btn click
    fun updateIsPaidOffValue() {
        viewModelScope.launch {
            val transSum = transSum.value
            transSum?.is_paid_off = transSum?.is_paid_off?.not() ?: true
            transSum?.let { updataTransSumDB(it) }
            onImageClicked(transSum!!.is_paid_off)
        }
    }
    //set mutable isPaidOff value upter update transSum is paid off
    private fun onImageClicked(bool:Boolean){
        _isBtnpaidOff.value = bool
    }
    // update Payment table
    fun bayar(num:Int){
        viewModelScope.launch {
            val bayar = Payment()
            bayar.payment_ammount= num
            bayar.payment_date =Date()
            bayar.sum_id = transSum.value?.sum_id ?:-1
            bayar.payment_ref = UUID.randomUUID().toString()
            insertPaymentToDB(bayar)
            updateTransSum()
        }
    }
    // update Transum value
    private fun updateTransSum(){
        viewModelScope.launch {
            val bayar = withContext(Dispatchers.IO){
                datasource4.selectSumFragmentBySumId(transSum.value!!.sum_id)
            }
            val transSum = transSum.value!!
            transSum.paid= bayar
            updateTransSumDB(transSum)
        }
    }
    //update transum is keeped value and insert call insert To Summary Function when btn pembukuan click
    fun onBtnPembukuanClick(){
        viewModelScope.launch {
            val transsummary = transSum.value
            if (transsummary?.is_keeped==false){
            transsummary.is_keeped = transsummary.is_keeped.not() ?: true
            updataTransSumDB(transsummary)
               insertToSummary()
            }
        }
    }
    //Insert transdetail list to Summary
    private fun insertToSummary(){
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.time = transSum.value!!.trans_date
            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())

            transDetail.value?.forEach {it->
               val summary = Summary()
                Log.i("INSERTSUMMARYBUG","insertSummary summary ${summary}")
               summary.year= calendar.get(Calendar.YEAR)
               summary.month = dateFormat.format(transSum.value!!.trans_date)
               summary.month_number = calendar.get(Calendar.MONTH)+1
               summary.day = calendar.get(Calendar.DATE)
               summary.day_name = transSum.value!!.trans_date.toString()
               summary.item_name = getProductName(it.trans_item_name)
               summary.price = it.trans_price.toDouble()
               summary.item_sold = it.qty
               summary.total_income = it.total_price
               insertItemToSummaryDB(summary)
           }
        }
    }
    //Update Trans Detail value
    fun updateTransDetail(transdetail:TransactionDetail){
        viewModelScope.launch {
            updateTransDetailDB(transdetail)
        }
    }
    /******************************************** Suspend **************************************/
    private suspend fun updateTransSumDB(transSum: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transSum)
        }
    }
    private suspend fun updataTransSumDB(transSum:TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transSum)
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
    private suspend fun updateTransDetailDB(transdetail: TransactionDetail){
        withContext(Dispatchers.IO){
            datasource2.update(transdetail)
        }
    }
    private suspend fun insertPaymentToDB(payment: Payment){
        withContext(Dispatchers.IO){
            datasource4.insert(payment)
        }
    }
    fun generateReceiptText(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value)
        return textGenerator.generateReceiptText()
    }
    fun generateReceiptTextNew(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value)
        return textGenerator.generateReceiptTextNew()
    }

    /******************************************** Navigation**************************************/

    fun onKirimBtnClick(){
        _sendReceipt.value=true
    }
    fun onKirimBtnClicked(){
        _sendReceipt.value = false
    }
    fun onNavigateToEdit(){
        _navigateToEdit.value = id
    }
    fun onNavigatedToEdit(){this._navigateToEdit.value = null}
    fun onBtnBayarClick(){
        _isBtnBayarClicked.value = true
    }
    fun onBtnBayarClicked(){
        _isBtnBayarClicked.value = false
    }

}