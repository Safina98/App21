package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseCategoryDao {
    @Insert
    fun insert(expenseCategory: ExpenseCategory)
    @Update
    fun update(expenseCategory: ExpenseCategory)
    @Query("SELECT expense_category_name FROM expense_category_table")
    fun getAllExpenseCategory():List<String>
    @Query("SELECT expense_category_name FROM expense_category_table WHERE id =:id")
    fun getExpenseCategoryNameById(id:Int?):String?
    @Query("SELECT id FROM expense_category_table WHERE expense_category_name=:name")
    fun getECIdByName(name:String):Int?
}