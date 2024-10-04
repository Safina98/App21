package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DiscountDao {
    @Insert
    fun insert(discountTable: DiscountTable)
    @Update
    fun update(discountTable: DiscountTable)

    @Query("SELECT * FROM discount_table")
    fun getAllDiscount():LiveData<List<DiscountTable>>
    @Query("SELECT * FROM discount_table")
    fun getAllDiscountList():List<DiscountTable>
    @Query("SELECT discountName FROM discount_table")
    fun getAllDiscountName():LiveData<List<String>>
    @Query("SELECT * FROM discount_table WHERE discountId =:id")
    fun getDiscountById(id:Int):DiscountTable
    @Query("SELECT discountName FROM discount_table WHERE discountId =:id")
    fun getDiscountNameById(id:Int?):String?
    @Query("SELECT discountId FROM discount_table WHERE discountName =:name")
    fun getDiscountIdByName(name:String):Int?
    @Delete
    fun delete(discountTable: DiscountTable)
}