package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TransactionEditViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao,
    private val discountDao: DiscountDao,
    private val discountTransDao: DiscountTransDao,
    private val customerDao: CustomerDao,
    var id:Int
):AndroidViewModel(application){


    private val _isBtnBayarClicked = MutableLiveData<Boolean>()
    val isBtnBayarClicked: LiveData<Boolean> get() = _isBtnBayarClicked
    //Customer Name two way binding
    var custName  =MutableLiveData<String>("")
    var itemTransDetail = datasource2.selectATransDetail(id)
    val allCustomerTable = customerDao.allCustomer()

    val transSum = datasource1.getTransSum(id)
    //Mutable transaction summary for edit purpose
    var mutableTransSum = MutableLiveData<TransactionSummary> ()
    //get total transaction
    val totalSum = datasource2.getTotalTrans(id)
    //format total transaction
    val trans_total: LiveData<String> = Transformations.map(totalSum) { formatRupiah(it).toString() }
    //show or hide popupDialog
    private val _showDialog = MutableLiveData<TransactionDetail>()
    val showDialog:LiveData<TransactionDetail> get() = _showDialog
    //Navigation
    private val _navigateToVendible = MutableLiveData<Array<String>>()
    val navigateToVendible: LiveData<Array<String>> get() = _navigateToVendible
    private val _navigateToDetail = MutableLiveData<Int>()
    val navigateToDetail: LiveData<Int> get() = _navigateToDetail


    val transDetailWithProduct= datasource2.getTransactionDetailsWithProduct(id)
    var discountTransactionList =  mutableListOf <DiscountTransaction>()


    //change item position on drag
    val updatePositionCallback: (List<TransactionDetail>) -> Unit = { updatedItems ->
        viewModelScope.launch {
            for ((index, item) in updatedItems.withIndex()) {
                item.item_position = index
                Log.i("drag","viewModel ${item.trans_item_name} ${item.item_position}")
                _updateTransDetail(item)
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

    fun calculateDisc() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            val transactionSummary = mutableTransSum.value // Assume this is a valid TransactionSummary object
            val custId = mutableTransSum.value?.cust_name ?: "-1"
            val customerDeferred = async(Dispatchers.IO) { customerDao.getCustomerByName(custId) }
            val discountList = withContext(Dispatchers.IO){discountDao.getAllDiscountList()}
            val customer = customerDeferred.await()

            val transDetailWithProductList = withContext(Dispatchers.IO){ datasource2.getTransactionDetailsWithProductList(transactionSummary!!.sum_id) }

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
                        if (totalQty >= (discount.minimumQty ?: 0.0)) {
                            val discountAppliedValue = productTransactions.sumOf { it.transactionDetail.qty * discount.discountValue }
                            DiscountTransaction(
                                discountId = discount.discountId,
                                discTransRef = UUID.randomUUID().toString(),
                                discTransName = discount.discountName,
                                sum_id = transactionSummary.sum_id,
                                discTransDate = transactionSummary.trans_date,
                                discountAppliedValue = discountAppliedValue
                            )
                        } else {
                            Log.i("DiscProbs", "min qty not met")
                            null // No discount if minimumQty condition is not met
                        }
                    } else {
                        Log.i("DiscProbs", "no matching discount for this transaction")
                        null // No matching transactions for this discount
                    }
                } else {
                    Log.i("DiscProbs", "location doesnot match")
                    null // Location does not match
                }
            }
            val existingDiscountList= withContext(Dispatchers.IO){discountTransDao.selectDiscTransBySumId(id)}
            // Log the DiscountTransaction list
            discountTransactions.forEach { discountTransaction ->
                //Log.i("DiscProbs", "DiscountTransaction: $discountTransaction")
                val existingDiscount= withContext(Dispatchers.IO){discountTransDao.selectDiscTransBySumIdAndDiscName(discountTransaction.sum_id ,discountTransaction.discTransName)}
                //Log.i("DiscProbs", "ExistingDiscount: $existingDiscount")
                if (existingDiscount==null){
                    insertDiscountTransToDB(discountTransaction)
                }else{
                    existingDiscount.discountAppliedValue=discountTransaction.discountAppliedValue
                    updateDiscountTransToDB(existingDiscount)

                }
            }

            existingDiscountList?.let { existingList ->
                val itemsInFirstNotInSecond = existingList.filter { item1 ->
                    discountTransactions.none { item2 -> item1.discTransName == item2.discTransName }
                }
                discountTransactionList = discountTransactions.toMutableList().apply {
                    itemsInFirstNotInSecond.forEach { deleteDiscountTransToDB(it) }
                }
            }


            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            Log.i("DiscProbs", "Time taken to calculate discounts: $duration ms")
            _navigateToDetail.value=id
        }
    }

    private suspend fun getCustomerId(name:String):Int?{
        return withContext(Dispatchers.IO){
            customerDao.getIdByName(name)
        }
    }

    private suspend fun insertDiscountTransToDB(discList:List<DiscountTransaction>){
        withContext(Dispatchers.IO){
            discountTransDao.insertAll(discList)
        }
    }
    private suspend fun insertDiscountTransToDB(disc: DiscountTransaction){
        withContext(Dispatchers.IO){
            discountTransDao.insert(disc)
        }
    }
    private suspend fun updateDiscountTransToDB(disc: DiscountTransaction){
        withContext(Dispatchers.IO){
            discountTransDao.update(disc)
        }
    }
    private suspend fun deleteDiscountTransToDB(disc: DiscountTransaction){
        withContext(Dispatchers.IO){
            discountTransDao.delete(disc)
        }
    }


    //update transactionDetail qty and total price
    fun updateTransDetail(transactionDetail: TransactionDetail, i: Double){
        viewModelScope.launch {
            transactionDetail.qty = transactionDetail.qty + i
            transactionDetail.total_price = transactionDetail.trans_price*transactionDetail.qty*transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
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
                    transactionDetail.unit = selectedItem
                }
                _updateTransDetail(transactionDetail)
            }
        }
    }

    /////////////////////////////////Transaction Summary////////////////////////////
    //get custName value
    fun getCustomerName(id:Int){
        viewModelScope.launch {
            val customerName = withContext(Dispatchers.IO){datasource1.getCustName(id)}
            custName.value = customerName
        }
    }
    //get transaction summary
    fun setMutableTransSum(){
        viewModelScope.launch {
            val transactionSummary = withContext(Dispatchers.IO){datasource1.getTrans(id)}
            mutableTransSum.value = transactionSummary
            Log.i("CustNameProbs","setMutableTransSum transSumS ${transactionSummary}")
        }
    }

    //set trasactionSummary customername from editText
    fun setCustomerName(){
        transSum.value?.cust_name = custName.value ?: "-"
        mutableTransSum.value?.cust_name=custName.value ?: "-"
    }

    //update Transaction Detail recyclerview item name
    fun updateTransDetailItemName(transactionDetail: TransactionDetail, itemName: String){
        viewModelScope.launch {
            transactionDetail.trans_item_name = itemName
            _updateTransDetail(transactionDetail)
        }
    }
    // update Transaction Detail recyclerview price
    fun updateTransDetailItemPrice(transactionDetail: TransactionDetail, itemPrice: Int){
        viewModelScope.launch {
            transactionDetail.trans_price = itemPrice
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }
    }
    //update Transaction Detail unitQty
    fun updateUitQty(transactionDetail: TransactionDetail, unit_qyt: Double){
        viewModelScope.launch {
            transactionDetail.unit_qty = unit_qyt
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }

    }
    //update transaction Summary totalSum
    fun updateTotalSum(){
        viewModelScope.launch {
            val transSumS: TransactionSummary = datasource1.getTrans(id)
            transSumS.total_trans = totalSum.value ?: 0.0
            updateSum(transSumS)
            setMutableTransSum()
        }
    }
    //update transactionSUmmary CustomerName
    fun updateCustNameSum(){
        viewModelScope.launch {
            var transSumS: TransactionSummary = withContext(Dispatchers.IO){ datasource1.getTrans(id)}
            transSumS.cust_name=custName.value?: "-"
            Log.i("CustNameProbs","updateCustNameSum transSumS ${transSumS}")
            updateSum(transSumS)
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
            delete_(idm)
        }
    }

    ////suspend
    private suspend fun _updateTransDetail(transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){
            datasource2.update(transactionDetail)
        }
    }
    private suspend fun updateSum(transactionSummary: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transactionSummary)
        }
    }
    private suspend fun delete_(idm: Long){
        withContext(Dispatchers.IO){
            datasource2.deleteAnItemTransDetail(idm)
        }
    }
    ////////////////////////////////Navigation//////////////////////////////////////
    fun onNavigatetoDetail(idm:Int){
        viewModelScope.launch {
            val id = getCustomerId(custName.value?:"")
            mutableTransSum.value?.custId=id
            updateSum(mutableTransSum.value!!)
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
