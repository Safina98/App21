package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CustomerDao {
    @Insert
    fun insert(customerTable: CustomerTable)

    @Query("SELECT * FROM customer_table")
    fun selectAll():List<CustomerTable>
    @Query("SELECT * FROM customer_table")
    fun allCustomer():LiveData<List<CustomerTable>>
}