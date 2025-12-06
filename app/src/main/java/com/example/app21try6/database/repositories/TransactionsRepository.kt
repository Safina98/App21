package com.example.app21try6.database.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.TracketailWarnaModel
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.getDate
import com.example.app21try6.grafik.StockModel
import com.example.app21try6.parseDate
import com.example.app21try6.transaction.transactiondetail.TransactionDetailWithProduct
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class TransactionsRepository(application: Application) {
    private val transDetailDao= VendibleDatabase.getInstance(application).transDetailDao
    private val transSumDao=VendibleDatabase.getInstance(application).transSumDao


    suspend fun getAllTransactionSummary():List<TransactionSummary>{
        return withContext(Dispatchers.IO){
            transSumDao.selectAllTransactionSummaryTable()
        }
    }
    suspend fun assignCloudIdToTransactionSummaryTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            transSumDao.assignTransactionSummaryCloudID(cloudId,id)
        }
    }

    suspend fun getAllTransactionDetail():List<TransactionDetail>{
        return withContext(Dispatchers.IO){
            transDetailDao.selectAllTransactionDetailTable()
        }
    }
    suspend fun assignCloudIdToTransactionDetailTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            transDetailDao.assignTransactionDetailCloudID(cloudId,id)
        }
    }
    suspend fun getTransactionDetailsWithDuplicateCloudIds(): List<TransactionDetail> {
        return withContext(Dispatchers.IO) {
            transDetailDao.getTransactionDetailsWithDuplicateCloudIds()
        }
    }

    suspend fun getTransactionSummariesWithDuplicateCloudIds(): List<TransactionSummary> {
        return withContext(Dispatchers.IO) {
            transSumDao.getTransactionSummariesWithDuplicateCloudIds()
        }
    }

    fun getStockModel(): LiveData<List<StockModel>> {
        return transDetailDao.getTransactionDetails()
    }
    fun getTransSelectModelLive(productId: Long, sum_id: Long): LiveData<List<TransSelectModel>> {
        Log.i("LiveDataProbs","getTransSelectModelLive sumId: $sum_id")
        return transDetailDao.getSubProductMLive(productId, sum_id)
    }

    fun getTransactionDetails(id:Long): LiveData<List<TransactionDetail>> {
        return transDetailDao.selectATransDetail(id)
    }

    suspend fun getTransactionDetailsWithProductIDList(id:Long): List<TransactionDetailWithProduct>? {
        return withContext(Dispatchers.IO){ transDetailDao.getTransactionDetailsWithProductList(id)}
    }
    fun getTotalTransaction(id:Long): LiveData<Double> {
        return transDetailDao.getTotalTrans(id)
    }
    suspend fun getTotalTransactionn(id: Long):Double{
        return withContext(Dispatchers.IO){transDetailDao.getTotalTransaction(id)}
    }

    suspend fun insertTransDetail(transDetail: TransactionDetail):Long{
       return withContext(Dispatchers.IO){
           transDetail.tDCloudId= System.currentTimeMillis()
            transDetailDao.insert(transDetail)
        }
    }
    suspend fun deleteTransDetail(sum_id:Long,name:String){
        withContext(Dispatchers.IO){
           transDetailDao.deteleAnItemTransDetailSub(sum_id,name)
        }
    }
    suspend fun deleteTransDetail(id:Long){
        withContext(Dispatchers.IO){
            transDetailDao.deleteAnItemTransDetail(id)
        }
    }
    suspend fun updateTransDetail(transdetail: TransactionDetail){
        withContext(Dispatchers.IO){
            transDetailDao.update(transdetail)
        }
    }

    suspend fun getStockModelList():List<StockModel>{
        return withContext(Dispatchers.IO){
            transDetailDao.getTransactionDetailsList()
        }
    }
    ///////////////////////////////////TransSum////////////////////////////////////////////////////////
    fun getTransactionSummary(id:Long): LiveData<TransactionSummary> {
        return transSumDao.getTransSum(id)
    }
    suspend fun filterTransSum(query: String?,sumId:Long?, limit:Int, offset:Int, startDate:Date?, endDate:Date?):List<TransactionSummary>{
        return withContext(Dispatchers.IO){

            transSumDao.getTransactionSummariesByItemName(query,sumId,startDate,endDate,limit,offset)
        }
    }
    suspend fun getSubProductTrans(query: String, startDate:Date?, endDate:Date?):List<TracketailWarnaModel>{
        return withContext(Dispatchers.IO){
            transDetailDao.getTracketailWarnaModels(query,startDate,endDate)
        }
    }
    suspend fun getTransactionSummaryById(id: Long):TransactionSummary{
        return withContext(Dispatchers.IO){
            transSumDao.getTrans(id)
        }
    }
    suspend fun updateTransactionSummary(transSum: TransactionSummary){
        withContext(Dispatchers.IO){
            transSum.needsSyncs=1
            transSumDao.update(transSum)
        }

    }
    suspend fun getTransactionCount(name: String?,startDate:Date?,endDate:Date?):Int?{
       return withContext(Dispatchers.IO){ transSumDao.getTransactionCount(startDate,endDate,name) }
    }
    suspend fun getTotalTransactionAfterDiscount(name: String?,startDate:Date?,endDate:Date?):Double?{
       return withContext(Dispatchers.IO){transSumDao.getTotalAfterDiscount(startDate,endDate,name)}
    }
    suspend fun insertNewSumAndGetId(transactionSummary: TransactionSummary):Long{
        return withContext(Dispatchers.IO){
            transactionSummary.tSCloudId= System.currentTimeMillis()
            transSumDao.insertNew(transactionSummary)
        }
    }

    suspend fun deleteTransSummaries(list: MutableList<TransactionSummary>){
        withContext(Dispatchers.IO){
            transSumDao.delete_(*list.toTypedArray())
        }
    }
    suspend fun getActiveTransFromDb():List<TransactionSummary>{
        return withContext(Dispatchers.IO){
            transSumDao.getActiveSumList(false)
        }
    }
    suspend fun performInsertCvs(tokensList: List<List<String>>){
       transSumDao.performTransaction {
            val batchSize = 100 // Define your batch size here
            for (i in 0 until tokensList.size step batchSize) {
                // Log.i("INSERTCSVPROB","i: $i")
                val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                //  Log.i("INSERTCSVPROB","batch: $batch")
                insertBatch(batch)
            }
        }
    }
    private suspend fun insertBatch(batch: List<List<String>>) {
        batch.forEach { tokens ->
            insertCSVN(tokens)
        }
    }

    private suspend fun insertCSVN(token: List<String>) {
        // Log.i("INSERTCSVPROB","token: $token")
        Log.i("INSERTCSVPROB","trans_detail: $token")
        val transactionSummary= TransactionSummary()
        transactionSummary.trans_date = getDate(token[0])
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
       transSumDao.insertIfNotExist(transactionSummary.cust_name,transactionSummary.total_trans,transactionSummary.paid,transactionSummary.trans_date?: Date(),transactionSummary.is_taken_,transactionSummary.is_paid_off,transactionSummary.is_keeped,transactionSummary.ref,transactionSummary.sum_note)
        //datasource2.insert(transactionDetail)
        transDetailDao.insertTransactionDetailWithRef(transactionSummary.ref, transactionDetail.trans_item_name, transactionDetail.qty, transactionDetail.trans_price, transactionDetail.total_price, transactionDetail.is_prepared,transactionDetail.trans_detail_date,transactionDetail.unit,transactionDetail.unit_qty,transactionDetail.item_position)
    }
}