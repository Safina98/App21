package com.example.app21try6.transaction.transactionselect

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.*
import kotlinx.coroutines.*

class TransactionSelectViewModel(
    val sum_id:Int,
    val database1: CategoryDao,
    val database2: ProductDao,
    val database3: BrandDao,

    val database4: SubProductDao,
    val date:Array<String>,
    val database5: TransSumDao,
    val database6:TransDetailDao,
    application: Application): AndroidViewModel(application){
    val allSubsFromDB = database4.getAllSub()
    val thisTrans = database6.selectATransDetail(sum_id)
    var _trans_select_model = MutableLiveData<List<TransSelectModel>>()
    val trans_select_model =database6.getSubProduct(date[1].toInt(),sum_id)
    private val _showDialog = MutableLiveData<TransSelectModel>()
    val showDialog:LiveData<TransSelectModel> get() = _showDialog


    fun updateTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            var t = converter(s)
            t.trans_detail_id = s.trans_detail_id
            _updateTransDetail(t)
        }
    }
    fun converter(s:TransSelectModel): TransactionDetail {
        var t = TransactionDetail()
        t.sum_id = sum_id
        t.qty = s.qty
        t.total_price = s.qty*s.item_price
        t.trans_item_name = s.item_name
        t.trans_price = s.item_price
        return t
    }

    fun onCheckBoxClicked(s:TransSelectModel,boolean: Boolean){
        viewModelScope.launch {
            checkedSub(s.item_name,if (boolean) 1 else 0)
            if (boolean == true) insertTransDetail(s) else delete(s)
        }

    }
    private suspend fun checkedSub(name:String,bool:Int){
        withContext(Dispatchers.IO){
            database4.update_checkbox(name,bool)
        }
    }

    fun updateTransDetail(transSelectModel: TransSelectModel,i: Double){
        viewModelScope.launch {
            transSelectModel.qty = transSelectModel.qty +i
            var transactionDetail= converter(transSelectModel)
            _updateTransDetail(transactionDetail)
        }
    }

    fun delete(s:TransSelectModel){
        viewModelScope.launch {
            _delete(s)
        }
    }

    private suspend fun _delete(s:TransSelectModel){
        withContext(Dispatchers.IO){
            database6.deleteAnItemTransDetail(s.trans_detail_id)
        }
    }

    fun insertTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            var t = converter(s)
            _insertTransDetail(t)
        }
    }

    private suspend fun _insertTransDetail(transactionDetail: TransactionDetail){
       withContext(Dispatchers.IO){ database6.insert(transactionDetail)}
    }

    private suspend fun _updateTransDetail(transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){database6.update(transactionDetail)}
    }

    fun onShowDialog(transSelectModel: TransSelectModel){
        _showDialog.value = transSelectModel
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun onCloseDialog(){
        _showDialog.value = null
    }
    fun unCheckedAllSubs(){
        viewModelScope.launch {
            uncheckedAllSubs_()
        }
    }
    private suspend fun uncheckedAllSubs_(){
        withContext(Dispatchers.IO){
            database4.unchecked_allCheckbox()
        }
    }

    }