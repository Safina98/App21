package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.app21try6.Constants
import com.example.app21try6.database.*
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TransactionEditViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao,
    var id:Int
):AndroidViewModel(application){
    //Navigation
    private val _navigateToVendible = MutableLiveData<Array<String>>()
    val navigateToVendible: LiveData<Array<String>> get() = _navigateToVendible

    private val _isBtnBayarClicked = MutableLiveData<Boolean>()
    val isBtnBayarClicked: LiveData<Boolean> get() = _isBtnBayarClicked

    private val _navigateToDetail = MutableLiveData<Int>()
    val navigateToDetail: LiveData<Int> get() = _navigateToDetail
    private val _showDialog = MutableLiveData<TransactionDetail>()
    val showDialog:LiveData<TransactionDetail> get() = _showDialog

   var custName  =MutableLiveData<String>("")

    var itemTransDetail = datasource2.selectATransDetail(id)
    //val transSum = MutableLiveData<TransactionSummary>()
    val transSum = datasource1.getTransSum(id)

    var mutableTransSum = MutableLiveData<TransactionSummary> ()
    val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    val currentDate = sdf.format(Date())
    val totalSum = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> =
            Transformations.map(totalSum) { formatRupiah(it).toString() }

    val updatePositionCallback: (List<TransactionDetail>) -> Unit = { updatedItems ->
        // Update the positions in the database
        viewModelScope.launch {
            for ((index, item) in updatedItems.withIndex()) {
                item.item_position = index
                Log.i("drag","viewModel ${item.trans_item_name} ${item.item_position}")
                //datasource2.updateItemPosition(item)
                _updateTransDetail(item)
            }
        }
    }
    init {
        setCustomerName(id)
        setMutableTransSum()


        //getTransactionSummary(id)
    }

    ////////////////////////////////Transacttion Detail/////////////////////////////

    fun updateTransDetail(transactionDetail: TransactionDetail,i: Double){
        viewModelScope.launch {
            transactionDetail.qty = transactionDetail.qty + i
            transactionDetail.total_price = transactionDetail.trans_price*transactionDetail.qty*transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }
    }
    fun updateUnitTransDetail(selectedItem:String?,transactionDetail: TransactionDetail){
        viewModelScope.launch {

            if (transactionDetail.unit!=selectedItem){
                transactionDetail.unit = if(selectedItem =="NONE") null else selectedItem
                _updateTransDetail(transactionDetail)
                Log.i("unit_Spinner","viewModel ${transactionDetail}")
            }
        }
    }
    fun updateTransPosition(transList:List<TransactionDetail>){
        viewModelScope.launch {
            for ((index, item) in transList.withIndex()) {
                // Assuming you have a method to update the item position in your database
                item.item_position = index
                _updateTransDetail(item)
            }
        }
    }

    /////////////////////////////////Transaction Summary////////////////////////////
    fun setCustomerName(id:Int){
        viewModelScope.launch {
            var customerName = withContext(Dispatchers.IO){datasource1.getCustName(id)}
            custName.value = customerName
            Log.e("SUMVM","transum in TransEditVm custName is "+custName.value+"")
        }
    }
    fun setMutableTransSum(){
        viewModelScope.launch {
            var transactionSummary = withContext(Dispatchers.IO){datasource1.getTrans(id)}
            mutableTransSum.value = transactionSummary
        }
    }
    fun onNavigatetoDetail(idm:Int){
        viewModelScope.launch {
            updateSum(transSum.value!!)
            _navigateToDetail.value=id
        }
    }
    fun setCustomerName(){
        transSum.value?.cust_name = custName.value ?: "-"
    }
    fun bayar(bayar : Int){
        viewModelScope.launch {
            transSum.value?.paid =bayar
            mutableTransSum.value?.paid = bayar
            Log.i("PROBBayar","mutable "+mutableTransSum.value.toString())
            Log.i("PROBBayar","live "+transSum.value.toString())
            mutableTransSum.value?.let { updateSum(it) }
        }
    }

    ////Suspend
    private suspend fun updateSum(transactionSummary: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transactionSummary)
        }
    }
    fun updateTransDetailItemName(transactionDetail: TransactionDetail,itemName: String){
        viewModelScope.launch {
            transactionDetail.trans_item_name = itemName
            _updateTransDetail(transactionDetail)
        }
    }
    fun updateTransDetailItemPrice(transactionDetail: TransactionDetail,itemPrice: Int){
        viewModelScope.launch {
            transactionDetail.trans_price = itemPrice
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }
    }
    fun updateUitQty(transactionDetail: TransactionDetail,unit_qyt: Double){
        viewModelScope.launch {
            transactionDetail.unit_qty = unit_qyt
            transactionDetail.total_price = transactionDetail.trans_price * transactionDetail.qty *transactionDetail.unit_qty
            _updateTransDetail(transactionDetail)
        }

    }
    fun updateTotalSum(){
        viewModelScope.launch {
            var transSumS: TransactionSummary = datasource1.getTrans(id)
            transSumS.total_trans = totalSum.value ?: 0.0
            updateSum(transSumS)
        }
    }
    fun onBtnBayarClick(){
        _isBtnBayarClicked.value = true
    }
    fun onBtnBayarClicked(){
        _isBtnBayarClicked.value = false
    }
    ////suspend
    private suspend fun _updateTransDetail(transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){
            datasource2.update(transactionDetail)
        }
    }
    ////////////////////////////////Navigation//////////////////////////////////////

    fun onNavigatetoVendible(){
        _navigateToVendible.value= arrayOf(transSum.value!!.sum_id.toString(),"-1")
        Log.i("SUMIDPROB","TransactionEditViewModel onnavigateTotransactionProduct $_navigateToVendible")
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoVendible(){
        _navigateToVendible.value =null
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoDetail(){
        this._navigateToDetail.value =null
    }

    private suspend fun delete_(idm: Long){
        withContext(Dispatchers.IO){
            datasource2.deleteAnItemTransDetail(idm)
        }
    }
    fun delete(idm: Long){
        viewModelScope.launch {
          delete_(idm)
        }
    }
    fun onShowDialog(transactionDetail: TransactionDetail){
        _showDialog.value = transactionDetail
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onCloseDialog(){
        _showDialog.value = null
    }

    fun onFragmentStart(){

    }

}
