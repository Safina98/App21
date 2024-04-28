package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CategoryDao {
    @Insert
    fun insert(category: Category)
    @Update
    fun update(category: Category)
    @Query("SELECT * FROM category_table")
    fun getAll():LiveData<List<Category>>
    @Query("SELECT category_name FROM category_table")
    fun getName():LiveData<List<String>>
    @Query("SELECT category_name FROM category_table")
    fun getAllCategoryName():List<String>
    @Query("SELECT category_id FROM category_table WHERE category_name = :name")
    fun getCode(name:String):LiveData<Int>
    @Query("SELECT category_id FROM category_table WHERE category_name = :name")
    fun getCategoryId(name:String):Int
    @Query("SELECT * FROM category_table")
    fun getAllItem():List<Category>
    @Query("DELETE FROM category_table WHERE category_name = :category_name_")
    fun clear(category_name_:String)
    @Query("INSERT INTO category_table (category_name) VALUES (:cath_name_)")
    fun insert_try(cath_name_:String)
    @Query("INSERT INTO category_table(category_name) SELECT :cath_name_ WHERE NOT EXISTS (SELECT 1 FROM category_table WHERE category_name = :cath_name_)")
    fun insertIfNotExist(cath_name_:String)
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