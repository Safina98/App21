package com.example.app21try6.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog

@Dao
interface DetailWarnaDao {
    @Insert
    fun insert(detailWarnaTable: DetailWarnaTable)
    @Insert
    fun insertLog(inventoryLog: InventoryLog)
    @Update
    fun update(detailWarnaTable: DetailWarnaTable)

    @Query("DELETE FROM detail_warna_table WHERE detailId=:id")
    fun delete(id:Int)
    @Query("SELECT * FROM detail_warna_table WHERE subId=:subId")
    fun getDetailWarnaBySubId(subId:Int):List<DetailWarnaTable>
    //if the combination of net and sub_id exist update, if not insert
    @Query("SELECT * FROM detail_warna_table WHERE subId = :subId AND net = :net LIMIT 1")
    fun getDetailBySubIdAndNet(subId: Int, net: Double): DetailWarnaTable?
    @Query("UPDATE detail_warna_table SET batchCount = batchCount + :newBatchCount WHERE detailId = :detailId")
    fun updateBatchCount(detailId: Int, newBatchCount: Double)


    @Transaction
    fun insertDetailWarnaAndLog(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog) {
        insert(detailWarnaTable)
        insertLog(inventoryLog)
    }
    @Transaction
    fun updateDetailWarnaAndInsertLog(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog) {
        update(detailWarnaTable)
        insertLog(inventoryLog)
    }
}