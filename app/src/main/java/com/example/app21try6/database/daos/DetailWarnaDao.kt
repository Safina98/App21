package com.example.app21try6.database.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct

@Dao
interface DetailWarnaDao {
    @Insert
    fun insert(detailWarnaTable: DetailWarnaTable)
    @Insert
    fun insertLog(inventoryLog: InventoryLog)
    @Update
    fun update(detailWarnaTable: DetailWarnaTable)

    @Insert
    fun insert(merchandiseRetail: MerchandiseRetail)
    @Update
    fun updateRetail(merchandiseRetail: MerchandiseRetail)

    @Query("DELETE FROM detail_warna_table WHERE id=:id")
    fun delete(id:Int)

    @Query("DELETE FROM merchandise_table WHERE id =:id")
    fun deleteMerchandise(id:Int)

    @Query("SELECT \n" +
            "    id, \n" +
            "    sub_id AS sub_id, \n" +
            "    ref, \n" +
            "    net, \n" +
            "    NULL AS batchCount, \n" +
            "    NULL AS ket, \n" +
            "    date AS date \n" +
            "FROM merchandise_table \n WHERE sub_id=:subId")
    fun getRetaiBySubId(subId:Int):List<DetailMerchandiseModel>

    @Query("SELECT * FROM merchandise_table WHERE sub_id=:subId")
    fun selectRetailBySubId(subId:Int):List<MerchandiseRetail>

    @Query("SELECT \n" +
            "    id, \n" +
            "    subId AS sub_id, \n" +
            "    ref, \n" +
            "    net, \n" +
            "    batchCount, \n" +
            "    ket, \n" +
            "    NULL AS date \n" +
            "FROM detail_warna_table\n WHERE subId=:subId")
    fun getDetailWarnaBySubId(subId:Int):List<DetailMerchandiseModel>
    //if the combination of net and sub_id exist update, if not insert
    @Query("SELECT * FROM detail_warna_table WHERE subId = :subId AND net = :net LIMIT 1")
    fun getDetailBySubIdAndNet(subId: Int, net: Double): DetailWarnaTable?
    @Query("UPDATE detail_warna_table SET batchCount = batchCount + :newBatchCount WHERE id = :detailId")
    fun updateBatchCount(detailId: Int, newBatchCount: Double)

    @Query("SELECT * FROM brand_table WHERE brand_id = :brandId")
    suspend fun getBrandById(brandId: Int): Brand?

    @Query("SELECT * FROM product_table WHERE product_id = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Query("SELECT * FROM sub_table WHERE sub_id = :subProductId")
    suspend fun getSubProductById(subProductId: Int): SubProduct?

    @Query("SELECT * FROM detail_warna_table WHERE ref = :ref")
    suspend fun getDetailWarnaByRef(ref: String): DetailWarnaTable?


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
    fun deleteDetailWarnaAndInsertLog(detailWarnaId:Int,inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        insertLog(inventoryLog)
        delete(detailWarnaId)
        if (merchandiseRetail!=null) {
            insert(merchandiseRetail)
        }//TODO insert merchandise retail
    }
}