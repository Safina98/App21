package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.bookkeeping.summary.MonthlyProfit
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.tables.Summary
import com.example.app21try6.getDateFromComponents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookkeepingRepository(
    application: Application
) {
    private val summaryDbDao= VendibleDatabase.getInstance(application).summaryDbDao
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
    suspend fun getMothlyProfit():List<MonthlyProfit>{
        return withContext(Dispatchers.IO){
            summaryDbDao.getMonthlyProfitN()
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
    suspend fun insertCSVBatch(tokensList: List<List<String>>) {
        summaryDbDao.performTransaction {
            val batchSize = 100 // Define batch size
            for (i in 0 until tokensList.size step batchSize) {
                val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
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
        val summary = Summary().apply {
            year = token[0].toInt()
            month = token[1]
            month_number = token[2].toInt()
            day_name= token[4]
            day = token[3].toInt()
            date = getDateFromComponents(year, month, month_number, day, day_name)
            item_name = token[5]
            item_sold = token[7].toDouble()
            price = token[6].toDouble()
            total_income = token[8].toDouble()
        }
        summaryDbDao.insert(summary)
    }











}