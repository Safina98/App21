package com.example.app21try6.transaction.transactionactive

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.TransactionSummary
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/*
 val datasource1: TransSumDao,
    val datasource2: TransDetailDao
 */
class TransactionActiveViewModel(
    application: Application,
   val transRepo:TransactionsRepository
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
    private val _navigateToTransEdit = MutableLiveData<Long>()
    val navigateToTransEdit: LiveData<Long> get() = _navigateToTransEdit
    private val _navigateToTransDetail = MutableLiveData<Long>()
    val navigateToTransDetail: LiveData<Long> get() = _navigateToTransDetail
    private val _navigateToAllTrans = MutableLiveData<Boolean>()
    val navigatToAllTrans: LiveData<Boolean> get() = _navigateToAllTrans

    //Get active transaction from database
    fun getActiveTrans(){
        viewModelScope.launch {
            val list = transRepo.getActiveTransFromDb()
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
            transRepo.deleteTransSummaries(checkedItemList)
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
            trans.tSCloudId=System.currentTimeMillis()
            trans.needsSyncs=1
            val id =transRepo.insertNewSumAndGetId(trans)
            trans.tSCloudId = id
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

    //Navigations
    fun onNavigatetoTransEdit(id:Long){ _navigateToTransEdit.value=id }
    fun onNavigatedToTransEdit(){ this._navigateToTransEdit.value=null }
    fun onNavigatetoTransDetail(id:Long){ _navigateToTransDetail.value = id }
    fun onNavigatedToTransDetail(){ this._navigateToTransDetail.value = null }
    fun onNavigateToAllTrans(){ _navigateToAllTrans.value=true }
    fun onNavigatedToAllTrans(){ _navigateToAllTrans.value=false }

    fun insertCSVBatch(tokensList: List<List<String>>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
               transRepo.performInsertCvs(tokensList)
                _insertionCompleted.value = true
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show()
                Log.i("INSERTCSVPROB","excepttions: $e")
            }finally {
                _isLoading.value = false // Hide loading indicator
            }
        }
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

