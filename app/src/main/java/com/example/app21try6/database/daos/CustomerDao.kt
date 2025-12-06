package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.Product

@Dao
interface CustomerDao {
    @Insert
    fun insert(customerTable: CustomerTable)

    @Update
    fun update(customerTable: CustomerTable)

    @Delete
    fun delete(customerTable: CustomerTable)

    @Query("""
        UPDATE customer_table
        SET customerCloudId =:cloudId
        WHERE custId=:id
    """)
    fun assignCustomerCloudID(cloudId:Long,id:Int)
    
    @Query("SELECT * FROM customer_table")
    fun selectAllCustomerTable(): List<CustomerTable>


    @Query("SELECT custId FROM customer_table WHERE customerBussinessName=:name")
    fun getIdByName(name:String):Int?

    @Query("SELECT * FROM customer_table WHERE customerBussinessName=:id")
    fun getCustomerByName(id:String): CustomerTable?

    @Query("SELECT * FROM customer_table")
    fun allCustomer():LiveData<List<CustomerTable>>

    @Query("""
    SELECT *
    FROM customer_table
    WHERE customerCloudId IN (
        SELECT customerCloudId
        FROM customer_table
        GROUP BY customerCloudId
        HAVING COUNT(customerCloudId) > 1
    )
    ORDER BY customerCloudId
""")
    fun getCustomersWithDuplicateCloudId(): List<CustomerTable>
}