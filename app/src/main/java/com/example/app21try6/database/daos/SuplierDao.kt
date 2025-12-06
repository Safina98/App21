package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.SuplierTable

@Dao
interface SuplierDao {
    @Insert
    suspend fun insert(list: List<SuplierTable>)

    @Query("SELECT * FROM suplier_table")
    fun getAllSuplier():LiveData<List<SuplierTable>>

    @Query("""
        UPDATE suplier_table
        SET suplierCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignSuplierCloudID(cloudId:Long,id:Int)
    
    @Query("SELECT * FROM suplier_table ")
    fun selectAllSuplierTable(): List<SuplierTable>


}