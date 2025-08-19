package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.statement.DiscountAdapterModel
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expenses: Expenses):Long

    @Query("SELECT * FROM expenses_table WHERE id=:id")
    fun getExpenseById(id:Int):Expenses
    @Update
    fun update(expenses: Expenses)


    @Query("""
        SELECT e.id as id,  
            e.expense_name as expense_name,  
            e.expense_ammount as expense_ammount, 
            e.expense_date as date,  
            e.expense_ref as expense_ref,  
            ec.expense_category_name as expense_category_name
        FROM expenses_table e
          LEFT JOIN expense_category_table ec ON e.expense_category_id = ec.id
        WHERE (:ceId IS NULL OR e.expense_category_id = :ceId)
            AND(:selectedYear IS NULL OR strftime('%Y', e.expense_date) = :selectedYear)
          AND (:selectedMonth IS NULL OR strftime('%m', e.expense_date) = :selectedMonth)
          ORDER BY expense_date DESC
    """)
    suspend fun getAllExpense(selectedMonth: String?, selectedYear: String?, ceId:Int?): List<DiscountAdapterModel>
    
    @Query("""SELECT
            e.id as id,  
            e.expense_name as expense_name,  
            e.expense_ammount as expense_ammount, 
            e.expense_date as date,  
            e.expense_ref as expense_ref,  
            ec.expense_category_name as expense_category_name
        FROM expenses_table e
        LEFT JOIN expense_category_table ec ON e.expense_category_id = ec.id
             JOIN inventory_purchase_table AS ip ON e.id = ip.expensesId  
            WHERE ip.subProductName LIKE '%' || :name || '%' 
             AND(:selectedYear IS NULL OR strftime('%Y', e.expense_date) = :selectedYear)
          AND (:selectedMonth IS NULL OR strftime('%m', e.expense_date) = :selectedMonth)
            """)
    fun getExpenseByQuery(name: String, selectedMonth: String?,selectedYear: String?): List<DiscountAdapterModel>

    @Query("DELETE FROM expenses_table WHERE id=:id")
    fun delete(id:Int)

}