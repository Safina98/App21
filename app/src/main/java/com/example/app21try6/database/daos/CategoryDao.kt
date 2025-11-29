package com.example.app21try6.database.daos

import android.hardware.camera2.CameraExtensionSession.StillCaptureLatency
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.tables.Category
import com.example.app21try6.stock.brandstock.CategoryModel
import com.example.app21try6.stock.brandstock.StockCategoryModel

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category):Long
    @Update
    fun update(category: Category)
    @Query("DELETE FROM category_table WHERE categoryCloudId=:id")
    fun delete(id:Long)
    @Query("SELECT * FROM category_table WHERE categoryCloudId = :id LIMIT 1")
    suspend fun getById(id: Long): Category?

    @Query("SELECT categoryCloudId AS id, category_name AS categoryName FROM category_table")
    fun getCategoryModelList():LiveData<List<StockCategoryModel>>

    @Query("SELECT category_name FROM category_table")
    fun getName():LiveData<List<String>>
    @Query("SELECT category_name FROM category_table")
    fun getAllCategoryName():List<String>
    @Query("SELECT categoryCloudId FROM category_table WHERE category_name = :name")
    fun getCategoryId(name:String):Long
    @Query("SELECT category_name FROM category_table WHERE categoryCloudId=:id")
    fun getCategoryNameById(id:Long):String
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