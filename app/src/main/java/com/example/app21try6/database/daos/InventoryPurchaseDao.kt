package com.example.app21try6.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase

@Dao
interface InventoryPurchaseDao {


    @Query("SELECT *FROM inventory_purchase_table WHERE expensesId=:exId")
    fun selectPurchaseList(exId:Int):List<InventoryPurchase>
    @Insert
    fun insertPurchases(list: List<InventoryPurchase>)
    @Insert
    fun insertPurchase(inventoryPurchase: InventoryPurchase)
    @Update
    fun updatePurchase(inventoryPurchase: InventoryPurchase)
    @Delete
    fun deletePurchases(purchaseList: List<InventoryPurchase>)



    @Insert
    fun insertLog(list: List<InventoryLog>)
    @Query("SELECT * FROM detail_warna_table WHERE subId = :subId AND net = :net LIMIT 1")
    suspend fun getDetailWarnaBySubIdAndNet(subId: Int, net: Double): DetailWarnaTable?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetailWarna(detailWarna: DetailWarnaTable): Long

    @Query("UPDATE detail_warna_table SET batchCount = batchCount + :batchCount WHERE subId = :subId AND net = :net")
    suspend fun updateBatchCount(subId: Int, net: Double, batchCount: Double)

    @Insert
    fun insertExpense(expenses: Expenses):Long
    @Update
    fun updateExpense(expenses: Expenses)
    @Transaction
    suspend fun updateSubProductAndTransDetail(
        detailWarnaList:List<DetailWarnaTable>,
        inventoryLogList:List<InventoryLog>

    ) {
        insertLog(inventoryLogList)
        //insert detailWarnaTable if combination of net and sub id not exist, if ecist than update the batch count (olad batch count = new batchcount + onld batch councount)
        for (detailWarna in detailWarnaList) {
            val existingDetail = getDetailWarnaBySubIdAndNet(detailWarna.subId, detailWarna.net)
            if (existingDetail == null) {
                // Insert new entry if not exists
                insertDetailWarna(detailWarna)
            } else {
                // Update batch count if exists
                updateBatchCount(
                    detailWarna.subId,
                    detailWarna.net,
                    detailWarna.batchCount
                )
            }
        }
    }
    @Transaction
    suspend fun insertPurchaseAndExpense(
        expenses: Expenses,
        purchaseList: List<InventoryPurchase>

    ) {
        val id = insertExpense(expenses).toInt()
        insertPurchases(purchaseList.map { it.apply { expensesId = id } })

    }
    @Transaction
    suspend fun updatePurchasesAndExpense(
        expenses: Expenses,
        purchaseList: List<InventoryPurchase>

    ) {
        updateExpense(expenses)
        val allPurchaseByExpense = selectPurchaseList(expenses.id)
        val purchasesToDelete = allPurchaseByExpense.filter { dbPurchase ->
            purchaseList.none { newPurchase -> newPurchase.id == dbPurchase.id }
        }
        // Delete the purchases
        deletePurchases(purchasesToDelete)

        purchaseList.forEach {
            if (it.id==0){
                it.expensesId=expenses.id
                insertPurchase(it)
            }else{
                updatePurchase(it)
            }
        }

    }
}