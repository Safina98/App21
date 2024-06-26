package com.example.app21try6.transaction.transactionactive

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.Constants
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TransactionActiveViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao
):AndroidViewModel(application){
    private val _navigateToTransEdit = MutableLiveData<Int>()
    val navigateToTransEdit: LiveData<Int> get() = _navigateToTransEdit
    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail

    private val _navigateToAllTrans = MutableLiveData<Boolean>()
    val navigatToAllTrans: LiveData<Boolean> get() = _navigateToAllTrans

   var active_trans = datasource1.getActiveSum(false)
    val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    val currentDate = sdf.format(Date())

    private var _is_image_clicked = MutableLiveData<Boolean>(false)
   val is_image_clicked:LiveData<Boolean>get() = _is_image_clicked

    private var checkedItemList = mutableListOf<TransactionSummary>()

    init {
        viewModelScope.launch {
            //active_trans = datasource1.getActiveSum()
        }
    }

    fun onImageClicked(){
        _is_image_clicked.value = true
    }
    fun onButtonClicked(){
        _is_image_clicked.value = false
        checkedItemList.clear()
    }


    fun onCheckBoxClicked(stok: TransactionSummary,bool:Boolean){
        viewModelScope.launch {
            if(bool == true){
                checkedItemList.add(stok)
            }
            else{
                checkedItemList.remove(stok) }
        }
    }
    fun delete(){
        viewModelScope.launch {
            delete_(checkedItemList)
            onButtonClicked()
        }
    }
    suspend fun delete_(list: MutableList<TransactionSummary>){
        withContext(Dispatchers.IO){
           datasource1.delete_(*list.toTypedArray())
        }
    }
    fun initiateNewId(){
        var id:Int = -1
        viewModelScope.launch {
            var trans =   TransactionSummary()
            trans.trans_date = currentDate
            insertNewSumAndGetId(trans)
        }
    }
    suspend fun insertNewSumAndGetId(transactionSummary: TransactionSummary){
       withContext(Dispatchers.IO){
            datasource1.insert(transactionSummary)
        }
    }
    suspend fun getLastInsertedID():Int{
        return withContext(Dispatchers.IO){
            datasource1.getLastInsertedIdN()
        }
    }
    fun onAddNewTransactionClick(){
        viewModelScope.launch {
            var trans =   TransactionSummary()
            trans.trans_date = currentDate
            insertNewSumAndGetId(trans)
            var id = getLastInsertedID()
            _navigateToTransEdit.value = id
            Log.e("SUMVM","_navigateToTransEdit id "+id.toString()+"")
        }
    }

    fun onNavigatetoTransEdit(id:Int){
        _navigateToTransEdit.value=id
    }
    fun onNavigatedToTransEdit(){
        this._navigateToTransEdit.value=null
    }
    fun onNavigatetoTransDetail(id:Int){
        _navigateToTransDetail.value = id
    }
    fun onNavigatedToTransDetail(){
        this._navigateToTransDetail.value = null
    }
    fun onNavigateToAllTrans(){
        _navigateToAllTrans.value=true
    }
    fun onNavigatedToAllTrans(){
        _navigateToAllTrans.value=false
    }


}

