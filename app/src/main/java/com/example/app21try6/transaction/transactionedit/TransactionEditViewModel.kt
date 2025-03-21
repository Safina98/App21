package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
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

/*
val datasource1: TransSumDao,
    val datasource2: TransDetailDao,
    private val discountDao: DiscountDao,
    private val discountTransDao: DiscountTransDao,
    private val customerDao: CustomerDao,
    private val productDao:ProductDao,
 */
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


    val transDetailWithProduct= transRepo.getTransactionDetailsWithProductID(id)
    var discountTransactionList =  mutableListOf <DiscountTransaction>()

    //change item position on drag
    val updatePositionCallback: (List<TransactionDetail>) -> Unit = { updatedItems ->
        viewModelScope.launch {
            for ((index, item) in updatedItems.withIndex()) {
                item.item_position = index
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
    fun calculateDisc() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            val transactionSummary = mutableTransSum.value // Assume this is a valid TransactionSummary object
            val custId = mutableTransSum.value?.cust_name ?: "-1"
            val customerDeferred = async(Dispatchers.IO) { discountRepo.getCustomerByName(custId) }
            val discountList = withContext(Dispatchers.IO){discountRepo.getAllDicountNameList()}
            val customer = customerDeferred.await()

            val transDetailWithProductList = transRepo.getTransactionDetailsWithProductIDList(id)


            if (transDetailWithProductList==null || transactionSummary == null) {
               // Log.i("DiscProbs", "${transDetailWithProduct.value}")
                return@launch
            }
            // Group transactions by discountId once to avoid repeated grouping
            val groupedByProduct = transDetailWithProductList?.groupBy { it.discountId }
                ?: return@launch
           // Log.i("DiscProbs", "groupbyProduct  : ${transDetailWithProduct.value}")
            // Calculate the discount value for products that match the discount conditions
            val discountTransactions = discountList.mapNotNull { discount ->
                // Check location first to avoid unnecessary computations

                if (discount.custLocation?.lowercase() == customer?.customerLocation?.lowercase()||discount.custLocation==null) {
                    // Get the transaction details for the discount
                    val productTransactions = groupedByProduct[discount.discountId]
                    // If there are matching transactions for the discount, calculate the total quantity
                   // Log.i("DiscProbs", "groupby product $productTransactions")
                    if (productTransactions != null) {
                        val totalQty = productTransactions
                            .filter { it.transactionDetail.trans_price ==it.productPrice  }
                            .sumOf { it.transactionDetail.qty }

                        // Check if the total quantity meets the minimumQty condition
                        Log.i("DiscProbs", "total Qty:$totalQty, minQty=${discount.minimumQty}")
                        if (totalQty>= (discount.minimumQty ?: 0.0)) {
                            Log.i("DiscProbs", "min qty met")
                            val discountAppliedValue = productTransactions.sumOf { it.transactionDetail.qty * discount.discountValue }
                            DiscountTransaction(
                                discountId = discount.discountId,
                                discTransRef = UUID.randomUUID().toString(),
                                discTransName = discount.discountName,
                                sum_id = transactionSummary.sum_id,
                                discTransDate = transactionSummary.trans_date?:Date(),
                                discountAppliedValue = discountAppliedValue
                            )
                        } else {
                            Log.i("DiscProbs", "min qty not met")
                            null // No discount if minimumQty condition is not met
                        }
                    } else {
                       // Log.i("DiscProbs", "no matching discount for this transaction")
                        null // No matching transactions for this discount
                    }
                } else {
                   // Log.i("DiscProbs", "location doesnot match")
                    null // Location does not match
                }
            }
            val existingDiscountList= withContext(Dispatchers.IO){discountRepo.getDiscountTransactionList(id)}
            // Log the DiscountTransaction list
            discountTransactions.forEach { discountTransaction ->
                //Log.i("DiscProbs", "DiscountTransaction: $discountTransaction")
                val existingDiscount= discountRepo.selectExistingDiscount(discountTransaction.sum_id ,discountTransaction.discTransName)
                //Log.i("DiscProbs", "ExistingDiscount: $existingDiscount")
                if (existingDiscount==null){
                    discountRepo.insertTransactionDiscount(discountTransaction)
                }else{
                    existingDiscount.discountAppliedValue=discountTransaction.discountAppliedValue
                    discountRepo.updateTransactionDiscount(existingDiscount.discTransId,existingDiscount.discountAppliedValue)
                }
            }
            existingDiscountList?.let { existingList ->
                val itemsInFirstNotInSecond = existingList.filter { item1 ->
                    discountTransactions.none { item2 -> item1.discTransName == item2.discTransName }
                }
                discountTransactionList = discountTransactions.toMutableList().apply {
                    itemsInFirstNotInSecond.forEach { discountRepo.deleteTransactionDiscount(it.discTransId) }
                }
            }
            val totalDiscount=discountTransactionList.sumOf { it.discountAppliedValue }
            val transSumS: TransactionSummary = transRepo.getTransactionSummaryById(id)
            transSumS.total_after_discount = transSumS.total_trans-totalDiscount

            transRepo.updateTransactionSummary(transSumS)

            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            Log.i("DiscProbs", "Time taken to calculate discounts: $duration ms")
            _navigateToDetail.value=id
        }
    }




    //update transactionDetail qty and total price
    fun updateTransDetail(transactionDetail: TransactionDetail, i: Double){
        viewModelScope.launch {
            Log.i("modulo","${(transactionDetail.trans_price/1000)%2}")
            transactionDetail.qty = transactionDetail.qty + i
            if (transactionDetail.qty < 0.35) {
                transactionDetail.trans_price += 9000
            } else if (transactionDetail.qty < 0.9) {
                transactionDetail.trans_price += if ((transactionDetail.trans_price / 1000) % 2 == 0) 6000 else 5000
            }

            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty * transactionDetail.unit_qty

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
            transRepo.updateTransDetail(transactionDetail)
        }
    }
    // update Transaction Detail recyclerview price
    fun updateTransDetailItemPrice(transactionDetail: TransactionDetail, itemPrice: Int){
        viewModelScope.launch {
            transactionDetail.trans_price = itemPrice
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
           transRepo.updateTransDetail(transactionDetail)
        }
    }
    //update Transaction Detail unitQty
    fun updateUitQty(transactionDetail: TransactionDetail, unit_qyt: Double){
        viewModelScope.launch {
            transactionDetail.unit_qty = unit_qyt
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
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
