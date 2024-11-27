package com.example.app21try6.database.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.statement.purchase.tagp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface InventoryLogDao {
    @Insert
    fun insertInventoryLogs(inventoryLog: List<InventoryLog>)
    @Insert
    fun insertDetailWarna(detailWarnaTable: DetailWarnaTable)

    @Query("SELECT * FROM inventory_log_table")
    fun selectAllInventoryLogLD():LiveData<List<InventoryLog>>

    @Query("SELECT * FROM detail_warna_table")
    fun selectAllDetailWarna():List<DetailWarnaTable>

    @Query("SELECT * FROM inventory_log_table")
    fun selectAllInventoryLog():List<InventoryLog>


        @Query("SELECT * FROM detail_warna_table WHERE net = :net AND subId = :subId")
        fun getDetailByNetAndSubId(net: Double, subId: Int): DetailWarnaTable?

        @Update
        fun updateDetail(detail: DetailWarnaTable)

        @Insert
        fun insertDetail(detail: DetailWarnaTable)

        @Transaction
        fun upsertDetail(detail: DetailWarnaTable):String {
            val existingDetail = getDetailByNetAndSubId(detail.net, detail.subId)
            var ref:String
            Log.e(tagp,"$detail")
            if (existingDetail != null) {
                val updatedDetail = existingDetail.copy(
                    batchCount = existingDetail.batchCount + detail.batchCount
                )
                updateDetail(updatedDetail)
                ref=existingDetail.ref
            } else {
                insertDetail(detail)
                ref=detail.ref
            }
            return ref
        }

        @Transaction
        fun updateInsertDetailWarnaAndLog(
            detailList: List<DetailWarnaTable>,
            inventoryLogList: MutableList<InventoryLog>
        ) {
            //Log.e(tagp,"$inventoryLogList")
            for (detail in detailList) {
               val newRef= upsertDetail(detail)
                val matchingLog = inventoryLogList.find { it.detailWarnaRef == detail.ref }

                // Update the InventoryLog's detailWarnaRef to the newRef in the list
                matchingLog?.let {
                    val updatedLog = it.copy(detailWarnaRef = newRef)

                    // Replace the updated log in the mutable list
                    val index = inventoryLogList.indexOf(it)
                    if (index != -1) {
                        inventoryLogList[index] = updatedLog
                    }
                }
            }
            insertInventoryLogs(inventoryLogList)


            val detailWarnaList= selectAllDetailWarna()
            val inventoryLog= selectAllInventoryLog()

            Log.i(tagp,"$detailWarnaList")
            Log.i(tagp,"$inventoryLog")
        }


}