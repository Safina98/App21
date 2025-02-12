package com.example.app21try6.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.DetailWarnaTable

@Dao
interface DetailWarnaDao {
    @Insert
    fun insert(detailWarnaTable: DetailWarnaTable)
    @Update
    fun update(detailWarnaTable: DetailWarnaTable)
    @Query("DELETE FROM detail_warna_table WHERE detailId=:id")
    fun delete(id:Int)
    @Query("SELECT * FROM detail_warna_table WHERE subId=:subId")
    fun getDetailWarnaBySubId(subId:Int):List<DetailWarnaTable>
}