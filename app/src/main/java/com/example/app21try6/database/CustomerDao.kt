package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDao {
    @Insert
    fun insert(customerTable: CustomerTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerTable>)

    @Query("SELECT * FROM customer_table")
    fun selectAll():List<CustomerTable>

    @Query("SELECT * FROM customer_table WHERE custId=:id")
    fun getCustomerById(id:Int):CustomerTable?

    @Query("SELECT custId FROM customer_table WHERE customerBussinessName=:name")
    fun getIdByName(name:String):Int?

    @Query("SELECT * FROM customer_table WHERE customerBussinessName=:id")
    fun getCustomerByName(id:String):CustomerTable?

    @Query("SELECT * FROM customer_table")
    fun allCustomer():LiveData<List<CustomerTable>>
}