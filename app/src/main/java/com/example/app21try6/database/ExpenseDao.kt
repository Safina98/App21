package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.statement.DiscountAdapterModel

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expenses: Expenses)
    @Update
    fun update(expenses: Expenses)
    @Query("SELECT " +
            "expenses_table.id as id, " +
            "expenses_table.expense_name as expense_name, " +
            "expenses_table.expense_ammount as expense_ammount, " +
            "expenses_table.expense_date as date, " +
            "expense_category_table.expense_category_name as expense_category_name " +  // Assuming you want to select category_name
            "FROM expenses_table " +
            "LEFT JOIN expense_category_table ON expenses_table.expense_category_id = expense_category_table.id"  // Correct join condition
    )
    fun getAllExpense():LiveData<List<DiscountAdapterModel>>

    @Query("DELETE FROM expenses_table WHERE id=:id")
    fun delete(id:Int)
}