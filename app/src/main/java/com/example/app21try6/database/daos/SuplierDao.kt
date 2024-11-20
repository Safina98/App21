package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app21try6.database.tables.SuplierTable

@Dao
interface SuplierDao {
    @Insert
    suspend fun insert(list: List<SuplierTable>)

    @Query("SELECT * FROM suplier_table")
    fun getAllSuplier():LiveData<List<SuplierTable>>
}