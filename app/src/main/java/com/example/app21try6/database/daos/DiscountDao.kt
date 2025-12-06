package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.statement.DiscountAdapterModel

@Dao
interface DiscountDao {
    @Insert
    fun insert(discountTable: DiscountTable)
    @Update
    fun update(discountTable: DiscountTable)

    @Query("SELECT " +
            "discountId as id," +
            "discountName as discountName, " +
            "discountType as discountType, " +
            "minimumQty as minimumQty, " +
            "discountValue as discountValue, " +
            "custLocation as custLocation " +
            "FROM discount_table")
    fun getAllDiscount():LiveData<List<DiscountAdapterModel>>

    @Query("""
        UPDATE discount_table
        SET discountCloudId =:cloudId
        WHERE discountId=:id
    """)
    fun assignDiscountCloudID(cloudId:Long,id:Int)
    
    @Query("SELECT * FROM discount_table")
    fun selectAllDiscountTable(): List<DiscountTable>


    @Query("SELECT * FROM discount_table")
    fun getAllDiscountList():List<DiscountTable>
    @Query("SELECT discountName FROM discount_table")
    fun getAllDiscountName():LiveData<List<String>>
    @Query("SELECT discountName FROM discount_table WHERE discountId =:id")
    fun getDiscountNameById(id:Int?):String?
    @Query("SELECT discountId FROM discount_table WHERE discountName =:name")
    fun getDiscountIdByName(name:String):Int?
    @Query("DELETE  FROM discount_table WHERE discountId =:id")
    fun delete(id:Int)
}