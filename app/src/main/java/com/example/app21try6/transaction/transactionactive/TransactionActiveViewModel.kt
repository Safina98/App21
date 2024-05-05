package com.example.app21try6.transaction.transactionactive

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.Constants
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionDetail
import com.example.app21try6.database.TransactionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
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

   var allTransFromDB =datasource2.getExportedDataNew()

    private val _navigateToAllTrans = MutableLiveData<Boolean>()
    val navigatToAllTrans: LiveData<Boolean> get() = _navigateToAllTrans

 //  var active_trans = datasource1.getActiveSum(false)
    private var _active_trans = MutableLiveData<List<TransactionSummary>>()
    val active_trans :LiveData<List<TransactionSummary>> get() = _active_trans

    val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
    val currentDate = sdf.format(Date())

    private var _is_image_clicked = MutableLiveData<Boolean>(false)
   val is_image_clicked:LiveData<Boolean>get() = _is_image_clicked

    private var checkedItemList = mutableListOf<TransactionSummary>()

    init {
        Log.i("ACTIVETRANSPROB", "INIT: ${active_trans.value}")
    }
    fun getActiveTrans(){
        viewModelScope.launch {
            var list = withContext(Dispatchers.IO){
               datasource1.getActiveSumList(false)
            }
            _active_trans.value = list
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
           // trans.trans_date = currentDate
            insertNewSumAndGetId(trans)
        }
    }
    suspend fun insertNewSumAndGetId(transactionSummary: TransactionSummary):Long{
       return withContext(Dispatchers.IO){
            datasource1.insertNew(transactionSummary)
        }
    }
    suspend fun getLastInsertedID():Int{
        return withContext(Dispatchers.IO){
            datasource1.getLastInsertedIdN()
        }
    }
    fun onAddNewTransactionClick(){
        viewModelScope.launch {
            val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
            val currentDate = sdf.parse(sdf.format(Date()))
            var trans =   TransactionSummary()
            trans.trans_date = currentDate
            trans.ref =UUID.randomUUID().toString()
            var id =insertNewSumAndGetId(trans).toInt()
            trans.sum_id = id
            Log.i("INSERTCSVPROB","transDetail transSUm: $trans")
            //var id = getLastInsertedID()
            _navigateToTransEdit.value = id
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
    fun getAllTransactions(){
        viewModelScope.launch {
             var list = getAllTransactionsFromDB()
            //allTransFromDB.value = list
        }
    }
    private suspend fun getAllTransactionsFromDB():List<TransExportModel>{
        return withContext(Dispatchers.IO)
        {
            datasource2.getExportedData()
        }
    }
    fun insertCSVBatch(tokensList: List<List<String>>) {
        viewModelScope.launch {
            try {
               // _isLoading.value = true
                datasource1.performTransaction {
                    val batchSize = 100 // Define your batch size here
                    for (i in 0 until tokensList.size step batchSize) {
                        val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                        insertBatch(batch)
                    }
                }
              //  _insertionCompleted.value = true
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
            }finally {
              //  _isLoading.value = false // Hide loading indicator
            }
        }
    }
    private suspend fun insertBatch(batch: List<List<String>>) {
        batch.forEach { tokens ->
            insertCSVN(tokens)
        }
    }
    private suspend fun insertCSVN(token: List<String>) {
        val transactionSummary=TransactionSummary()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy") // Adjust the format according to your CSV date format
        val date = dateFormat.parse(token[0])
        transactionSummary.trans_date = date
        transactionSummary.cust_name = token[1]
        transactionSummary.total_trans = token[2].toDouble()
        transactionSummary.paid = token[3].toInt()
        transactionSummary.is_taken_ = if(token[4]=="TRUE") true else false
        transactionSummary.is_paid_off  = if(token[5]=="TRUE") true else false
        transactionSummary.ref = token[11]
        val transactionDetail = TransactionDetail()
        // "${j.trans_item_name},${j.trans_price},${j.qty},${j.total_price},${j.is_prepared}"
        transactionDetail.trans_item_name = token[6]
        transactionDetail.trans_price = token[7].toInt()
        transactionDetail.qty = token[8].toDouble()
        transactionDetail.total_price = token[9].toDouble()
        transactionDetail.is_prepared =  if(token[10]=="TRUE") true else false

        datasource1.insertIfNotExist(transactionSummary.cust_name,transactionSummary.total_trans,transactionSummary.paid,transactionSummary.trans_date,transactionSummary.is_taken_,transactionSummary.is_paid_off,transactionSummary.ref)
        //datasource2.insert(transactionDetail)
        datasource2.insertTransactionDetailWithRef(transactionSummary.ref, transactionDetail.trans_item_name, transactionDetail.qty, transactionDetail.trans_price, transactionDetail.total_price, transactionDetail.is_prepared)

    }
    fun writeCSV(file: File) {
            try {
                val content = "Date,Customer, Total Trans, Paid, isTaken,isPaidOff,Item Name,Item Price,QTY,Total Price,is Prepared,ref"
                val fw = FileWriter(file.absoluteFile)
                val bw = BufferedWriter(fw)
                bw.write(content)
                bw.newLine()
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy") // Change the date format according to your requirements
                // Write data to file
                allTransFromDB.value?.let {
                    for (j in allTransFromDB.value!!) {
                        if (j.trans_detail_id!=null) {

                            val dateString = dateFormatter.format(j.trans_date)
                            val line = "$dateString,${j.cust_name},${j.total_trans},${j.paid},${j.is_taken},${j.is_paid_off},${j.trans_item_name},${j.trans_price},${j.qty},${j.total_price},${j.is_prepared},${j.ref}"
                            Log.i("INSERTCSVPROB", "line: $line")
                            Log.i("INSERTCSVPROB", "j: $j")
                            bw.write(line)
                            bw.newLine()
                        }
                    }
                    // Close BufferedWriter
                    bw.close()
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(getApplication(),"Failed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("INSERTCSVPROB", "$e" )
                Toast.makeText(getApplication(),"An unexpected error occurred", Toast.LENGTH_SHORT).show()
            }

    }



}

