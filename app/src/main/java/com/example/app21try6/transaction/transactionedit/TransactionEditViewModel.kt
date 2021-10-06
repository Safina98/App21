package com.example.app21try6.transaction.transactionedit

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.app21try6.Constants
import com.example.app21try6.database.*
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
    private val _navigateToVendible = MutableLiveData<Array<String>>()
    val navigateToVendible: LiveData<Array<String>>
        get() = _navigateToVendible
    private val _navigateToDetail = MutableLiveData<Int>()
    val navigateToDetail: LiveData<Int>
        get() = _navigateToDetail
   var custName  =MutableLiveData<String>()
    val itemTransDetail = datasource2.selectATransDetail(id)
    val transSum = MutableLiveData<TransactionSummary>()
    val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    val currentDate = sdf.format(Date())
    private val _showDialog = MutableLiveData<TransactionDetail>()
    val showDialog:LiveData<TransactionDetail> get() = _showDialog

    init {
        viewModelScope.launch {
            if (id==-1){
                initiateNewId()
                id=  datasource1.getLastInsertedId() ?: return@launch
            }
            custName.value = datasource1.getCustName(id)  ?: return@launch
           // itemTransDetail = datasource2.selectATransDetail(id)
            transSum.value = datasource1.getTrans(id)
        }
    }
    fun initiateNewId(){
        viewModelScope.launch {
          var trans =   TransactionSummary()
            trans.trans_date = currentDate
         insertNewSum(trans)
             //id=  datasource1.getLastInsertedId() ?: return@launch
        }
    }

    suspend fun insertNewSum(transactionSummary: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.insert(transactionSummary)
        }
    }

    fun onNavigatetoVendible(){
        _navigateToVendible.value= arrayOf(transSum.value!!.sum_id.toString(),"1")
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoVendible(){
        _navigateToVendible.value =null
    }
    suspend fun updateSum(transactionSummary: TransactionSummary){
        withContext(Dispatchers.IO){
            datasource1.update(transactionSummary)
        }
    }
    fun updateTransDetail(transactionDetail: TransactionDetail,i: Double){
        viewModelScope.launch {
            transactionDetail.qty = transactionDetail.qty + i
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

            var transSumS: TransactionSummary = datasource1.getTrans(id) ?: return@launch
            transSumS.cust_name = custName.value ?: "Kosong"
            Log.e("SUM","transum in viewModel navigate fun is "+transSumS.sum_id.toString())
           updateSum(transSumS)
           // delete_()
       _navigateToDetail.value=id
        }
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoDetail(){
        this._navigateToDetail.value =null
    }

    suspend fun delete_(){
        withContext(Dispatchers.IO){
            datasource1.deleteAll()
        }
    }
    fun delete(){
        viewModelScope.launch {
            delete()
        }
    }
    fun onShowDialog(transactionDetail: TransactionDetail){
        _showDialog.value = transactionDetail
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onCloseDialog(){
        _showDialog.value = null
    }


}
