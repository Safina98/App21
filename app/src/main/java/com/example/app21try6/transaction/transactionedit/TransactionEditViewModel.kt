package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.app21try6.Constants
import com.example.app21try6.database.*
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val navigateToVendible: LiveData<Array<String>>
        get() = _navigateToVendible
    private val _navigateToDetail = MutableLiveData<Int>()
    val navigateToDetail: LiveData<Int>
        get() = _navigateToDetail
    private val _showDialog = MutableLiveData<TransactionDetail>()
    val showDialog:LiveData<TransactionDetail> get() = _showDialog

   var custName  =MutableLiveData<String>("")

    var itemTransDetail = datasource2.selectATransDetail(id)
    //val transSum = MutableLiveData<TransactionSummary>()
    val transSum = datasource1.getTransSum(id)
    val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    val currentDate = sdf.format(Date())
    val totalSum = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> =
            Transformations.map(totalSum) { formatRupiah(it).toString() }
    init {
        getCustName(id)
        //getTransactionSummary(id)
    }
    fun getCustName(id:Int){
        viewModelScope.launch {
            var customerName = withContext(Dispatchers.IO){datasource1.getCustName(id)}
            custName.value = customerName
            Log.e("SUMVM","transum in TransEditVm custName is "+custName.value+"")
        }
    }
    fun getTransactionSummary(id:Int){
        viewModelScope.launch {
            //var transactionSummary = withContext(Dispatchers.IO){datasource1.getTrans(id)}
            //transSum.value = transactionSummary
            Log.e("SUMVM","transum in TransEditVm transactionSUmmary is "+transSum.value.toString()+"")
        }
    }
    fun onNavigatetoVendible(){
        _navigateToVendible.value= arrayOf(transSum.value!!.sum_id.toString(),"1")
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoVendible(){
        _navigateToVendible.value =null
    }
    private suspend fun updateSum(transactionSummary: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transactionSummary)
        }
    }
    fun updateTransDetail(transactionDetail: TransactionDetail,i: Double){
        viewModelScope.launch {
            transactionDetail.qty = transactionDetail.qty + i
            transactionDetail.total_price = transactionDetail.trans_price*transactionDetail.qty
            _updateTransDetail(transactionDetail)
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
            _updateTransDetail(transactionDetail)
        }
    }
    suspend fun _updateTransDetail(transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){
            datasource2.update(transactionDetail)
        }
    }
    fun onNavigatetoDetail(idm:Int){
        viewModelScope.launch {
            // delete_()
            updateSum(transSum.value!!)
            _navigateToDetail.value=id
        }
    }
    fun saveCustName(){

            transSum.value?.cust_name = custName.value ?: "-"
            Log.e("SUMVM","transum in viewModel saveCustName transum is "+transSum.value.toString())
            Log.e("SUMVM","transum in viewModel saveCustName name is "+custName.value.toString()+"")
           // updateSum(transSum.value!!)
            Log.e("SUMVM","transum in viewModel navigate fun is "+transSum.value?.cust_name.toString()+"")

    }
    fun saveCustomerName(){
        viewModelScope.launch {

        }
    }
    fun updateTotalSum(){
        viewModelScope.launch {
            var transSumS: TransactionSummary = datasource1.getTrans(id)
            transSumS.total_trans = totalSum.value ?: 0.0
            updateSum(transSumS)

        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoDetail(){
        this._navigateToDetail.value =null
    }

    suspend fun delete_(idm: Int){
        withContext(Dispatchers.IO){
            datasource2.deleteAnItemTransDetail(idm)
        }
    }
    fun delete(idm: Int){
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
    fun onFramgentClosed(){
        viewModelScope.launch {
            saveCustName()
        }
    }
    fun onFragmentStart(){

    }

}
