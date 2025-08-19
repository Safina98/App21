package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.stock.brandstock.CategoryModel

@Dao
interface ExpenseCategoryDao {
    @Insert
    fun insert(expenseCategory: ExpenseCategory)
    @Update
    fun update(expenseCategory: ExpenseCategory)
    @Query("DELETE FROM expense_category_table WHERE id=:id")
    fun delete(id: Int)
    @Query("SELECT expense_category_name FROM expense_category_table")
    fun getAllExpenseCategoryName():List<String>
    @Query("SELECT id AS id, expense_category_name AS categoryName FROM expense_category_table")
    fun getAllExpenseCategoryModel():LiveData<List<CategoryModel>>

    @Query("SELECT id FROM expense_category_table WHERE expense_category_name=:name")
    fun getECIdByName(name:String):Int?
}