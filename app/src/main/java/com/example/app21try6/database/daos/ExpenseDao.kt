package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.statement.DiscountAdapterModel

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expenses: Expenses):Long

    @Query("SELECT * FROM expenses_table WHERE id=:id")
    fun getExpenseById(id:Int):Expenses
    @Update
    fun update(expenses: Expenses)
    @Query("SELECT " +
            "expenses_table.id as id, " +
            "expenses_table.expense_name as expense_name, " +
            "expenses_table.expense_ammount as expense_ammount, " +
            "expenses_table.expense_date as date, " +
            "expenses_table.expense_ref as expense_ref, " +
            "expense_category_table.expense_category_name as expense_category_name " +  // Assuming you want to select category_name
            "FROM expenses_table " +
            "LEFT JOIN expense_category_table ON expenses_table.expense_category_id = expense_category_table.id"  // Correct join condition
    )
    fun getAllExpense():LiveData<List<DiscountAdapterModel>>


    @Query("""SELECT SUM(expense_ammount) as total FROM expenses_table
        LEFT JOIN expense_category_table ON expenses_table.expense_category_id = expense_category_table.id  
            WHERE (:ceId IS NULL OR expenses_table.expense_category_id = :ceId)
        AND (:startDate IS NULL OR expense_date >= :startDate)
        AND (:endDate IS NULL OR expense_date <= :endDate)
    """)
    fun getExpenseSum( startDate: String?, endDate: String?,ceId:Int?):Int

    @Query("""
   SELECT 
            expenses_table.id as id,  
            expenses_table.expense_name as expense_name,  
            expenses_table.expense_ammount as expense_ammount, 
            expenses_table.expense_date as date,  
            expenses_table.expense_ref as expense_ref,  
            expense_category_table.expense_category_name as expense_category_name  
            FROM expenses_table 
            LEFT JOIN expense_category_table ON expenses_table.expense_category_id = expense_category_table.id  
            WHERE (:ceId IS NULL OR expenses_table.expense_category_id = :ceId)
        AND (:startDate IS NULL OR expense_date >= :startDate)
        AND (:endDate IS NULL OR expense_date <= :endDate)
    ORDER BY expense_date DESC
""")// Correct join condition
    fun getAllExpense( startDate: String?, endDate: String?,ceId:Int?): List<DiscountAdapterModel>
    @Query("DELETE FROM expenses_table WHERE id=:id")
    fun delete(id:Int)
}