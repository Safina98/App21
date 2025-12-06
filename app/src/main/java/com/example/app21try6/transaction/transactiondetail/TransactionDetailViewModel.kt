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
import com.example.app21try6.utils.TextGenerator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.app21try6.Constants
import com.example.app21try6.database.tables.MerchandiseRetailLog
import java.text.NumberFormat


class TransactionDetailViewModel (
    val stockRepo: StockRepositories,
    val bookRepo: BookkeepingRepository,
    val transRepo: TransactionsRepository,
    val discountRepo:DiscountRepository,
    application: Application,
    var id: Long):AndroidViewModel(application){

    val transDetail = transRepo.getTransactionDetails(id)
    val transSum = transRepo.getTransactionSummary(id)
    val transTotalDouble = transRepo.getTotalTransaction(id)
    val transTotal: LiveData<String> = transTotalDouble.map { formatRupiahVM(it).toString() }
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode
    private lateinit var textGenerator: TextGenerator

    private var _isDiskClicked=MutableLiveData<Boolean>()
    val isDiscClicked:LiveData<Boolean> get() = _isDiskClicked

    private val _multipleMerch = MutableLiveData<Pair<TransactionDetail?, Double?>>()
    val multipleMerch: LiveData<Pair<TransactionDetail?, Double?>> = _multipleMerch


    private var _pickNewItem=MutableLiveData<String?>()
    val pickNewItem:LiveData<String?> get() = _pickNewItem


    //todo update the selected value from repo
    private var _retailMerchList=MutableLiveData<List<DetailMerchandiseModel>?>()
    val retailMerchList:LiveData<List<DetailMerchandiseModel>?> get() = _retailMerchList

    private var merchSelectionDeferred:CompletableDeferred <Pair<List<DetailMerchandiseModel?>?, Double?>?>? = null

    private var transdetail=TransactionDetail()


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
            value = label + formatRupiahVM(abs(finalAmount)).toString()
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
    private val _navigateToEdit = MutableLiveData<Long>()
    val navigateToEdit: LiveData<Long> get() = _navigateToEdit

    private val _navigateToSubProduct= MutableLiveData<Array<String>?>()
    val navigateToSubProduct: LiveData<Array<String>?> get() = _navigateToSubProduct

    val discountTransBySumId=discountRepo.getTransactionDiscounts(id)
    //Set ui to change icons color when night mode or day mode on
    init {
        _multipleMerch.value=Pair(null,null)
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
                bayar.needsSyncs=1
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
                discount.tSCloudId =id
                discount.needsSyncs=1
                discount.dTCloudId= System.currentTimeMillis()
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
            transRepo.updateTransactionSummary(transSumS)
        }
    }


    private fun insertPayment(bayar: Payment){
        viewModelScope.launch {
            bayar.payment_date =Date()
            bayar.tSCloudId = transSum.value?.tSCloudId ?:-1
            bayar.payment_ref = UUID.randomUUID().toString()
            bayar.paymentCloudId=System.currentTimeMillis()
            bayar.needsSyncs=1
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
        bayar.tSCloudId = transSum.value?.tSCloudId ?:-1
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

            transdetail=item//transDetail untuk update retail net
            val subId=item.sub_id
            val retailList = stockRepo.selectRetailBySumId(subId ?: -1)
            val totalNet = retailList?.sumOf { it.net }
            var trans=item.copy()
            var unitQty:Double=0.0
            if (trans.unit==Constants.ITEMUNIT.LSN) {
                trans = trans.copy(unit_qty = trans.unit_qty*12)
            }

            val totalQty= trans.qty*trans.unit_qty
            if (item.is_cutted==false){
                if (totalQty <= (totalNet?:0.0)){
                    if (trans.unit== null || trans.unit==Constants.ITEMUNIT.LSN || trans.unit==Constants.ITEMUNIT.ROLL){
                        _multipleMerch.value = Pair(
                            item.copy(),
                            if (item.unit == Constants.ITEMUNIT.LSN) item.qty * 12
                            else if( item.unit==Constants.ITEMUNIT.ROLL) item.qty*item.unit_qty
                            else item.qty
                        )
                        _retailMerchList.value = retailList!!.toList()
                        val merchAndExtra=waitForMerchSelection()
                        val (merch,extra) = merchAndExtra?: Pair(null, null)
                        val merchandiseRetailList = merch.toMerchandiseRetailList()
                        updateMerchValue(merchandiseRetailList,totalQty,item.copy(),extra)
                    }
                }else{
                    //pop up dialog
                    val detailWarnaList = stockRepo.getDetailWarnaList(subId?:-1)
                    _pickNewItem.value=trans.trans_item_name
                    _retailMerchList.value = detailWarnaList!!.toList()
                    val detailAndExtra=waitForMerchSelection()
                    val (detail,exta) = detailAndExtra ?: Pair(null,null)
                    trackDetailWarna(detail,item)
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
    fun DetailMerchandiseModel.toMerchandiseRetail():MerchandiseRetail{
        return this.let { model->
            MerchandiseRetail(
                id=model.id,
                sub_id=model.sub_id,
                ref=model.ref,
                net = model.net,
                date = model.date?: Date()
            )
        }
    }
    fun updateMerchNet(model:DetailMerchandiseModel){
        viewModelScope.launch {
            val merc=model.toMerchandiseRetail()
            stockRepo.updateDetailRetail(merc)
            Log.i("multipleMerch","updateMerchNet ${_multipleMerch.value?.first}")
            updateRetailOnClick(transdetail)
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
                    merch.needsSyncs=1
                    trans.needsSyncs=1
                    stockRepo.updateDetaiAndlRetail(merch,trans)
                }
            }
        }
    }

    fun createRetailLog(merchandiseRetail: MerchandiseRetail, remainingQty: Double, trans: TransactionDetail):MerchandiseRetailLog{
            val retailLog=MerchandiseRetailLog()
            retailLog.retailId=merchandiseRetail.id
            retailLog.transDetailId=trans.tDCloudId
            retailLog.ket="Pembelian"
            retailLog.date=Date()
            retailLog.subId=trans.sub_id!!
            retailLog.customer=transSum.value?.cust_name ?: ""
            retailLog.cutCount = merchandiseRetail.cutCount+1
            retailLog.detailWarnaNet=merchandiseRetail.initialNet
            retailLog.previousNet=merchandiseRetail.net
            retailLog.transDetailQty=trans.qty
            retailLog.substractedWith=if(remainingQty>=merchandiseRetail.net) merchandiseRetail.net else remainingQty
        return retailLog
    }

    fun trackDetailWarna(detailWarnaModelList: List<DetailMerchandiseModel?>?,trans: TransactionDetail){
        viewModelScope.launch {
            detailWarnaModelList?.forEach {detailWarnaModel->
                if (detailWarnaModel!=null){
                    val detailWarnaTable=detailWarnaModel.toDetailWarnaTable()
                    detailWarnaTable.batchCount -=detailWarnaModel.selectedQty
                    val productId = stockRepo.getProdutId(detailWarnaTable.subId) ?: return@launch
                    val brandId = stockRepo.getBrandId(productId) ?: return@launch
                    val inventoryLog =createInventoryLog(detailWarnaTable,detailWarnaTable.batchCount,productId,brandId,"Keluar Retail")
                    val merchandiseRetailList= mutableListOf<MerchandiseRetail?>()
                    for (i in 0 until detailWarnaModel!!.selectedQty) {
                        // i goes from 0 until selectedQty - 1
                        Log.i("DWP","$i")
                        val merchandiseRetail = detailWarnaModel?.let { createMerchandiseRetail(it) }
                        merchandiseRetailList.add(merchandiseRetail)
                    }
                    //val merchandiseRetail = detailWarnaModel?.let { createMerchandiseRetail(it) }
                    if (detailWarnaTable.batchCount>0)
                        stockRepo.updateDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetailList)
                    else
                        stockRepo.deleteDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetailList)
                }
                updateRetailOnClick(trans)
            }
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
    fun createInventoryLog(detailWarnaTable: DetailWarnaTable, batchCount:Double, productId: Long, brandId: Long, ket:String): InventoryLog {
        val inventoryLog= InventoryLog()
        inventoryLog.detailWarnaRef=detailWarnaTable.ref
        inventoryLog.subProductId=detailWarnaTable.subId
        inventoryLog.isi=detailWarnaTable.net
        inventoryLog.pcs=batchCount.toInt()
        inventoryLog.barangLogKet=ket
        inventoryLog.barangLogDate= Date()
        inventoryLog.barangLogRef=UUID.randomUUID().toString()
        inventoryLog.productCloudId =productId
        inventoryLog.brandId=brandId
        return inventoryLog
    }

    fun createMerchandiseRetail(dmModel: DetailMerchandiseModel):MerchandiseRetail{
        val merchandiseRetail=MerchandiseRetail()
        merchandiseRetail.sub_id=dmModel.sub_id
        merchandiseRetail.net=dmModel.net
        merchandiseRetail.ref=UUID.randomUUID().toString()
        merchandiseRetail.date=dmModel.date?:Date()
        merchandiseRetail.mRCloudId=System.currentTimeMillis()
        return merchandiseRetail
    }
    fun setValuesToNull(){
        _retailMerchList.value=null
        _multipleMerch.value=Pair(null,null)
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
                        productCloudId = product?.productCloudId
                        price = it.trans_price.toDouble()
                        total_income = it.total_price
                        product_capital = (product?.product_capital ?: 0.0).toInt()
                        needsSyncs=1
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
                summary.needsSyncs=1
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
            transdetail.needsSyncs=1
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

    fun onNavigateToSubProduct(){
        viewModelScope.launch {
            val subId=transdetail.sub_id
            val productId=stockRepo.getProdutId(subId?:0)
            val brandId=stockRepo.getBrandId(productId)
            val transDetailId=transdetail.tDCloudId
            val ctgId=stockRepo.getCategoryIdByBrandId(brandId)
            val productName=stockRepo.getProductById(productId?:0).product_name
            val stringArray=arrayOf(productId.toString(),brandId.toString(),ctgId.toString(),transDetailId.toString(),productName,subId.toString())
            _navigateToSubProduct.value=stringArray
        }
    }
    fun onNavigatedToSubProduct(){
        _navigateToSubProduct.value=null
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
    fun formatRupiahVM(number: Double?): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        formatRupiah.maximumFractionDigits = 0
        return if (number != null) {
            formatRupiah.format(number)
        } else {
            formatRupiah.format(0.0) // or any default value you prefer
        }
    }
}