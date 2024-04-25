package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.bookkeeping.summary.ListModel

@Dao
interface SummaryDbDao {
    @Insert
    fun insert(summary: Summary)
    @Update
    fun update(summary: Summary)
    @Query("SELECT * from summary_table WHERE id_m = :key")
    fun get(key:Long):Summary?
    @Query("DELETE FROM summary_table")
    fun clear()
    @Query("DELETE FROM summary_table WHERE month = 'empty' OR day = 0 or month_number=0")
    fun clearEmpties()
    @Query("DELETE  FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND id_m = :item_name_ ")
    fun deleteItemSummary(year_: Int,month_: String,day_:Int,item_name_:Int)

    @Query("DELETE FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND item_name!='empty'")
    fun clearToday(year_: Int,month_: String,day_:Int)

    @Query("SELECT * FROM summary_table WHERE year = :year_  AND day =0 ORDER BY id_m ASC")
    fun getAllMonth(year_: Int): LiveData<List<Summary>>

    @Query("SELECT DISTINCT month FROM summary_table WHERE year =:year_ ORDER BY id_m ASC ")
    fun getDistictMonth(year_:Int):LiveData<List<String>>

    @Query("SELECT * FROM summary_table WHERE year = :year_ AND month = :month_  AND day!=0 AND item_name=='empty' ORDER BY day")
    fun getAllDay(year_: Int,month_:String): LiveData<List<Summary>>

    @Query("SELECT * FROM summary_table WHERE year = :year_ AND month = :month_  AND day= :day_ AND item_name=='empty'")
    fun updateTodayTotalIncomeee(year_: Int,month_:String,day_: Int): Summary

    @Query("SELECT DISTINCT day FROM summary_table WHERE year =:year_ AND month = :month_ ORDER BY day")
    fun getDistictDay(year_:Int,month_:String):LiveData<List<Int>>

    @Query("SELECT * FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND item_name!='empty' ORDER BY id_m DESC")
    fun getToday(year_: Int,month_: String,day_:Int): LiveData<List<Summary>>

    @Query("SELECT * FROM summary_table WHERE year = :year_ AND month = :month_ AND day = :day_ AND item_name!='empty' ORDER BY id_m DESC")
    fun getTodayNew(year_: Int,month_: String,day_:Int): List<Summary>

    @Query("SELECT * FROM summary_table  ORDER BY id_m DESC LIMIT 1")
    fun getThisMonth(): Summary?

    @Query("SELECT SUM(total_income) FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day = :day_ ")
    fun getTotalToday(year_: Int,month_: String,day_:Int):LiveData<Double>

    @Query("SELECT SUM(total_income) FROM SUMMARY_TABLE  WHERE year = :year_ ")
    fun getTotalThisYear(year_: Int):LiveData<Double>
    @Query("SELECT SUM(total_income) FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_")
    fun getTotalThisMonth(year_: Int,month_: String):LiveData<Double>

    @Query("SELECT year as year_n,month as month_n, month_number as month_nbr,day as day_n,day as nama,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day !=0 GROUP BY day ")
    fun getAllDayNew(year_: Int,month_: String):LiveData<List<ListModel>>
    @Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")
    fun getAllMonthNew(year_: Int):LiveData<List<ListModel>>

    @Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")
    fun getMonthlyData(year_: Int):List<ListModel>

    @Query("SELECT year as year_n,month as month_n, month_number as month_nbr,day as day_n,day as nama,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day !=0 GROUP BY day ")
    fun getDailyData(year_: Int,month_: String):List<ListModel>

    @Query("SELECT * FROM summary_table ORDER BY month_number")
    fun getAllSummary():LiveData<List<Summary>>

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