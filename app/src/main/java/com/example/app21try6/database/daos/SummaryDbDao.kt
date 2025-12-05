package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.bookkeeping.summary.MonthlyProfit
import com.example.app21try6.bookkeeping.summary.ProductProfit
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.Summary
import com.example.app21try6.grafik.StockModel

@Dao
interface SummaryDbDao {
    @Insert
    fun insert(summary: Summary)

    @Update
    fun update(summary: Summary)


    @Query("SELECT * FROM summary_table WHERE item_name = :itemName AND year =:year AND month=:month_n AND day=:day AND price = :price LIMIT 1")
    fun getSummaryByItemNameAndDayName(itemName: String, year: Int, month_n:String, day: Int,price:Double): Summary?

    @Query("SELECT * FROM summary_table WHERE item_name = :itemName AND year =:year AND month=:month_n AND day=:day LIMIT 1")
    fun getSummaryByItemNameAndDayName(itemName: String, year: Int, month_n:String, day: Int): Summary?
    // Function to insert or update based on existence of item_name and day_name combination
    @Transaction
    fun insertOrUpdate(summary: Summary) {
        val existingSummary = getSummaryByItemNameAndDayName(summary.item_name, summary.year,summary.month,summary.day,summary.price)
        if (existingSummary == null) {
            summary.summaryCloudId= System.currentTimeMillis()
            insert(summary)
        } else {
            summary.id_m = existingSummary.id_m
            summary.item_sold = summary.item_sold+existingSummary.item_sold
            summary.total_income = summary.item_sold*summary.price
            update(summary)
        }
    }
    @Transaction
    fun insertOrUpdateD(summary: Summary) {
        val existingSummary = getSummaryByItemNameAndDayName(summary.item_name, summary.year,summary.month,summary.day)
        if (existingSummary == null) {
            summary.summaryCloudId=System.currentTimeMillis()
            insert(summary)
        } else {
            summary.id_m = existingSummary.id_m
            summary.price +=existingSummary.price
            summary.total_income +=existingSummary.total_income
            update(summary)
        }
    }


    @Query("DELETE  FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND id_m = :item_name_ ")
    fun deleteItemSummary(year_: Int,month_: String,day_:Int,item_name_:Int)

    @Query("DELETE FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND item_name!='empty'")
    fun clearToday(year_: Int,month_: String,day_:Int)



    @Query("SELECT * FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND item_name!='empty' ORDER BY id_m DESC")
    fun getToday(year_: Int,month_: String,day_:Int): LiveData<List<Summary>>


    @Query("SELECT SUM(total_income) FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day = :day_ ")
    fun getTotalToday(year_: Int,month_: String,day_:Int):LiveData<Double>

    @Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total,SUM((price - product_capital) * item_sold) AS monthly_profit FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")
    fun getMonthlyData(year_: Int):List<ListModel>


    @Query("""
        SELECT year, month, SUM((price - product_capital) * item_sold) AS monthly_profit
        FROM summary_table 
        GROUP BY year, month
        ORDER BY year, month
    """)
    fun getMonthlyProfitN(): List<MonthlyProfit>


    @Query("SELECT " +
            "year as year_n,month as month_n, month_number as month_nbr,day as day_n,day as nama,day_name as day_name," +
            "SUM(total_income) as total, SUM((price - product_capital) * item_sold) AS monthly_profit FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day !=0 GROUP BY day ")
    fun getDailyData(year_: Int,month_: String):List<ListModel>

    @Query("SELECT * FROM summary_table ORDER BY month_number")
    fun getAllSummary():LiveData<List<Summary>>


    @Query("SELECT DISTINCT year year FROM summary_table")
    fun getAllYear():LiveData<List<Int>>


    @Transaction
    suspend fun performTransaction(block: suspend () -> Unit) {
        // Begin transaction
        try {
            block()
        } catch (e: Exception) {
            // Handle exceptions
            throw e
        }
    }

}