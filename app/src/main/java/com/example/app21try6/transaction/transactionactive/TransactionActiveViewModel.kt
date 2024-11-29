package com.example.app21try6.transaction.transactionactive

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TransactionActiveViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao
):AndroidViewModel(application){

    private val _insertionCompleted = MutableLiveData<Boolean>()
    val insertionCompleted: LiveData<Boolean> get() = _insertionCompleted
    //Show or hide loading image when insert csv
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    //All  trans for export
    //var allTransFromDB =datasource2.getExportedDataNew()
    //All active trans to populate rv
    private var _active_trans = MutableLiveData<List<TransactionSummary>>()
    val active_trans :LiveData<List<TransactionSummary>> get() = _active_trans
    //toggle Delete icon in toolbar for delete
    private var _is_image_clicked = MutableLiveData<Boolean>(false)
    val is_image_clicked:LiveData<Boolean>get() = _is_image_clicked
    //List checked transaction for delete purpose
    private var checkedItemList = mutableListOf<TransactionSummary>()
    //todays date
    var todaysdate = Date()
    //Navigation
    private val _navigateToTransEdit = MutableLiveData<Int>()
    val navigateToTransEdit: LiveData<Int> get() = _navigateToTransEdit
    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail
    private val _navigateToAllTrans = MutableLiveData<Boolean>()
    val navigatToAllTrans: LiveData<Boolean> get() = _navigateToAllTrans

   //Get active transaction from database
    fun getActiveTrans(){
        viewModelScope.launch {
            val list = getActiveTransFromDb()
            _active_trans.value = list
        }
    }

    //Add or remove checked item from checkedItemList
    fun onCheckBoxClicked(stok: TransactionSummary, bool:Boolean){
        viewModelScope.launch {
            if(bool == true){ checkedItemList.add(stok) } else{ checkedItemList.remove(stok) }
        }
    }
    //delete checked item from rv
    fun delete(){
        viewModelScope.launch {
            delete_(checkedItemList)
            onButtonClicked()
        }
    }
    //clear checkedItemList
    fun unchecked(){
        viewModelScope.launch {
            checkedItemList.clear()
        }
    }
    //Add new transsummary on button click and navigate to trans edit
    fun onAddNewTransactionClick(){
        viewModelScope.launch {
            val trans =   TransactionSummary()
            trans.trans_date = Date()
            trans.ref =UUID.randomUUID().toString()
            val id =insertNewSumAndGetId(trans).toInt()
            trans.sum_id = id
            onNavigatetoTransEdit(id)
        }
    }
    //on delete icon from toolbar click
    fun onImageClicked(){
        _is_image_clicked.value = true
    }
    //on delete icon from toolbar click
    fun onButtonClicked(){
        _is_image_clicked.value = false
        unchecked()
    }
    //Suspends
    suspend fun insertNewSumAndGetId(transactionSummary: TransactionSummary):Long{
        return withContext(Dispatchers.IO){
            datasource1.insertNew(transactionSummary)
        }
    }
    suspend fun delete_(list: MutableList<TransactionSummary>){
        withContext(Dispatchers.IO){
            datasource1.delete_(*list.toTypedArray())
        }
    }
    private suspend fun getActiveTransFromDb():List<TransactionSummary>{
        return withContext(Dispatchers.IO){
            datasource1.getActiveSumList(false)
        }
    }
    //Navigations
    fun onNavigatetoTransEdit(id:Int){ _navigateToTransEdit.value=id }
    fun onNavigatedToTransEdit(){ this._navigateToTransEdit.value=null }
    fun onNavigatetoTransDetail(id:Int){ _navigateToTransDetail.value = id }
    fun onNavigatedToTransDetail(){ this._navigateToTransDetail.value = null }
    fun onNavigateToAllTrans(){ _navigateToAllTrans.value=true }
    fun onNavigatedToAllTrans(){ _navigateToAllTrans.value=false }
    fun insertCSVBatch(tokensList: List<List<String>>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                datasource1.performTransaction {
                    val batchSize = 100 // Define your batch size here
                    for (i in 0 until tokensList.size step batchSize) {
                       // Log.i("INSERTCSVPROB","i: $i")
                        val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                      //  Log.i("INSERTCSVPROB","batch: $batch")
                        insertBatch(batch)
                    }
                }
                _insertionCompleted.value = true
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
                Log.i("INSERTCSVPROB","excepttions: $e")
            }finally {
                _isLoading.value = false // Hide loading indicator
            }
        }
    }
    private suspend fun insertBatch(batch: List<List<String>>) {
        batch.forEach { tokens ->
            insertCSVN(tokens)
        }
    }
    fun parseDate(dateString: String): Date? {
        // Specify the format pattern
        val pattern = "EEE MMM dd HH:mm:ss zzz yyyy"
        // Create a SimpleDateFormat instance with the specified pattern and locale
        val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)

        return try {
            // Parse the date string into a Date object
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // Handle the exception if the date string is unparseable
            e.printStackTrace()
            Log.i("INSERTCSVPROB","parse date catch: $e")
            null
        }
    }
    fun getDate(dateString:String?):Date?{
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return if (dateString!==null)inputFormat.parse(dateString) else null
    }
    private suspend fun insertCSVN(token: List<String>) {
       // Log.i("INSERTCSVPROB","token: $token")
        Log.i("INSERTCSVPROB","trans_detail: $token")
        val transactionSummary= TransactionSummary()
        transactionSummary.trans_date =getDate(token[0])!!
        transactionSummary.cust_name = token[1]
        transactionSummary.total_trans = token[2].toDouble()
        transactionSummary.paid = token[3].toInt()
        transactionSummary.is_taken_ = token[4].toBooleanStrictOrNull() ?: true//token[4].toBooleanStrictOrNull() ?: false
        transactionSummary.is_paid_off  = token[5].toBooleanStrictOrNull() ?: true
        transactionSummary.ref = token[11]
        transactionSummary.is_keeped = token[12].toBooleanStrictOrNull() ?: true//token[12].toBooleanStrictOrNull() ?: false
        transactionSummary.sum_note = token[16]
        val transactionDetail = TransactionDetail()
        // "${j.trans_item_name},${j.trans_price},${j.qty},${j.total_price},${j.is_prepared}"
        transactionDetail.trans_item_name = token[6]
        transactionDetail.trans_price = token[7].toInt()
        transactionDetail.qty = token[8].toDouble()
        transactionDetail.total_price = token[9].toDouble()
        transactionDetail.is_prepared =  token[10].toBooleanStrictOrNull() ?: false
        transactionDetail.unit = if (token[14]!="null") token[14] else null
        transactionDetail.trans_detail_date =if (token[13]!="null")parseDate(token[13]) else null
        transactionDetail.unit_qty ==if (token[15]!="null") token[15].toDouble() else null
        transactionDetail.item_position = 0
       Log.i("INSERTCSVPROB","trans_detail: $transactionDetail")
        datasource1.insertIfNotExist(transactionSummary.cust_name,transactionSummary.total_trans,transactionSummary.paid,transactionSummary.trans_date?:Date(),transactionSummary.is_taken_,transactionSummary.is_paid_off,transactionSummary.is_keeped,transactionSummary.ref,transactionSummary.sum_note)
        //datasource2.insert(transactionDetail)
        datasource2.insertTransactionDetailWithRef(transactionSummary.ref, transactionDetail.trans_item_name, transactionDetail.qty, transactionDetail.trans_price, transactionDetail.total_price, transactionDetail.is_prepared,transactionDetail.trans_detail_date,transactionDetail.unit,transactionDetail.unit_qty,transactionDetail.item_position)
    }
    fun writeCSV(file: File) {
            try {
                val content = "Date,Customer, Total Trans, Paid, isTaken,isPaidOff,Item Name,Item Price,QTY,Total Price,is Prepared,ref,isKeeped"
                val fw = FileWriter(file.absoluteFile)
                val bw = BufferedWriter(fw)
                bw.write(content)
                bw.newLine()
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy") // Change the date format according to your requirements
                // Write data to file
                /*
                allTransFromDB.value?.let {
                    for (j in allTransFromDB.value!!) {
                        if (j.trans_detail_id!=null) {

                            val dateString = dateFormatter.format(j.trans_date)
                            //          0               1               2
                            val line = "$dateString,${j.cust_name},${j.total_trans}," +
                                    //      3           4               5               6
                                        "${j.paid},${j.is_taken},${j.is_paid_off},${j.trans_item_name}," +
                                    //      7               8           9               10              11
                                        "${j.trans_price},${j.qty},${j.total_price},${j.is_prepared},${j.ref}," +
                                    //      12                  13                 14           15
                                        "${j.is_keeped},${j.trans_detail_date},${j.unit},${j.unit_qty},"+
                                    //  16
                                        "${j.sum_note}"
                            bw.write(line)
                            bw.newLine()
                        }
                    }
                    // Close BufferedWriter
                    bw.close()
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT).show()
                }

                 */
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

