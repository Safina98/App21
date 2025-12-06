package com.example.app21try6.database.daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.StringDateModel
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
import java.util.Date

@Dao
interface InventoryPurchaseDao {


    @Query("SELECT *FROM inventory_purchase_table WHERE expensesId=:exId")
    fun selectPurchaseList(exId:Int):List<InventoryPurchase>
    @Insert
    fun insertPurchases(list: List<InventoryPurchase>)
    @Insert
    fun insertPurchase(inventoryPurchase: InventoryPurchase):Long
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

    @Query("SELECT * FROM inventory_purchase_table WHERE id = :id")
    suspend fun getPurchaseById(id:Int): InventoryPurchase?

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
        // Insert expense and get the ID

        try {
            Log.i("SavePurchaseProbs"," insert $expenses")
            val id = insertExpense(expenses).toInt()

            // Ensure the ID is valid before proceeding
            if (id > 0) {
                purchaseList.map { it.apply { expensesId = id } }
                purchaseList.map { it.apply { it.id=0 } }
                purchaseList.map { it.apply { purchaseDate=expenses.expense_date?: Date() } }
                purchaseList.forEach{
                    it.iPCloudId=System.currentTimeMillis()
                    Log.i("insertPurchaseAndExpense","insert $it")
                }
                // Map the purchases to associate the expensesId, then insert them
                insertPurchases(purchaseList)
            } else {

                // Handle failure, maybe throw an exception or return
            }
        }catch (e:Exception){
            Log.e("insertPurchaseAndExpense", "$e")
        }

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
            it.needsSyncs=1
            Log.i("SavePurchaseProbs","$it")
        }

        purchaseList.map { it.apply { purchaseDate=expenses.expense_date?:Date()} }
        purchaseList.forEach {
            if (it.id<0){
                Log.i("SavePurchaseProbs","Insert $it")
                it.expensesId=expenses.id
                it.id=0
                it.iPCloudId=System.currentTimeMillis()
                var id = insertPurchase(it)
                var purhcaseItem=getPurchaseById(id.toInt())
                Log.i("SavePurchaseProbs","pId $purhcaseItem")
            }else{
                updatePurchase(it)
                Log.i("SavePurchaseProbs","update ${it}")
            }
        }

    }




    @Query("SELECT id as id,subProductName as nama,purchaseDate as date FROM inventory_purchase_table")
    fun selectNameAndDate(): List<StringDateModel?>?
    @Query("SELECT id as id,subProductName as nama,purchaseDate as date FROM inventory_purchase_table where id=:id")
    fun selectADataNameAndDate(id:Int):StringDateModel

    @Query("""
        UPDATE inventory_purchase_table
        SET purchaseDate =:date
        WHERE id=:id
    """)
    fun updatePurchaseDate(id: Int, date:Date)

    @Query("""
        UPDATE inventory_purchase_table
        SET iPCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignInventoryPurchaseCloudID(cloudId:Long,id:Int)
    
    @Query("SELECT * FROM inventory_purchase_table")
    fun selectAllInventoryPurchaseTable(): List<InventoryPurchase>
}