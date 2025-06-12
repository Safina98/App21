package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.app21try6.DISCTYPE
import com.example.app21try6.database.tables.Payment

import com.example.app21try6.database.tables.Summary
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatRupiah
import com.example.app21try6.utils.TextGenerator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/*
 private val datasource1: TransSumDao,
                                  private val datasource2: TransDetailDao,
                                  private val datasource3: SummaryDbDao,
                                  private val datasource4: PaymentDao,
                                  private val datasource5: SubProductDao,
                                  private val discountDao: DiscountDao,
                                  private val discountTransDao: DiscountTransDao,
                                  private val customerDao: CustomerDao,
 */
class TransactionDetailViewModel (
    val stockRepo: StockRepositories,
    val bookRepo: BookkeepingRepository,
    val transRepo: TransactionsRepository,
    val discountRepo:DiscountRepository,
    application: Application,
    var id:Int):AndroidViewModel(application){

    //transaction detail for recyclerview
    val transDetail = transRepo.getTransactionDetails(id)
    val transDetailWithProduct= transRepo.getTransactionDetailsWithProductID(id)
    //Transaction Summary
    val transSum = transRepo.getTransactionSummary(id)
    //Total Trans
    val transTotalDouble = transRepo.getTotalTransaction(id)
    val transTotal: LiveData<String> = transTotalDouble.map { formatRupiah(it).toString() }
   //To update icons color on night mode or liht mode
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode
    //Text Generator to send receipt
    private lateinit var textGenerator: TextGenerator

    private var _isDiskClicked=MutableLiveData<Boolean>()
    val isDiscClicked:LiveData<Boolean> get() = _isDiskClicked

    private var _multipleMerch=MutableLiveData<Double?>()
    val multipleMerch:LiveData<Double?> get() = _multipleMerch

    private var _retailMerchList=MutableLiveData<List<MerchandiseRetail>?>()
    val retailMerchList:LiveData<List<MerchandiseRetail>?> get() = _retailMerchList

    private var merchSelectionDeferred: CompletableDeferred<List<MerchandiseRetail?>?>? = null

    // Get the total discount from the database
    // Get the total discount from the database
    val discSum: LiveData<Double> = discountRepo.getTransactionTotalDiscounts(id)

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
            if (item != null) {
                transactionTotal = item.total_trans ?: 0.0
                paidAmount = item.paid?.toDouble() ?: 0.0
            } else {
                transactionTotal = 0.0
                paidAmount = 0.0
            }
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


    var itemCount :LiveData<String> = transDetail.map{ items->
        "${items.size} item"
    }

    //Transaction Summary note on edit text and carview
    var txtNote =MutableLiveData<String?>()
    //payments
    val paymentModel = discountRepo.getTransactionPayments(id)

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
    var isn: LiveData<Boolean> = transSum.map{ item ->
        if(item?.sum_note.isNullOrEmpty()) false else true
    }


    //Navigations
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int> get() = _navigateToEdit

    val discountTransBySumId=discountRepo.getTransactionDiscounts(id)
    //Set ui to change icons color when night mode or day mode on
    init {
        _multipleMerch.value=null
        _retailMerchList.value=null
    }
    fun setUiMode(mode:Int){
        _uiMode.value = mode
    }
    /******************************************** CRUD **************************************/

    fun getSummaryWithNullProductId(){
        viewModelScope.launch {
            //val list = withContext(Dispatchers.IO){database.getAllSummaryProductId()}
            val simpleFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = simpleFormatter.parse("2024-11-27 00:00")
            val dateEnd = simpleFormatter.parse("2024-11-27 23:59")

            val startDate = simpleFormatter.parse("2024-01-01 00:00")!!
            val endDate = simpleFormatter.parse("2024-12-01 00:00")!! // Exclusive end date



        }
    }
   fun deleteDiscount(id:Int){
       viewModelScope.launch {
           discountRepo.deleteTransactionDiscount(id)
       }
   }

    //Toggle and update Transaction Summary is_taken value when btn_is_taken clicked
    fun updateBooleanValue() {
       viewModelScope.launch {
           val transSum = transSum.value
           transSum?.is_taken_ = transSum?.is_taken_?.not() ?: true
           transSum?.let { transRepo.updateTransactionSummary(it) }
       }
    }

    fun updateLogBooleanValue() {
        viewModelScope.launch {
            val transSum = transSum.value
            transSum?.is_logged = transSum?.is_logged?.not() ?: true
            transSum?.let { transRepo.updateTransactionSummary(it) }
            updateRetail(transSum?.is_logged?:true)
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
            transRepo.updateTransactionSummary(transum!!)
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
    fun bayar(paymentModel: PaymentModel){
        viewModelScope.launch {
            val bayar = modelToPaymentConverter(paymentModel)
            if (paymentModel.id!=null){
                bayar.id = paymentModel.id!!
                discountRepo.updatePayment(bayar) }
            else{
                insertPayment(bayar)
                }
            updateTransSum()
        }
    }
    fun updateDiscount(paymentModel: PaymentModel){
        viewModelScope.launch {
            if(paymentModel.id==null){
                val discount=DiscountTransaction()
                discount.discTransRef =UUID.randomUUID().toString()
                discount.discTransDate=Date()
                discount.discTransName=paymentModel.name?:"Potongan: "
                discount.discountAppliedValue=paymentModel.payment_ammount?.toDouble() ?: 0.0
                discount.sum_id=id
                discountRepo.insertTransactionDiscount(discount)
            }else{
                discountRepo.updateTransactionDiscount(paymentModel.id!!,paymentModel.payment_ammount!!.toDouble())
            }
            val discountList = discountRepo.getDiscountTransactionList(id)
            updateTotalAfterDiscount(discountList)
        }
    }
    fun updateTotalAfterDiscount(discountList: List<DiscountTransaction>){
        viewModelScope.launch {
            val totalDiscount = discountList.sumOf { it.discountAppliedValue}
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            transSumS.total_after_discount = transSumS.total_trans-totalDiscount
            Log.i("DiscProbs","total trans: ${transSumS.total_trans}")
            Log.i("DiscProbs","diskon: ${totalDiscount}")
            Log.i("DiscProbs","total after discount: ${transSumS.total_after_discount}")
            transRepo.updateTransactionSummary(transSumS)
        }
    }


    private fun insertPayment(bayar: Payment){
        viewModelScope.launch {
            bayar.payment_date =Date()
            bayar.sum_id = transSum.value?.sum_id ?:-1
            bayar.payment_ref = UUID.randomUUID().toString()
            discountRepo.insertPayment(bayar)
            updateTransSum()
        }
    }
    fun deletePayment(id:Int){
        viewModelScope.launch {
            discountRepo.deletePayment(id)
            updateTransSum()
        }
    }
    //Convert PaymentModel to PaymentTable for insert or update purpose
    private fun modelToPaymentConverter(paymentModel: PaymentModel): Payment {
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
            val bayar =discountRepo.getTransactionTotalPayment(id)
            val transSum = transSum.value!!
            transSum.paid= bayar
            transRepo.updateTransactionSummary(transSum)
        }
    }


    //update transum is keeped value and insert call insert To Summary Function when btn pembukuan click
    fun onBtnPembukuanClick(){
        viewModelScope.launch {
            val transsummary = transSum.value
            if (transsummary?.is_keeped==false){
                transsummary.is_keeped = transsummary.is_keeped.not() ?: true
                transRepo.updateTransactionSummary(transsummary)
                insertToSummary()
            }
        }
    }
    //Insert transdetail list to Summary
    fun updateRetailOld(bool: Boolean){
       viewModelScope.launch{
            val transDetails=transDetail.value
            transDetails?.forEach {trans->
                //get the sub product id
                val subId=trans.sub_id
                //get the retail by sub product id
                val retailList = stockRepo.selectRetailBySumIdS(subId?:-1)
                var value:Double=trans.qty
                if(retailList?.size?:0>1){
                    //Show dialog to choose which merch to pick
                    _multipleMerch.value=trans.qty
                    Log.i("MERCHPROBS","viewmodel $retailList")
                    _retailMerchList.value=retailList!!.toList()
                }else{
                    val merch =retailList?.get(0)
                    if(merch!=null){
                        merch.net =if (bool==true) merch.net-trans.qty else merch.net+trans.qty
                        stockRepo.updateDetailRetail(merch)
                    }
                }


            }
        }
    }
    fun updateRetail(bool: Boolean) {
        viewModelScope.launch {
            val transDetails = transDetail.value
            transDetails?.let {
                for (trans in it) {
                    val subId = trans.sub_id
                    val retailList = stockRepo.selectRetailBySumIdS(subId ?: -1)
                    var value: Double = trans.qty

                    if ((retailList?.size ?: 0) > 1) {
                        // Set up LiveData to trigger popup
                        _multipleMerch.value = trans.qty
                        _retailMerchList.value = retailList!!.toList()

                        // Wait for user to pick a merch from the popup
                        val selectedMerch = waitForMerchSelection()  // suspending function
                       updateMerchValue(selectedMerch,trans.qty)
                    } else {
                        val merch = retailList?.getOrNull(0)
                        if (merch != null) {
                            merch.net = if (bool) merch.net - trans.qty else merch.net + trans.qty
                            stockRepo.updateDetailRetail(merch)
                        }
                    }
                }
            }
        }
    }
    suspend fun waitForMerchSelection(): List<MerchandiseRetail?>? {
        merchSelectionDeferred = CompletableDeferred()
        return merchSelectionDeferred?.await()
    }

    fun onMerchSelected(merch: List<MerchandiseRetail?>?) {
        merchSelectionDeferred?.complete(merch)
        merchSelectionDeferred = null
    }

    fun updateMerchValue(merchList:List<MerchandiseRetail?>?,qty:Double){
        viewModelScope.launch {
            var remainingQty=qty
            if (merchList!=null){
                merchList.sortedBy { it!!.net }.forEach { merch->
                    if ((remainingQty-merch!!.net)>=0){
                        remainingQty=remainingQty + 0.20 - merch.net
                        merch.net=0.0
                    }else{
                        merch.net=merch.net-remainingQty
                        remainingQty=-1.0
                    }
                    stockRepo.updateDetailRetail(merch)
                    Log.i("MERCHPROBS","id: ${merch.id} net ${merch.net}, remainingqty: $remainingQty")
                }
            }

        }
    }

    fun setValuesToNull(){
        _retailMerchList.value=null
        _multipleMerch.value=null
    }

    private fun insertToSummary(){
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.time = transSum.value!!.trans_date
            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            transDetail.value?.forEach { it ->
                val product = stockRepo.getProductBySubId(it.sub_id?:-1)
                if(product?.product_name?.contains("biaya", ignoreCase = true) != true){
                    val summary = Summary().apply {
                        year = calendar.get(Calendar.YEAR)
                        month = dateFormat.format(transSum.value!!.trans_date)
                        month_number = calendar.get(Calendar.MONTH) + 1
                        day = calendar.get(Calendar.DATE)
                        day_name = transSum.value!!.trans_date.toString()
                        item_name = product?.product_name ?: it.trans_item_name
                        sub_id = it.sub_id
                        product_id = product?.product_id
                        price = it.trans_price.toDouble()
                        total_income = it.total_price
                        product_capital = (product?.product_capital ?: 0.0).toInt()
                    }
                    val unitQty = it.unit_qty ?: 1.0
                    summary.item_sold = it.qty * unitQty
                    if (it.unit == "Lusin" && unitQty == 1.0) {
                        summary.item_sold = it.qty * 12
                        summary.product_capital = (product?.product_capital ?: 0.0).toInt()
                    } else if (it.unit!=null && unitQty == 1.0) {
                        summary.product_capital = (product?.alternate_capital ?: it.total_price).toInt()
                    }
                    // Log.i("profitbrobs","${summary.item_name} ${summary?.product_capital}")
                    //Log.i("profitbrobs","${product}")
                    bookRepo.insertTransactionToSummary(summary)
                }

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
                bookRepo.insertTransactionToSummary(summary)
            }

        }
    }
    //update Todays Date onClick
    fun updateDate(){
        viewModelScope.launch {
            if (transSum.value!=null) {
                val transsummary = transSum.value!!
                transsummary.trans_date = Date()
                transRepo.updateTransactionSummary(transsummary)
            }
        }
    }
    fun updateLongClickedDate(date:Date){
        viewModelScope.launch {
            if (transSum.value!=null) {
                val transsummary = transSum.value!!
                transsummary.trans_date = date
                transRepo.updateTransactionSummary(transsummary)
            }
        }
    }

    //Update Trans Detail value
    fun updateTransDetail(transdetail: TransactionDetail){
        viewModelScope.launch {
            transRepo.updateTransDetail(transdetail)
        }
    }
    /******************************************** Suspend **************************************/

    fun generateReceiptTextWa(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value?.filter { it.discountType!=DISCTYPE.CashbackNotPrinted })
        return textGenerator.generateReceiptTextWa()
    }
    fun generateReceiptTextNew(): String {

        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value?.filter { it.discountType!=DISCTYPE.CashbackNotPrinted })
        return textGenerator.generateReceiptTextNew()
    }

    /******************************************** Navigation**************************************/

    fun onBtnDiscClick(){
        _isDiskClicked.value=true
    }
    fun onBtnDiscClicked(){
        _isDiskClicked.value=false
    }

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