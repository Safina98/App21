package com.example.app21try6.database.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.Constants

import com.example.app21try6.database.models.InventoryLogWithSubProduct
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.statement.purchase.tagp

@Dao
interface InventoryLogDao {
    @Insert
    fun insertInventoryLogs(inventoryLog: List<InventoryLog>)
    @Insert
    fun insertDetailWarna(detailWarnaTable: DetailWarnaTable)

    @Query("""
    SELECT il.*,s.sub_name
    FROM inventory_log_table il
    INNER JOIN sub_table s ON il.sPCloudId = s.sPCloudId
""")
    fun selectAllInventoryLogLD():LiveData<List<InventoryLogWithSubProduct>>

    @Query("SELECT * FROM detail_warna_table")
    fun selectAllDetailWarna():List<DetailWarnaTable>

    @Query("SELECT * FROM inventory_log_table")
    fun selectAllInventoryLog():List<InventoryLog>

    @Delete
    fun deleteLog(log:InventoryLog)


        @Query("SELECT * FROM detail_warna_table WHERE net = :net AND sPCloudId = :subId")
        fun getDetailByNetAndSubId(net: Double, subId: Long): DetailWarnaTable?

        @Update
        fun updateDetail(detail: DetailWarnaTable)

        @Insert
        fun insertDetail(detail: DetailWarnaTable)

        @Transaction
        fun upsertDetail(detail: DetailWarnaTable):String {
            val existingDetail = getDetailByNetAndSubId(detail.net, detail.sPCloudId)
            var ref:String
            Log.e(tagp,"$detail")
            if (existingDetail != null) {
                val updatedDetail = existingDetail.copy(
                    batchCount = existingDetail.batchCount + detail.batchCount
                )
                updatedDetail.needsSyncs=1
                updateDetail(updatedDetail)
                ref=existingDetail.ref
            } else {
                detail.dWCloudId=System.currentTimeMillis()
                detail.needsSyncs=1
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
        }

    @Transaction
    fun updateLogAndDetailWarna(newInventoryLog: InventoryLogWithSubProduct){
        //fin detail warna
        /*
            find the old inventorylog
            if isi of the new inventorylog == isi of the old inventory log
                 if old inventorylog ket is barang keluar
                    detailwarna . pcs = detailwarna .pcs + old inventory log.pcs - new inventory log.pcs
                 else
                    detailwarna . pcs= detailwarna. pcs - old inventory log.pcs   + new inventory log.pcs
                 update detailwarna
            else if new inventory log isi != old inventory log isi
                if old inventory log ket is barang keluar
                detailwarna .pcs  = detailwarna .pcs + oldinventorylog.pcs
                update detailwarna
                newdetail wa


         */
    }

    @Transaction
    fun deleteLogAndUpdateDetailWarna(log:InventoryLogWithSubProduct){
        /* find detail warna with the same sub id and isi
            if log ket barang keluar
                then add detail warna
           else
                then substract detail warna
           delete log
           update detailwarna
         */
        val detailWarna=getDetailByNetAndSubId(log.inventoryLog.isi,log.inventoryLog.sPCloudId!!)!!
        if (log.inventoryLog.barangLogKet== Constants.BARANGLOGKET.keluar) {
            detailWarna.batchCount=detailWarna.batchCount + log.inventoryLog.pcs
        }else {
            detailWarna.batchCount=detailWarna.batchCount - log.inventoryLog.pcs
        }
        deleteLog(log.inventoryLog)
        updateDetail(detailWarna)
        val list=selectAllDetailWarna()
        Log.i("LOGPROBS","$list")
    }

    @Query("""
        UPDATE inventory_log_table
        SET iLCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignInventoryLogCloudID(cloudId:Long,id:Int)

    @Query("SELECT * FROM inventory_log_table")
    fun selectAllInventoryLogTable(): List<InventoryLog>

}