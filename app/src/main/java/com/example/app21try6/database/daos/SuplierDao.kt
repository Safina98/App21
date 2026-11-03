package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app21try6.database.models.PurchaseExpenseRow
import com.example.app21try6.database.models.PurchaseOrderRaw
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.SuplierTable

@Dao
interface SuplierDao {
    @Insert
    suspend fun insert(list: List<SuplierTable>)

    @Query("DELETE  FROM suplier_table")
    fun delete()

    @Query("SELECT suplierName FROM suplier_table")
    fun getAllSuplier():LiveData<List<String>>

    @Query("SELECT id  FROM suplier_table WHERE suplierName =:name")
    suspend fun getSuplierIdByName(name:String):Long

    @Query("SELECT suplierName FROM suplier_table WHERE id =:id ")
    suspend fun getSuplierNameById(id:Long?):String?


    
    @Query("SELECT * FROM suplier_table ")
    fun selectAllSuplierTable(): List<SuplierTable>

    @Query(
        """
        SELECT
            s.sPCloudId AS subId,
            s.productCloudId AS productId,
            s.sub_name AS subName,
            (
                COALESCE((SELECT SUM(d.batchCount * d.net)
                          FROM detail_warna_table d
                          WHERE d.sPCloudId = s.sPCloudId AND d.is_deleted = 0), 0)
                +
                COALESCE((SELECT SUM(m.net)
                          FROM merchandise_table m
                          WHERE m.sPCloudId = s.sPCloudId), 0)
            ) AS totalNet,
            p.default_net AS defaultNet,
            COALESCE((SELECT SUM(t.qty * t.unit_qty)
                      FROM trans_detail_table t
                      WHERE t.sPCloudId = s.sPCloudId
                        AND t.trans_detail_date >= :since30), 0) AS totalSells30Days,
            COALESCE((SELECT SUM(t.qty * t.unit_qty)
                      FROM trans_detail_table t
                      WHERE t.sPCloudId = s.sPCloudId
                        AND t.trans_detail_date >= :since90), 0) AS totalSells90Days
        FROM product_table p
        INNER JOIN sub_table s ON s.productCloudId = p.productCloudId
        WHERE p.suplier_id = :suplierId
        ORDER BY p.product_name, s.sub_name
        """
    )
    suspend fun getPurchaseOrderRaw(
        suplierId: Long,
        since30: String,
        since90: String
    ): List<PurchaseOrderRaw>


    @Query("""
        SELECT sp.productCloudId AS productCloudId,
               e.expense_name    AS expenseName,
               e.expense_date    AS expenseDate
        FROM inventory_purchase_table ip
        INNER JOIN sub_table sp      ON sp.sPCloudId = ip.sPCloudId
        INNER JOIN expenses_table e  ON e.id = ip.expensesId
        WHERE ip.is_deleted = 0 AND sp.is_deleted = 0 AND e.is_deleted = 0
    """)
    suspend fun getPurchaseExpenseRows(): List<PurchaseExpenseRow>

    @Query("SELECT * FROM suplier_table WHERE is_deleted = 0")
    suspend fun getAllSuppliers(): List<SuplierTable>

    @Query("""
        UPDATE product_table
        SET suplier_id = :supplierId, needs_syncs = 1
        WHERE productCloudId = :productId
    """)
    suspend fun updateProductSupplier(productId: Long, supplierId: Long)

}