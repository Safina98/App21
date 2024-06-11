package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.app21try6.database.*
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TransactionEditViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao,
    var id:Int
):AndroidViewModel(application){


    private val _isBtnBayarClicked = MutableLiveData<Boolean>()
    val isBtnBayarClicked: LiveData<Boolean> get() = _isBtnBayarClicked
    //Customer Name two way binding
    var custName  =MutableLiveData<String>("")
    var itemTransDetail = datasource2.selectATransDetail(id)

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
    //update transactionDetail qty and total price
    fun updateTransDetail(transactionDetail: TransactionDetail,i: Double){
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
            var customerName = withContext(Dispatchers.IO){datasource1.getCustName(id)}
            custName.value = customerName
        }
    }
    //get transaction summary
    fun setMutableTransSum(){
        viewModelScope.launch {
            var transactionSummary = withContext(Dispatchers.IO){datasource1.getTrans(id)}
            mutableTransSum.value = transactionSummary
        }
    }
    //set trasactionSummary customername from editText
    fun setCustomerName(){
        transSum.value?.cust_name = custName.value ?: "-"
    }
    //update Transaction Detail recyclerview item name
    fun updateTransDetailItemName(transactionDetail: TransactionDetail,itemName: String){
        viewModelScope.launch {
            transactionDetail.trans_item_name = itemName
            _updateTransDetail(transactionDetail)
        }
    }
    // update Transaction Detail recyclerview price
    fun updateTransDetailItemPrice(transactionDetail: TransactionDetail,itemPrice: Int){
        viewModelScope.launch {
            transactionDetail.trans_price = itemPrice
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }
    }
    //update Transaction Detail unitQty
    fun updateUitQty(transactionDetail: TransactionDetail,unit_qyt: Double){
        viewModelScope.launch {
            transactionDetail.unit_qty = unit_qyt
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }

    }
    //update transaction Summary totalSum
    fun updateTotalSum(){
        viewModelScope.launch {
            var transSumS: TransactionSummary = datasource1.getTrans(id)
            transSumS.total_trans = totalSum.value ?: 0.0
            updateSum(transSumS)
        }
    }
    //update transactionSUmmary CustomerName
    fun updateCustNameSum(){
        viewModelScope.launch {
            var transSumS: TransactionSummary = datasource1.getTrans(id)
            transSumS.cust_name = custName.value ?: ""
            updateSum(transSumS)
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
            updateSum(transSum.value!!)
            _navigateToDetail.value=id
        }
    }
    fun onNavigatetoVendible(){
        _navigateToVendible.value= arrayOf(transSum.value!!.sum_id.toString(),"-1")
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
