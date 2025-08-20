package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.tables.Payment

import com.example.app21try6.database.tables.Summary
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.InventoryLog
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
import com.example.app21try6.Constants
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
    //val transDetailWithProduct= transRepo.getTransactionDetailsWithProductID(id)
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

    private var _multipleMerch=MutableLiveData<TransactionDetail?>()
    val multipleMerch:LiveData<TransactionDetail?> get() = _multipleMerch

    private var _pickNewItem=MutableLiveData<String?>()
    val pickNewItem:LiveData<String?> get() = _pickNewItem

    private var _detailWarnaList=MutableLiveData<List<DetailMerchandiseModel>>()
    val detailWarnaList:LiveData<List<DetailMerchandiseModel>> get() = _detailWarnaList
    //todo update the selected value from repo
    private var _retailMerchList=MutableLiveData<List<DetailMerchandiseModel>?>()
    val retailMerchList:LiveData<List<DetailMerchandiseModel>?> get() = _retailMerchList

    //private var merchSelectionDeferred: CompletableDeferred<List<MerchandiseRetail?>?>? = null
    private var merchSelectionDeferred:CompletableDeferred <Pair<List<DetailMerchandiseModel?>?, Double?>?>? = null
    private var longMerchSelectionDeferred: CompletableDeferred<Pair<List<MerchandiseRetail?>?, Double?>?>? = null

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


    suspend fun waitForMerchSelection(): Pair<List<DetailMerchandiseModel?>?, Double?>? {
        merchSelectionDeferred = CompletableDeferred()
        return merchSelectionDeferred?.await()
    }

    fun onMerchSelected(merch: List<DetailMerchandiseModel?>?, extra: Double) {
        merchSelectionDeferred?.complete(Pair(merch, extra))
        merchSelectionDeferred = null
    }

    fun updateRetailOnClick(item:TransactionDetail){
        viewModelScope.launch {
            val subId=item.sub_id
            val retailList = stockRepo.selectRetailBySumId(subId ?: -1)
            val totalNet = retailList?.sumOf { it.net }
            val trans=item
            if (item.is_cutted==false){
                if (trans.qty <= (totalNet?:0.0)){
                    var newItem:TransactionDetail
                    newItem=item
                    if (trans.unit== null){
                        _multipleMerch.value = newItem
                        _retailMerchList.value = retailList!!.toList()
                        val merchAndExtra=waitForMerchSelection()
                        val (merch,extra) = merchAndExtra?: Pair(null, null)
                        val merchandiseRetailList = merch.toMerchandiseRetailList()
                        updateMerchValue(merchandiseRetailList,trans.qty,trans,extra)
                    }else if(trans.unit== Constants.ITEMUNIT.LSN) {
                      newItem.qty=item.qty*12
                    }else {
                        //get default qty from db
                        //trans detail pop up, with remaining
                        val detailWarnaList = stockRepo.getDetailWarnaList(subId?:-1)
                        _pickNewItem.value=trans.trans_item_name
                        _retailMerchList.value = detailWarnaList!!.toList()


                    }


                }else{
                //pop up dialog
                    val detailWarnaList = stockRepo.getDetailWarnaList(subId?:-1)
                    _pickNewItem.value=trans.trans_item_name
                    _retailMerchList.value = detailWarnaList!!.toList()
                    val detailAndExtra=waitForMerchSelection()
                    val (detail,exta) = detailAndExtra ?: Pair(null,null)
                    trackDetailWarna(detail?.get(0),trans)
                Toast.makeText(getApplication(),"Net tidak cukup",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun List<DetailMerchandiseModel?>?.toMerchandiseRetailList(): List<MerchandiseRetail?>? {
        return this?.map { detail ->
            detail?.id?.let {
                MerchandiseRetail(
                    id = it,
                    sub_id = detail.sub_id,
                    ref = detail.ref,
                    net = detail.net,
                    date = detail.date ?: Date() // Fallback to current date if null
                )
            }
        }
    }
    fun updateMerchValue(merchList:List<MerchandiseRetail?>?,qty:Double, trans: TransactionDetail,extra:Double?){
        viewModelScope.launch {
            var remainingQty=qty+ (extra?:0.0)
            if (merchList!=null){
                trans.is_cutted=true
                merchList.sortedBy { it!!.net }.forEach { merch->
                    if ((remainingQty-merch!!.net)>=0){
                        remainingQty=remainingQty  - merch.net
                        merch.net=0.0
                    }else{
                        merch.net=merch.net-remainingQty
                        remainingQty=-1.0
                    }
                    stockRepo.updateDetaiAndlRetail(merch,trans)
                    Log.i("MERCHPROBS","id: ${merch.id} net ${merch.net}, remainingqty: $remainingQty")
                }
            }
        }
    }

    fun trackDetailWarna(detailWarnaModel: DetailMerchandiseModel?,trans: TransactionDetail){
        viewModelScope.launch {
            if (detailWarnaModel!=null){
                val detailWarnaTable=detailWarnaModel.toDetailWarnaTable()
                detailWarnaTable.batchCount -=1
                val productId = stockRepo.getProdutId(detailWarnaTable.subId) ?: return@launch
                val brandId = stockRepo.getBrandId(productId) ?: return@launch
                val inventoryLog =createInventoryLog(detailWarnaTable,detailWarnaTable.batchCount,productId,brandId,"Keluar Retail")
                val merchandiseRetail= detailWarnaModel?.let { createMerchandiseRetail(it) }
                if (detailWarnaTable.batchCount>0)
                    stockRepo.updateDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetail)
                else
                    stockRepo.deleteDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetail)
                }
            updateRetailOnClick(trans)
            }


    }
    fun DetailMerchandiseModel.toDetailWarnaTable(): DetailWarnaTable {
        return DetailWarnaTable(
            id = this.id,
            subId = this.sub_id,
            ref = this.ref,
            net = this.net,
            batchCount = this.batchCount ?: 0.0, // Handle null with default value
            ket = this.ket ?: "", // Handle null with empty string
            // Note: date is not part of DetailWarnaTable so it's omitted
        )
    }
    fun createInventoryLog(detailWarnaTable: DetailWarnaTable,batchCount:Double,productId:Int,brandId:Int,ket:String): InventoryLog {
        val inventoryLog= InventoryLog()
        inventoryLog.detailWarnaRef=detailWarnaTable.ref
        inventoryLog.subProductId=detailWarnaTable.subId
        inventoryLog.isi=detailWarnaTable.net
        inventoryLog.pcs=batchCount.toInt()
        inventoryLog.barangLogKet=ket
        inventoryLog.barangLogDate= Date()
        inventoryLog.barangLogRef=UUID.randomUUID().toString()
        inventoryLog.productId=productId
        inventoryLog.brandId=brandId
        return inventoryLog
    }

    fun createMerchandiseRetail(detailWarnaTable: DetailMerchandiseModel):MerchandiseRetail{
        Log.i("Check","${detailWarnaTable.id}")
        val merchandiseRetail=MerchandiseRetail()
        merchandiseRetail.sub_id=detailWarnaTable.sub_id
        merchandiseRetail.net=detailWarnaTable.net
        merchandiseRetail.ref=UUID.randomUUID().toString()
        merchandiseRetail.date=detailWarnaTable.date?:Date()
        return merchandiseRetail
    }
    fun setValuesToNull(){
        _retailMerchList.value=null
        _multipleMerch.value=null
        _pickNewItem.value=null
    }
    fun setPickNewItemToNull(){
        _pickNewItem.value=null
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
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value?.filter { it.discountType!=Constants.DISCTYPE.CASHBACK_NOT_PRINTED })
        return textGenerator.generateReceiptTextWa()
    }

    fun generateReceiptTextNew(): String {
        textGenerator = TextGenerator(transDetail.value,transSum.value,paymentModel.value,discountTransBySumId.value?.filter { it.discountType!=Constants.DISCTYPE.CASHBACK_NOT_PRINTED })
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