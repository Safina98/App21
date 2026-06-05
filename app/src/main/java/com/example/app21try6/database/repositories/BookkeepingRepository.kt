package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.app21try6.database.models.ListModel
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.tables.Summary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookkeepingRepository(
    application: Application
) {
    private val summaryDbDao= VendibleDatabase.getInstance(application).summaryDbDao

    suspend fun assignCloudIdToSummaryTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            summaryDbDao.assignSumamryCloudID(cloudId,id)
        }
    }

    suspend fun swapDecemberAndJanuary(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO){
                summaryDbDao.assignDecemberTemp()
                summaryDbDao.januaryToDecember()
                summaryDbDao.decemberToJanuary()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllYear():LiveData<List<Int>>{
        return summaryDbDao.getAllYear()
    }
    fun getDailySells(date: Array<String>): LiveData<List<Summary>> {
        return summaryDbDao.getToday(date[0].toInt(), date[1], date[2].toInt())
    }
    fun getDailyTotal(date: Array<String>): LiveData<Double> {
        return summaryDbDao.getTotalToday(date[0].toInt(), date[1], date[2].toInt())
    }
    fun getAllSummary():LiveData<List<Summary>>{
        return  summaryDbDao.getAllSummary()
    }
    suspend fun update(summary: Summary){
        withContext(Dispatchers.IO){
            summaryDbDao.update(summary)}
    }
    suspend fun deleteItemSummary(year:Int, month:String, day: Int, item_name: Int){
        withContext(Dispatchers.IO){
            summaryDbDao.deleteItemSummary(year,month,day,item_name)
        }
    }
    suspend fun clearToday(year:Int, month:String, day: Int) {
        withContext(Dispatchers.IO) {
            summaryDbDao.clearToday(year, month, day)
        }
    }
    suspend fun insertTransactionToSummary(summary: Summary){
        withContext(Dispatchers.IO){
            summaryDbDao.insertOrUpdate(summary)
        }
    }


        suspend fun insertItemToSummary(summary: Summary){
        withContext(Dispatchers.IO) {
            summaryDbDao.insert(summary)
        }
    }

    suspend fun getMonthlyData(year:Int): List<ListModel> {
        return withContext(Dispatchers.IO) {
            summaryDbDao.getMonthlyData( year)
        }
    }


    suspend fun getDailyDataForSelectedMonth(year: Int,month: String): List<ListModel> {
        return withContext(Dispatchers.IO) {
            summaryDbDao.getDailyData( year,month)
        }
    }

    //....................................................CSV......................................................................

}