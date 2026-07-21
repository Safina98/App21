package com.example.app21try6.database.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.PurchaseDiscount

@Dao
interface InventoryPurchaseDao {


    @Query("SELECT *FROM inventory_purchase_table WHERE expensesId=:exId")
    fun selectPurchaseListOld(exId:Int):List<InventoryPurchase>

    @Query("SELECT *FROM inventory_purchase_table WHERE expensesId=:exId")
    fun selectPurchaseList(exId:Int): LiveData<List<InventoryPurchase>>


    @Insert
    fun insertPurchases(list: List<InventoryPurchase>)
    @Insert
    fun insertPurchase(inventoryPurchase: InventoryPurchase):Long
    @Insert
    fun insertPDiscount(pDiscount: PurchaseDiscount)
    @Update
    fun updatePurchase(inventoryPurchase: InventoryPurchase)

    @Delete
    fun deletePurchase(inventoryPurchase: InventoryPurchase)
    @Delete
    fun deletePurchases(purchaseList: List<InventoryPurchase>)

    @Query("DELETE FROM purchase_discount_table WHERE id=:id")
    fun deletePDiscount(id:Long)

    @Query("UPDATE purchase_discount_table SET discountValue=:newValue WHERE id=:id")
    fun updatePDiscount(id:Long, newValue:Int)



    @Insert
    fun insertLog(list: List<InventoryLog>)
    @Query("SELECT * FROM detail_warna_table WHERE sPCloudId = :subId AND net = :net LIMIT 1")
    suspend fun getDetailWarnaBySubIdAndNet(subId: Long, net: Double): DetailWarnaTable?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetailWarna(detailWarna: DetailWarnaTable): Long

    @Query("SELECT * FROM inventory_purchase_table WHERE id = :id")
    suspend fun getPurchaseById(id:Int): InventoryPurchase?

    @Query("UPDATE detail_warna_table SET batchCount = batchCount + :batchCount WHERE sPCloudId = :subId AND net = :net")
    suspend fun updateBatchCount(subId: Long, net: Double, batchCount: Double)

    @Query("SELECT SUM(totalPrice) FROM inventory_purchase_table WHERE expensesId =:eId")
    fun getPurchaseSum(eId:Int): Double

    @Query("SELECT SUM(discountValue) FROM purchase_discount_table WHERE expenseId =:eId")
    fun getDiscountSum(eId:Int): Double

    @Insert
    fun insertExpense(expenses: Expenses):Long
    @Update
    fun updateExpense(expenses: Expenses)

    @Query("UPDATE expenses_table SET expense_ammount =:newAmmount WHERE id=:eId")
    fun updateExpenseAmmount(eId:Int,newAmmount: Double)



    @Transaction
    suspend fun updateSubProductAndTransDetail(
        detailWarnaList:List<DetailWarnaTable>,
        inventoryLogList:List<InventoryLog>

    ) {
        insertLog(inventoryLogList)
        //insert detailWarnaTable if combination of net and sub id not exist, if ecist than update the batch count (olad batch count = new batchcount + onld batch councount)
        for (detailWarna in detailWarnaList) {
            val existingDetail = getDetailWarnaBySubIdAndNet(detailWarna.sPCloudId, detailWarna.net)
            if (existingDetail == null) {
                // Insert new entry if not exists
                insertDetailWarna(detailWarna)
            } else {
                // Update batch count if exists
                updateBatchCount(
                    detailWarna.sPCloudId,
                    detailWarna.net,
                    detailWarna.batchCount
                )
            }
        }
    }

    @Transaction
    suspend fun upsertPurchaseAndUpdateExpense(inventoryPurchase: InventoryPurchase){
        if (inventoryPurchase.id==0L) {
            inventoryPurchase.id= System.currentTimeMillis()
            insertPurchase(inventoryPurchase)
        } else
            updatePurchase(inventoryPurchase)

        val expenseSum=getPurchaseSum(inventoryPurchase.expensesId!!)
        val totalDiscount=getDiscountSum(inventoryPurchase.expensesId!!)
        val newExpenseSum=expenseSum-totalDiscount
        updateExpenseAmmount(inventoryPurchase.expensesId!!,newExpenseSum)
    }
    @Transaction
    suspend fun deletePurchaseAndUpdateExpense(inventoryPurchase: InventoryPurchase){
        deletePurchase(inventoryPurchase)
        val expenseSum=getPurchaseSum(inventoryPurchase.expensesId!!)
        val totalDiscount=getDiscountSum(inventoryPurchase.expensesId!!)
        val newExpenseSum=expenseSum-totalDiscount
        updateExpenseAmmount(inventoryPurchase.expensesId!!,newExpenseSum)
    }

    @Transaction
    suspend fun insertPDiscountAndUpdateExpense(pDiscount: PurchaseDiscount){
        insertPDiscount(pDiscount)
        val expenseSum=getPurchaseSum(pDiscount.expenseId!!)
        val totalDiscount=getDiscountSum(pDiscount.expenseId!!)
        val newExpenseSum=expenseSum-totalDiscount
        updateExpenseAmmount(pDiscount.expenseId!!,newExpenseSum)
            }
    @Transaction
    suspend fun deletePDiscountAndUpdateExpense(pDiscount: PaymentModel){
        deletePDiscount(pDiscount.tSCloudId!!)
        val expenseSum=getPurchaseSum(pDiscount.id!!)
        val totalDiscount=getDiscountSum(pDiscount.id!!)
        val newExpenseSum=expenseSum-totalDiscount
        updateExpenseAmmount(pDiscount.id!!,newExpenseSum)
    }
    @Transaction
    suspend fun updatePDiscountAndUpdateExpense(pDiscount: PurchaseDiscount){

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
               // purchaseList.map { it.apply { purchaseDate=expenses.expense_date?: Date() } }
                purchaseList.forEach{
                 //   it.iPCloudId=System.currentTimeMillis()
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
        val allPurchaseByExpense = selectPurchaseListOld(expenses.id)
        val purchasesToDelete = allPurchaseByExpense.filter { dbPurchase ->
            purchaseList.none { newPurchase -> newPurchase.id == dbPurchase.id }
        }
        // Delete the purchases
        deletePurchases(purchasesToDelete)

        purchaseList.forEach {
            it.needsSyncs=1
            Log.i("SavePurchaseProbs","$it")
        }

       // purchaseList.map { it.apply { purchaseDate=expenses.expense_date?:Date()} }
        purchaseList.forEach {
            if (it.id<0){
                Log.i("SavePurchaseProbs","Insert $it")
                it.expensesId=expenses.id
                it.id=0
                //it.iPCloudId=System.currentTimeMillis()
                var id = insertPurchase(it)
                var purhcaseItem=getPurchaseById(id.toInt())
                Log.i("SavePurchaseProbs","pId $purhcaseItem")
            }else{
                updatePurchase(it)
                Log.i("SavePurchaseProbs","update ${it}")
            }
        }

    }




//    @Query("SELECT id as id,subProductName as nama,purchaseDate as date FROM inventory_purchase_table")
//    fun selectNameAndDate(): List<StringDateModel?>?
//    @Query("SELECT id as id,subProductName as nama,purchaseDate as date FROM inventory_purchase_table where id=:id")
//    fun selectADataNameAndDate(id:Int):StringDateModel




    
    @Query("SELECT * FROM inventory_purchase_table")
    fun selectAllInventoryPurchaseTable(): List<InventoryPurchase>
}