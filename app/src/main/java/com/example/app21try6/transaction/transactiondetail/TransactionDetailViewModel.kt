package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.DiscountTransDao
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
import java.lang.Math.abs
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
                                  private val discountDao:DiscountDao,
                                  private val discountTransDao: DiscountTransDao,
                                  private val customerDao:CustomerDao,
                                  var id:Int):AndroidViewModel(application){

    //transaction detail for recyclerview
    val transDetail = datasource2.selectATransDetail(id)
    val transDetailWithProduct= datasource2.getTransactionDetailsWithProduct(id)
    //Transaction Summary
    val transSum = datasource1.getTransSum(id)
    //Total Trans
    val transTotalDouble = datasource2.getTotalTrans(id)
    val transTotal: LiveData<String> = Transformations.map(transTotalDouble) { formatRupiah(it).toString() }
   //To update icons color on night mode or liht mode
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode
    //Text Generator to send receipt
    private lateinit var textGenerator: TextGenerator

    // Get the total discount from the database
    // Get the total discount from the database
    val discSum: LiveData<Double> = discountTransDao.getTotalDiscountBySumId(id)

    // Calculate 'bayar' with the discount applied
    var bayar: MediatorLiveData<String> = MediatorLiveData<String>().apply {
        var transactionTotal: Double = 0.0
        var paidAmount: Double = 0.0
        var discountValue: Double = 0.0

        // Update 'bayar' based on 'transSum' and 'discSum'
        fun updateBayar() {
            val remaining = transactionTotal - paidAmount // Remaining after payment
            val finalAmount = remaining - discountValue // Subtract the discount
            val label = when {
                finalAmount <= 0 -> "Kembalian: "
                paidAmount == 0.0 -> "Total: "
                else -> "Sisa: "
            }
            value = label + formatRupiah(abs(finalAmount)).toString()
        }

        // Observe 'transSum' to get total transaction amount
        addSource(transSum) { item ->
            transactionTotal = item.total_trans ?: 0.0
            paidAmount = item.paid.toDouble() ?: 0.0 // Get paid amount
            updateBayar()
        }

        // Observe 'discSum' to get total discount
        addSource(discSum) { discountAmount ->
            discountValue = discountAmount ?: 0.0
            updateBayar()
        }
    }

    // Get the total discount from the database

    // Combine 'bayar' and 'discSum' into a single LiveData


    var itemCount :LiveData<String> = Transformations.map(transDetail) { items->
        "${items.size} item"
    }

    //Transaction Summary note on edit text and carview
    var txtNote =MutableLiveData<String?>()
    val paymentModel = datasource4.selectPaymentModelBySumId(id)

    //Toggle boolean
    private var _isBtnBayarClicked = MutableLiveData<Boolean>(false)
    val isBtnBayarCLicked :LiveData<Boolean> get() = _isBtnBayarClicked

    private var _isBtnpaidOff = MutableLiveData<Boolean>()
    val isBtnpaidOff :LiveData<Boolean> get() = _isBtnpaidOff
    //hide or show card view
    private var _isCardViewShow = MutableLiveData<Boolean>()
    val isCardViewShow :LiveData<Boolean> get() = _isCardViewShow
    // toggle text view and edit text
    private var _isTxtNoteClick =MutableLiveData<Boolean>(false)
    val isTxtNoteClick :LiveData<Boolean> get() = _isTxtNoteClick
    // button send to wa
    private val _sendReceipt = MutableLiveData<Boolean>()
    val sendReceipt:LiveData<Boolean> get() = _sendReceipt
    //trans sum data long click
    private val _transSumDateLongClick = MutableLiveData<Boolean>()
    val transSumDateLongClick:LiveData<Boolean> get() = _transSumDateLongClick

    // change the note icon collor if note not empty
    var isn: LiveData<Boolean> = Transformations.map(transSum) { item ->
        if(item?.sum_note.isNullOrEmpty()) false else true
    }
    //Navigations
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit


    //val discountTransBySumId=discountTransDao.selectAllDiscTrans(id)
    val discountTransBySumId=discountTransDao.selectDiscAsPaymentModel(id)
    //Set ui to change icons color when night mode or day mode on
    fun setUiMode(mode:Int){
        _uiMode.value = mode
    }
    /******************************************** CRUD **************************************/

   fun deleteDiscount(id:Int){
       viewModelScope.launch {
           deleteDiscTransToDB(id)
       }
   }
   private suspend fun deleteDiscTransToDB(id: Int){
       withContext(Dispatchers.IO){
           discountTransDao.delete(id)
       }
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
        if (note!=null && note!=""){
           showCardDialog()
        }
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
    fun showCardDialog(){
        _isCardViewShow.value = true
        _isTxtNoteClick.value=true
    }

    //Toggle and update TransSUm is paid off on btn click
    fun updateIsPaidOffValue() {
        viewModelScope.launch {
            val transSum = transSum.value
            transSum?.is_paid_off = transSum?.is_paid_off?.not() ?: true
            onImageClick(transSum!!.is_paid_off)
        }
    }

    //set mutable isPaidOff value upter update transSum is paid off
    fun onImageClick(bool:Boolean){
        _isBtnpaidOff.value = bool
    }

    // update Payment table
    fun bayar(paymentModel:PaymentModel){
        viewModelScope.launch {
            val bayar = modelToPaymentConverter(paymentModel)
            if (paymentModel.id!=null){
                bayar.id = paymentModel.id!!
                updatePaymentDB(bayar) }
            else{
                insertPayment(bayar)
                }
            updateTransSum()
        }
    }
    fun updateDiscount(paymentModel:PaymentModel){
        viewModelScope.launch {
            updateDiscountToDb(paymentModel.id!!,paymentModel.payment_ammount!!.toDouble())
        }
    }
    private suspend fun updateDiscountToDb(id:Int,ammount:Double){
        withContext(Dispatchers.IO){
            discountTransDao.updateById(id,ammount)
        }
    }

    private fun insertPayment(bayar:Payment){
        viewModelScope.launch {
            bayar.payment_date =Date()
            bayar.sum_id = transSum.value?.sum_id ?:-1
            bayar.payment_ref = UUID.randomUUID().toString()
            insertPaymentToDB(bayar)
            updateTransSum()
        }
    }
    fun deletePayment(id:Int){
        viewModelScope.launch {
            deletePaymentDB(id)
            updateTransSum()
        }
    }
    //Convert PaymentModel to PaymentTable for insert or update purpose
    private fun modelToPaymentConverter(paymentModel: PaymentModel):Payment{
        val bayar = Payment()
        bayar.payment_ammount= paymentModel.payment_ammount!!
        bayar.payment_date =paymentModel.payment_date
        bayar.sum_id = transSum.value?.sum_id ?:-1
        bayar.payment_ref = paymentModel.ref?:""
        return bayar
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
               summary.year= calendar.get(Calendar.YEAR)
               summary.month = dateFormat.format(transSum.value!!.trans_date)
               summary.month_number = calendar.get(Calendar.MONTH)+1
               summary.day = calendar.get(Calendar.DATE)
               summary.day_name = transSum.value!!.trans_date.toString()
               summary.item_name = getProductName(it.trans_item_name)?: it.trans_item_name
               summary.price = it.trans_price.toDouble()
               summary.item_sold = it.qty
               summary.total_income = it.total_price
               insertItemToSummaryDB(summary)
           }
            discountTransBySumId.value?.forEach {
                val summary = Summary()
                summary.year= calendar.get(Calendar.YEAR)
                summary.month = dateFormat.format(transSum.value!!.trans_date)
                summary.month_number = calendar.get(Calendar.MONTH)+1
                summary.day = calendar.get(Calendar.DATE)
                summary.day_name = transSum.value!!.trans_date.toString()
                summary.item_name = it.name?: "Diskon"
                summary.price = it.payment_ammount?.toDouble()?.times((-1)) ?: 0.0
                summary.item_sold = 1.0
                summary.total_income = it.payment_ammount?.toDouble()?.times((-1)) ?: 0.0
                insertDiscountToSummaryDB(summary)
            }

        }
    }
    //update Todays Date onClick
    fun updateDate(){
        viewModelScope.launch {
            if (transSum.value!=null) {
                val transsummary = transSum.value!!
                transsummary.trans_date = Date()
                updateTransSumDB(transsummary)
            }
        }
    }
    fun updateLongClickedDate(date:Date){
        viewModelScope.launch {
            if (transSum.value!=null) {
                val transsummary = transSum.value!!
                transsummary.trans_date = date
                updateTransSumDB(transsummary)
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

    private suspend fun updatePaymentDB(payment: Payment) {
        withContext(Dispatchers.IO) {
             datasource4.update(payment)
        }
    }
    private suspend fun deletePaymentDB(id:Int){
        withContext(Dispatchers.IO){
            datasource4.deletePayment(id)
        }
    }
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
    private suspend fun getProductName(subName:String):String?{
        return withContext(Dispatchers.IO){
            datasource5.getProductName(subName)
        }
    }
    private suspend fun insertItemToSummaryDB(summary: Summary){
        withContext(Dispatchers.IO){
            datasource3.insertOrUpdate(summary)
        }
    }
    private suspend fun insertDiscountToSummaryDB(summary: Summary){
        withContext(Dispatchers.IO){
            datasource3.insertOrUpdateD(summary)
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

    fun generateReceiptTextWa(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value)
        return textGenerator.generateReceiptTextWa()
    }
    fun generateReceiptTextNew(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value)
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
    //Toggle
    fun onBtnBayarClick(){
        _isBtnBayarClicked.value = true
    }
    fun onBtnBayarClicked(){
        _isBtnBayarClicked.value = false
    }
    fun onLongClick(v: View): Boolean { return true }
    fun onTxtTransSumLongClikc(v:View):Boolean{
        _transSumDateLongClick.value = true
    return true
    }
    fun onTxtTransSumLongClikced(){_transSumDateLongClick.value = false}
}