package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.app21try6.database.models.TransSumModel
import com.example.app21try6.database.tables.TransactionSummary
import java.util.Date

@Dao
interface TransSumDao {
    //worker
    @Query("UPDATE trans_sum_table SET total_after_discount = :totalAfterDiscount WHERE sum_id = :sumId")
    fun updateTotalAfterDiscount(sumId: Int, totalAfterDiscount: Double)

    @Query("SELECT SUM(total_trans) as total FROM trans_sum_table WHERE trans_date >= :date AND trans_date<:date2")
    fun getTransactionSummariesAfterDate(date: Date,date2:Date): Double
    @Query("SELECT SUM(total_trans) as total FROM trans_sum_table WHERE trans_date >= :date AND trans_date<:date2 AND is_keeped = 0")
    fun getTransactionSummariesAfterDateNotBooked(date: Date,date2:Date): Double
    @Query("SELECT SUM(total_trans) as total FROM trans_sum_table WHERE trans_date >= :date AND trans_date<:date2 AND is_keeped = 1")
    fun getTransactionSummariesAfterDateBooked(date: Date,date2:Date): Double
    @Query("SELECT *  FROM trans_sum_table WHERE  trans_date >= :date AND trans_date<:date2 AND is_keeped = 0")
    fun getTransactionSummariesAfterDateList(date: Date,date2: Date): List<TransactionSummary>
    @Insert
    fun insert(transactionSummary: TransactionSummary)
    @Insert
    fun insertNew(transactionSummary: TransactionSummary):Long

    @Update
    fun update(transactionSummary: TransactionSummary)

    @Query("""
        UPDATE trans_sum_table 
        SET custId = (
            SELECT custId FROM customer_table 
            WHERE customerBussinessName = trans_sum_table.cust_name
        )
        WHERE cust_name IN (SELECT customerBussinessName FROM customer_table)
    """)
    suspend fun updateCustIdBasedOnCustName()

// @Query("INSERT INTO product_table (product_name,product_price,checkBoxBoolean,best_selling,brand_code,cath_code) SELECT :product_name_ as product_name,:product_price_ as product_price, 0 as checkBoxBoolean,:best_selling_ as best_selling,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ limit 1) as brand_code,(SELECT category_id FROM category_table WHERE category_name = :cath_code_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM product_table WHERE product_name = :product_name_)")
    // @Query("INSERT INTO category_table(category_name) SELECT :cath_name_ WHERE NOT EXISTS (SELECT 1 FROM category_table WHERE category_name = :cath_name_)")
@Query("INSERT INTO trans_sum_table (cust_name, total_trans, paid, trans_date, is_taken, is_paid_off, is_keeped,ref,sum_note) " +
        "SELECT  :custName, :totalTrans, :paid, :transDate, :isTaken, :isPaidOff,:isKeeped, :ref, :sum_note " +
        "WHERE NOT EXISTS (SELECT 1 FROM trans_sum_table WHERE ref = :ref)")
 fun insertIfNotExist(
    custName: String,
    totalTrans: Double,
    paid: Int,
    transDate: Date,
    isTaken: Boolean,
    isPaidOff: Boolean,
    isKeeped: Boolean,
    ref: String,
    sum_note:String?)
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

    @Query("SELECT * FROM trans_sum_table")
    suspend fun getAllTransSumList():List<TransactionSummary>

    @Query("SELECT * FROM trans_sum_table WHERE  is_taken = :bool")
    fun getActiveSum(bool:Boolean):LiveData<List<TransactionSummary>>

    @Query("SELECT * FROM trans_sum_table WHERE  is_taken = :bool")
    fun getActiveSumList(bool:Boolean):List<TransactionSummary>

    @Query("SELECT cust_name FROM trans_sum_table WHERE sum_id = :sum_id_")
    suspend fun getCustName(sum_id_:Int):String

    @Query("SELECT * FROM trans_sum_table WHERE sum_id = :sum_id_")
    suspend fun getTrans(sum_id_:Int): TransactionSummary

    @Query("SELECT * from trans_sum_table order by sum_id DESC limit 1")
    fun getLastInserted():LiveData<TransactionSummary>

    @Query("""
    SELECT DISTINCT ts.* FROM trans_sum_table AS ts
    WHERE 
    (
        (:name IS NOT NULL AND (
            ts.cust_name LIKE '%' || :name || '%' 
            OR ts.sum_id IN (
                SELECT td.sum_id FROM trans_detail_table td 
                WHERE td.trans_item_name LIKE '%' || :name || '%'
            )
        ))
        OR (:name IS NULL)
    )
    AND (:startDate IS NULL OR ts.trans_date >= :startDate)
    AND (:endDate IS NULL OR ts.trans_date <= :endDate)
    ORDER BY ts.trans_date DESC
    LIMIT :limit OFFSET :offset
""")

    fun getTransactionSummariesByItemName(name: String?, startDate: Date?,endDate: Date?,limit:Int,offset: Int): List<TransactionSummary>

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

    @Query("SELECT * FROM trans_sum_table WHERE trans_date >= :startOfDay AND trans_date < :startOfNextDay")
    fun getTransactionsForToday(startOfDay: Date, startOfNextDay: Date): List<TransactionSummary>

    @Query("SELECT * FROM trans_sum_table WHERE total_trans=:totalSum AND cust_name=:custName")
    fun getStrandedData(totalSum:Double,custName:String):List<TransactionSummary>

    @Query("SELECT * FROM trans_sum_table WHERE sum_id=:id")
    fun getStrandedData(id:Int):TransactionSummary


    @Query("""
    SELECT DISTINCT t.sum_id, t.cust_name, t.total_trans, t.trans_date, t.paid, 
                    t.is_taken, t.is_paid_off, t.is_keeped, t.ref, t.sum_note, t.custId ,t.total_after_discount
    FROM trans_sum_table t 
    JOIN trans_detail_table AS td ON t.sum_id = td.sum_id 
    WHERE 
        (:name IS NULL OR td.trans_item_name IS NULL OR td.trans_item_name LIKE '%' || :name || '%')
        AND (:startDate IS NULL OR t.trans_date >= :startDate)
        AND (:endDate IS NULL OR t.trans_date <= :endDate)
    ORDER BY t.trans_date DESC
""")
    fun getFilteredData3( startDate: String?, endDate: String?,name:String?): List<TransactionSummary>


    @Query("""
    SELECT DISTINCT t.sum_id, t.cust_name, t.total_trans, t.trans_date, t.paid, 
                    t.is_taken, t.is_paid_off, t.is_keeped, t.ref, t.sum_note, t.custId ,t.total_after_discount
    FROM trans_sum_table t 
    JOIN trans_detail_table AS td ON t.sum_id = td.sum_id 
    WHERE 
        (:name IS NULL OR td.trans_item_name IS NULL OR td.trans_item_name LIKE '%' || :name || '%')
        AND (:startDate IS NULL OR t.trans_date >= :startDate)
        AND (:endDate IS NULL OR t.trans_date <= :endDate)
    ORDER BY t.trans_date DESC
""")
    fun getFilteredDataOld( startDate: Date?, endDate: Date?,name:String?): List<TransactionSummary>

    @Query("""
    SELECT DISTINCT t.sum_id, t.cust_name, t.total_trans, t.trans_date, t.paid, 
                    t.is_taken, t.is_paid_off, t.is_keeped, t.ref, t.sum_note, t.custId ,t.total_after_discount
    FROM trans_sum_table t 
    JOIN trans_detail_table AS td ON t.sum_id = td.sum_id 
    WHERE 
        (:name IS NULL OR td.trans_item_name IS NULL OR td.trans_item_name LIKE '%' || :name || '%')
        AND (:startDate IS NULL OR t.trans_date >= :startDate)
        AND (:endDate IS NULL OR t.trans_date <= :endDate)
    ORDER BY t.trans_date DESC
    LIMIT :limit OFFSET :offset
""")
    fun getFilteredData4( startDate: Date?, endDate: Date?,name:String?,limit:Int,offset:Int): List<TransactionSummary>

    @Query("""
    SELECT COUNT(*) 
    FROM trans_sum_table t
    WHERE 
    (
        (:name IS NOT NULL AND (
            t.cust_name LIKE '%' || :name || '%'
            OR t.sum_id IN (
                SELECT DISTINCT td.sum_id 
                FROM trans_detail_table td
                WHERE td.trans_item_name LIKE '%' || :name || '%'
            )
        ))
        OR (:name IS NULL)
    )
    AND (:startDate IS NULL OR t.trans_date >= :startDate)
    AND (:endDate IS NULL OR t.trans_date <= :endDate)
""")
    fun getTransactionCount(startDate: Date?, endDate: Date?,name:String?):Int


    @Query("""
    SELECT sum(t.total_after_discount) 
    FROM trans_sum_table t
    WHERE 
    (
        (:name IS NOT NULL AND (
            t.cust_name LIKE '%' || :name || '%'
            OR t.sum_id IN (
                SELECT DISTINCT td.sum_id 
                FROM trans_detail_table td
                WHERE td.trans_item_name LIKE '%' || :name || '%'
            )
        ))
        OR (:name IS NULL)
    )
    AND (:startDate IS NULL OR t.trans_date >= :startDate)
    AND (:endDate IS NULL OR t.trans_date <= :endDate)
""")
    fun getTotalAfterDiscount(startDate: Date?, endDate: Date?,name:String?):Double



    @Query("DELETE FROM trans_sum_table WHERE sum_id IN (:sumIds)")
    suspend fun deleteTransactionSummaries(sumIds: List<Int>)

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
    @Query("SELECT * FROM trans_sum_table ORDER BY trans_date")
    fun getAllTransactionSumList():List<TransactionSummary>
}