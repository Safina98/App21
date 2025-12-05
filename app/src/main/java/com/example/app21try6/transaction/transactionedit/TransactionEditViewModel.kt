package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.app21try6.calculatePriceByQty
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class TransactionEditViewModel(
    private val stockRepo:StockRepositories,
    private val transRepo:TransactionsRepository,
    private val discountRepo:DiscountRepository,
    application: Application,
    var id:Int
):AndroidViewModel(application){


    private val _isBtnBayarClicked = MutableLiveData<Boolean>()
    val isBtnBayarClicked: LiveData<Boolean> get() = _isBtnBayarClicked
    //Customer Name two way binding
    var custName  =MutableLiveData<String>("")
    var itemTransDetail = transRepo.getTransactionDetails(id)
    val allCustomerTable = discountRepo.getAllCustomers()

    val transSum = transRepo.getTransactionSummary(id)
    //Mutable transaction summary for edit purpose
    var mutableTransSum = MutableLiveData<TransactionSummary> ()
    //get total transaction
    val totalSum = transRepo.getTotalTransaction(id)
    //format total transaction
    val trans_total: LiveData<String> = totalSum.map {   formatRupiah(it).toString() }
    //show or hide popupDialog
    private val _showDialog = MutableLiveData<TransactionDetail>()
    val showDialog:LiveData<TransactionDetail> get() = _showDialog
    //Navigation
    private val _navigateToVendible = MutableLiveData<Array<String>>()
    val navigateToVendible: LiveData<Array<String>> get() = _navigateToVendible
    private val _navigateToDetail = MutableLiveData<Int>()
    val navigateToDetail: LiveData<Int> get() = _navigateToDetail



    var discountTransactionList =  mutableListOf <DiscountTransaction>()

    //change item position on drag
    val updatePositionCallback: (List<TransactionDetail>) -> Unit = { updatedItems ->
        viewModelScope.launch {
            for ((index, item) in updatedItems.withIndex()) {
                item.item_position = index
                item.needsSyncs=1
                Log.i("drag","viewModel ${item.trans_item_name} ${item.item_position}")
                transRepo.updateTransDetail(item)
            }
        }
    }

    init {
        getCustomerName(id)
        setMutableTransSum()
    }

    ////////////////////////////////Transacttion Detail/////////////////////////////

    fun discountCustomer(){
        viewModelScope.launch {

        }
    }

    //Calculate discount
    private suspend fun calculateDisc() {
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val transactionSummary = mutableTransSum.value
            val custId = mutableTransSum.value?.cust_name ?: "-1"
            val customer = discountRepo.getCustomerByName(custId)
            val discountList = discountRepo.getAllDicountNameList()
            val transDetailWithProductList = transRepo.getTransactionDetailsWithProductIDList(id)

            if (transDetailWithProductList == null || transactionSummary == null) {
                return@withContext
            }

            val groupedByProduct = transDetailWithProductList.groupBy { it.discountId }

            val discountTransactions = discountList.mapNotNull { discount ->
                if (discount.custLocation?.lowercase() == customer?.customerLocation?.lowercase() || discount.custLocation == null) {
                    val productTransactions = groupedByProduct[discount.discountId]
                    productTransactions?.let {
                        val totalQty = it.filter { trans -> trans.transactionDetail.trans_price == trans.productPrice }
                            .sumOf { trans -> trans.transactionDetail.qty }

                        if (totalQty >= (discount.minimumQty ?: 0.0)) {
                            val discountAppliedValue = it.sumOf { trans -> trans.transactionDetail.qty * discount.discountValue }
                            DiscountTransaction(
                                discountId = discount.discountId,
                                discTransRef = UUID.randomUUID().toString(),
                                discTransName = discount.discountName,
                                sum_id = transactionSummary.sum_id,
                                discTransDate = transactionSummary.trans_date ?: Date(),
                                discountAppliedValue = discountAppliedValue
                            )
                        } else null
                    }
                } else null
            }

            val existingDiscountList = discountRepo.getDiscountTransactionList(id)
            discountTransactions.forEach { discountTransaction ->
                val existingDiscount = discountRepo.selectExistingDiscount(discountTransaction.sum_id, discountTransaction.discTransName)
                if (existingDiscount == null) {
                    discountTransaction.dTCloudId=System.currentTimeMillis()
                    discountRepo.insertTransactionDiscount(discountTransaction)
                } else {
                    existingDiscount.discountAppliedValue = discountTransaction.discountAppliedValue
                    existingDiscount.needsSyncs=1
                    discountRepo.updateTransactionDiscount(existingDiscount.discTransId, existingDiscount.discountAppliedValue)
                }
            }
            existingDiscountList?.let { existingList ->
                val itemsToDelete = existingList.filter { existing ->
                    discountTransactions.none { new -> existing.discTransName == new.discTransName }
                }
                itemsToDelete.forEach {
                    if (it.discountId!=null) discountRepo.deleteTransactionDiscount(it.discTransId)
                }
            }

            val totalDiscount = discountTransactions.sumOf { it.discountAppliedValue }
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            val totalTrans:Double=transRepo.getTotalTransactionn(id)
            transSumS.total_after_discount = totalTrans - totalDiscount
            transSumS.total_trans=totalTrans
            transRepo.updateTransactionSummary(transSumS)
            Log.i("DiscProbs", "discouont trans total ${formatRupiah(transSumS.total_trans)}")
            Log.i("DiscProbs", "total after discount ${formatRupiah(transSumS.total_after_discount)}")
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            Log.i("DiscProbs", "Time taken to calculate discounts: $duration ms")
        }
    }

    fun calculateDiscA(){
        viewModelScope.launch {
            calculateDisc()
        }
    }

    fun navigatetoDetail(){
        _navigateToDetail.value=id
    }

    //update transactionDetail qty and total price
    fun updateTransDetail(transactionDetail: TransactionDetail, i: Double){
        viewModelScope.launch {
            transactionDetail.qty = transactionDetail.qty + i
            val product=stockRepo.getProductBySubId(transactionDetail.sub_id?:0)
            transactionDetail.trans_price= calculatePriceByQty(transactionDetail.qty,product!!.product_price)
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty * transactionDetail.unit_qty
            transactionDetail.needsSyncs=1
            transRepo.updateTransDetail(transactionDetail)
        }
    }

    //update unit on radio button click
    fun updateUnitTransDetail(selectedItem:String?,transactionDetail: TransactionDetail){
        viewModelScope.launch {
            if (transactionDetail.unit!=selectedItem){
                if (selectedItem=="NONE"){
                    transactionDetail.unit = null
                    updateUitQty(transactionDetail,1.0)
                }
                else {
                    //get product by id
                    val product = stockRepo.getProductBySubId(transactionDetail.sub_id?:-1)
                    if (selectedItem=="ROLL") {
                        transactionDetail.unit_qty=product?.default_net?:1.0

                    }
                    transactionDetail.trans_price=product?.alternate_price?.toInt()?:0
                    transactionDetail.total_price=transactionDetail.trans_price*transactionDetail.unit_qty*transactionDetail.qty
                    transactionDetail.unit = selectedItem
                }
                transactionDetail.needsSyncs=1
                transRepo.updateTransDetail(transactionDetail)
            }
        }
    }
    fun updateDate(){
        viewModelScope.launch {
            if (transSum.value!=null) {
                val transsummary = transSum.value!!
                transsummary.trans_date = Date()
                transRepo.updateTransactionSummary(transsummary)
                setMutableTransSum()
            }
        }
    }

    /////////////////////////////////Transaction Summary////////////////////////////
    //get custName value
    fun getCustomerName(idm:Int){
        viewModelScope.launch {
            val customerName = transRepo.getTransactionSummaryById(id).cust_name
            custName.value = customerName
        }
    }
    //get transaction summary
    fun setMutableTransSum(){
        viewModelScope.launch {
            val transactionSummary = transRepo.getTransactionSummaryById(id)
            mutableTransSum.value = transactionSummary
        }
    }
    //set trasactionSummary customername from editText

    //update Transaction Detail recyclerview item name
    fun updateTransDetailItemName(transactionDetail: TransactionDetail, itemName: String){
        viewModelScope.launch {
            transactionDetail.trans_item_name = itemName
            transactionDetail.needsSyncs=1
            transRepo.updateTransDetail(transactionDetail)
        }
    }
    // update Transaction Detail recyclerview price
    fun updateTransDetailItemPrice(transactionDetail: TransactionDetail, itemPrice: Int){
        viewModelScope.launch {
            transactionDetail.trans_price = itemPrice
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            transactionDetail.needsSyncs=1
           transRepo.updateTransDetail(transactionDetail)
        }
    }
    //update Transaction Detail unitQty
    fun updateUitQty(transactionDetail: TransactionDetail, unit_qyt: Double){
        viewModelScope.launch {
            transactionDetail.unit_qty = unit_qyt
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            transactionDetail.needsSyncs=1
            transRepo.updateTransDetail(transactionDetail)
        }

    }
    //update transaction Summary totalSum
    fun updateTotalSum(){
        viewModelScope.launch {
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            transSumS.total_trans = totalSum.value ?: 0.0
            transRepo.updateTransactionSummary(transSumS)
            setMutableTransSum()
        }
    }
    fun updateTotalAfterDiscountSum(totalDiscount:Double){
        viewModelScope.launch {
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            transSumS.total_after_discount = transSumS.total_trans-totalDiscount
            Log.i("DiscProbs","total trans: ${transSumS.total_trans}")
            Log.i("DiscProbs","diskon: ${totalDiscount}")
            Log.i("DiscProbs","total after discount: ${transSumS.total_after_discount}")

            transRepo.updateTransactionSummary(transSumS)
            setMutableTransSum()
        }
    }
    //update transactionSUmmary CustomerName
    fun updateCustNameSum(){
        viewModelScope.launch {
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            val id = discountRepo.getCustomerId(custName.value?:"")
            transSumS.cust_name=custName.value?: "-"
            transSumS.custId=id
            mutableTransSum.value?.custId=id
            transRepo.updateTransactionSummary(transSumS)
            setMutableTransSum()
        }
    }
    //show or hide dialog
    fun onShowDialog(transactionDetail: TransactionDetail){
        _showDialog.value = transactionDetail
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onCloseDialog(){
        _showDialog.value = null
    }
    // delete transaction Detail item
    fun delete(idm: Long){
        viewModelScope.launch {
           transRepo.deleteTransDetail(idm)
        }
    }

    ////suspend

    ////////////////////////////////Navigation//////////////////////////////////////
    fun onNavigatetoDetail(idm:Int){
        viewModelScope.launch {
            transRepo.updateTransactionSummary(mutableTransSum.value!!)
            calculateDisc()
            navigatetoDetail()
           // _navigateToDetail.value=id
        }
    }
    fun onNavigatetoVendible(){
        _navigateToVendible.value= arrayOf(mutableTransSum.value!!.sum_id.toString(),"-1")
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoVendible(){
        _navigateToVendible.value =null
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoDetail(){
        this._navigateToDetail.value =null
    }
    
}
