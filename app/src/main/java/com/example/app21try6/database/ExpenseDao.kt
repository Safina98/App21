package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expenses: Expenses)
    @Update
    fun update(expenses: Expenses)
    @Query("SELECT * FROM expenses_table")
    fun getAllExpense():LiveData<List<Expenses>>
}