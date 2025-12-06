package com.example.app21try6.database.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail

@Dao
interface DetailWarnaDao {
    @Insert
    fun insert(detailWarnaTable: DetailWarnaTable)
    @Insert
    fun insertLog(inventoryLog: InventoryLog)
    @Update
    fun update(detailWarnaTable: DetailWarnaTable)
    @Update
    fun updateTransDetail(transactionDetail: TransactionDetail)

    @Insert
    fun insert(merchandiseRetail: MerchandiseRetail)
    @Update
    fun updateRetail(merchandiseRetail: MerchandiseRetail)

    @Query("""
        UPDATE detail_warna_table
        SET dWCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignDetailWarnaCloudID(cloudId:Long,id:Int)

    @Query("SELECT * FROM detail_warna_table")
    fun selectAllDetailWarnaTable(): List<DetailWarnaTable>

    @Query("""
        UPDATE merchandise_table
        SET mRCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignMerchandiseRetailCloudID(cloudId:Long,id:Int)

    @Query("SELECT * FROM merchandise_table")
    fun selectAllMerchandiseRetailTable(): List<MerchandiseRetail>

    @Query("DELETE FROM detail_warna_table WHERE id=:id")
    fun delete(id:Int)

    @Query("DELETE FROM detail_warna_table WHERE subId=:subId")
    fun deleteDetailWarnaBySubId(subId:Int)

    @Query("DELETE FROM merchandise_table WHERE id =:id")
    fun deleteMerchandise(id:Int)

    @Query("SELECT \n" +
            "    id, \n" +
            "    sub_id AS sub_id, \n" +
            "    ref, \n" +
            "    net, \n" +
            "    NULL AS batchCount, \n" +
            "    NULL AS ket, \n" +
            "    0 AS selectedQty, \n" +
            "    date AS date \n" +
            "FROM merchandise_table \n WHERE sub_id=:subId")
    fun getRetaiBySubId(subId:Int):List<DetailMerchandiseModel>

    @Query("SELECT \n" +
            "    id, \n" +
            "    subId AS sub_id, \n" +
            "    ref, \n" +
            "    net, \n" +
            "    batchCount, \n" +
            "    ket, \n" +
            "    0 AS selectedQty, \n" +
            "    NULL AS date \n" +
            "FROM detail_warna_table\n WHERE subId=:subId")
    fun getDetailWarnaBySubId(subId:Int):List<DetailMerchandiseModel>
    //if the combination of net and sub_id exist update, if not insert
    @Query("SELECT * FROM detail_warna_table WHERE subId = :subId AND net = :net LIMIT 1")
    fun getDetailBySubIdAndNet(subId: Int, net: Double): DetailWarnaTable?

    @Transaction
    fun insertDetailWarnaAndLog(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog) {
        insert(detailWarnaTable)
        insertLog(inventoryLog)
    }
    @Transaction
    fun updateDetailWarnaAndInsertLog(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?) {
        update(detailWarnaTable)
        insertLog(inventoryLog)
        if (merchandiseRetail!=null) {
            insert(merchandiseRetail)
        }//TODO insert merchandise retail
    }
    @Transaction
    fun updateDetailWarnaAndInsertLog(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: List<MerchandiseRetail?>) {
        update(detailWarnaTable)
        insertLog(inventoryLog)
        merchandiseRetail.forEach {
            if (it!=null){
                insert(it)
            }

            }
    }
    @Transaction
    fun deleteDetailWarnaAndInsertLog(detailWarnaId:Int,inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        insertLog(inventoryLog)
        delete(detailWarnaId)
        if (merchandiseRetail!=null) {
            insert(merchandiseRetail)
        }
    }
    @Transaction
    fun deleteDetailWarnaAndInsertLog(detailWarnaId:Int,inventoryLog: InventoryLog,merchandiseRetail: List<MerchandiseRetail?>){
        insertLog(inventoryLog)
        delete(detailWarnaId)
        merchandiseRetail.forEach {
            if (it!=null) {
                insert(it)
            }
        }
    }

    @Transaction
    fun updateTransDetailAndRetail(merchandiseRetail: MerchandiseRetail,transactionDetail: TransactionDetail){
        updateRetail(merchandiseRetail)
        updateTransDetail(transactionDetail)
    }
}