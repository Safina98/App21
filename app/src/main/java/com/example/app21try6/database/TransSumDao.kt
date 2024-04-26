package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransSumDao {

    @Insert
    fun insert(transactionSummary: TransactionSummary)
    //@Insert
    //fun insertNew(transactionSummary: TransactionSummary):Int

    @Update
    fun update(transactionSummary: TransactionSummary)

    //@Delete
    //fun delete(transactionSummary: TransactionSummary)

    @Query("DELETE FROM trans_sum_table where sum_id = :id")
    fun delete(id:Int)

    @Query("SELECT total_trans FROM trans_sum_table WHERE sum_id =:sum_id_")
    fun getTotalSum(sum_id_:Int):LiveData<Double>

    @Delete
    fun delete_(vararg stok: TransactionSummary)

    @Query("DELETE FROM trans_sum_table")
    suspend fun deleteAll()
    @Query("DELETE FROM trans_sum_table WHERE total_trans = 0.0")
    suspend fun deleteAllEmpties()

    @Query("SELECT * FROM trans_sum_table")
    fun getAllTransSum():LiveData<List<TransactionSummary>>

    @Query("SELECT * FROM trans_sum_table WHERE is_paid_off = 0 OR is_taken = 0")
    fun getActiveSum():LiveData<List<TransactionSummary>>

    @Query("SELECT cust_name FROM trans_sum_table WHERE sum_id = :sum_id_")
    suspend fun getCustName(sum_id_:Int):String

    @Query("SELECT * FROM trans_sum_table WHERE sum_id = :sum_id_")
    suspend fun getTrans(sum_id_:Int):TransactionSummary

    @Query("SELECT * from trans_sum_table order by sum_id DESC limit 1")
    fun getLastInserted():LiveData<TransactionSummary>

    @Query("SELECT last_insert_rowid()")
    fun getLastInsertedIdN(): Int

    @Query("SELECT * from trans_sum_table WHERE sum_id = :sum_id_")
    fun getTransSum(sum_id_: Int):LiveData<TransactionSummary>

    @Query("SELECT total_trans from trans_sum_table WHERE sum_id = :sum_id_")
    fun getTransSumTotal(sum_id_: Int):LiveData<Double>

    @Query("SELECT cust_name from trans_sum_table order by sum_id DESC limit 1")
    fun getLastInsertedCustName():LiveData<String>
    @Query("SELECT sum_id from trans_sum_table order by sum_id DESC limit 1")
    suspend fun getLastInsertedId():Int?
    //@Query("SELECT * FROM trans_sum_table WHERE sum_id=:id_")
    //suspend fun getSelected(id_:Int):TransactionSummary?
    //@Query("INSERT INTO trans_sum_table (cust_name,total_trans,paid,trans_date,is_taken,is_paid_off) SELECT '' as cust_name,0.0 as total_trans, 0 as paid, '' as trans_date, 0 as is_taken,0 as is_paid_off FROM trans_sum_table WHERE NOT EXISTS(SELECT sum_id FROM trans_sum_table WHERE sum_id =:sum_id) LIMIT 1  ")
    //fun insertNewCost(sum_id:Int)



    /*
    fun getByYear(year:Int)
    fun getByMonth(year:Int,month:Int)
    fun getByDay(year:Int,month:Int,day:Int)

     */








}