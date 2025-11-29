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

    @Delete
    fun delete_(vararg stok: TransactionSummary)

    @Query("SELECT * FROM trans_sum_table WHERE  is_taken = :bool")
    fun getActiveSumList(bool:Boolean):List<TransactionSummary>

    @Query("SELECT * FROM trans_sum_table WHERE sum_id = :sum_id_")
    suspend fun getTrans(sum_id_:Int): TransactionSummary


    @Query("""
    SELECT DISTINCT ts.* 
    FROM trans_sum_table AS ts
    LEFT JOIN trans_detail_table td ON ts.sum_id = td.sum_id
    WHERE 
        -- Case 1: Search by sumId (ignore date)
        (:sumId IS NOT NULL AND ts.sum_id = :sumId)
        
        OR 
        
        -- Case 2: Search by name (with date filter)
        (:name IS NOT NULL AND (
            ts.cust_name LIKE '%' || :name || '%'
            OR td.trans_item_name LIKE '%' || :name || '%'
        )
        AND (:startDate IS NULL OR ts.trans_date >= :startDate)
        AND (:endDate IS NULL OR ts.trans_date <= :endDate))
        
        OR 
       
        -- Case 3: No name and no sumId → return all within date range
        (:name IS NULL AND :sumId IS NULL
        AND (:startDate IS NULL OR ts.trans_date >= :startDate)
        AND (:endDate IS NULL OR ts.trans_date <= :endDate))
        
    ORDER BY ts.trans_date DESC
    LIMIT :limit OFFSET :offset
""")
    fun getTransactionSummariesByItemName(name: String?, sumId: Int?,startDate: Date?,endDate: Date?,limit:Int,offset: Int): List<TransactionSummary>

    @Query("SELECT * from trans_sum_table WHERE sum_id = :sum_id_")
    fun getTransSum(sum_id_: Int):LiveData<TransactionSummary>


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
    fun getTransactionSummariesByItemNameDEPRACATED(name: String?, startDate: Date?,endDate: Date?,limit:Int,offset: Int): List<TransactionSummary>

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

