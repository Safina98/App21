package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.PaymentModel

import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.PurchaseDiscount
import com.example.app21try6.database.tables.SuplierTable
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpensesRepository(application: Application) {
    private val expenseDao = VendibleDatabase.getInstance(application).expenseDao
    private val expenseCategoryDao = VendibleDatabase.getInstance(application).expenseCategoryDao
    private val suplierDao= VendibleDatabase.getInstance(application).suplierDao
    private val inventoryPurchaseDao= VendibleDatabase.getInstance(application).inventoryPurchaseDao
    private val subProductDao = VendibleDatabase.getInstance(application).subProductDao

    private val pDiscountDao= VendibleDatabase.getInstance(application).purchaseDiscountDao

    /////////////////////////////Expenxe/////////////////////////////////////////
    suspend fun getFilteredExpense(startDate:String?,endDate:String?,ecId:Int?): List<DiscountAdapterModel>{
        return withContext(Dispatchers.IO) { expenseDao.getAllExpense(startDate,endDate,ecId)}
    }
    suspend fun insertExpense(expenses: Expenses):Int{
        return withContext(Dispatchers.IO){
            expenseDao.insert(expenses).toInt()
        }
    }
    suspend fun updateExpense(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.update(expenses)
        }
    }

    suspend fun getExpensesById(id:Int):Expenses{
        return withContext(Dispatchers.IO){
            expenseDao.getExpenseById(id)
        }
    }
    suspend fun deleteExpensesToDao(id:Int){
        withContext(Dispatchers.IO){
            expenseDao.delete(id)
        }
    }
 suspend fun getExpenseByQuery(query: String,month:String?,year:String?):List<DiscountAdapterModel>{
        return withContext(Dispatchers.IO){

            expenseDao.getExpenseByQuery(query,month,year)
        }
    }
    fun getExpenseIsKeeped(eId:Int): LiveData<Boolean>{
        return expenseDao.getExpenseIsKeeped(eId)
    }
    /////////////////////////////////Esxpense Category////////////////////////////////////////////
    fun getExpenseCateroryModel(): LiveData<List<CategoryModel>>{
        return expenseCategoryDao.getAllExpenseCategoryModel()
    }

    suspend fun getAllExpenseCategoryName(): List<String>{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getAllExpenseCategoryName()
        }
    }
   suspend fun getCategoryIdByName(name:String):Int?{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getECIdByName(name)
        }
    }
    suspend fun insertExpensesCategory(expenseCategory: ExpenseCategory){
        withContext(Dispatchers.IO){
            expenseCategoryDao.insert(expenseCategory)
        }
    }
   suspend fun updateExpensesCategory(expenseCategory: ExpenseCategory){
        withContext(Dispatchers.IO){
            expenseCategoryDao.update(expenseCategory)
        }
    }
    suspend fun deleteExpensesCategoryToDao(id:Int){
        withContext(Dispatchers.IO){
            expenseCategoryDao.delete(id)
        }
    }

    suspend fun updateExpenseName(id:Int,name:String){
        withContext(Dispatchers.IO){
            expenseDao.updateExpenseName(id,name)
        }
    }


    suspend fun assignCloudIdToExpenseCategoryTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            expenseCategoryDao.assignExpenseCategoryCloudID(cloudId, id)
        }
    }

    suspend fun getAllExpenseCategoryTable(): List<ExpenseCategory> {
        return withContext(Dispatchers.IO) {
            expenseCategoryDao.selectAllExpenseCategoryTable()
        }
    }

    suspend fun assignCloudIdToExpenseTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            expenseDao.assignExpenseCloudID(cloudId, id)
        }
    }

    suspend fun getAllExpenseTable(): List<Expenses> {
        return withContext(Dispatchers.IO) {
            expenseDao.selectAllExpenseTable()
        }
    }

    suspend fun assignCloudIdToSuplierTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            suplierDao.assignSuplierCloudID(cloudId, id)
        }
    }

    suspend fun getAllSuplierTable(): List<SuplierTable> {
        return withContext(Dispatchers.IO) {
            suplierDao.selectAllSuplierTable()
        }
    }
    fun getAllSuplier() : LiveData<List<SuplierTable>>{
        return suplierDao.getAllSuplier()
    }
    suspend fun insertSupliers(){
        withContext(Dispatchers.IO){
            val suplierDummy= listOf<SuplierTable>(
                SuplierTable(1,"Mitra Jaya","Jakarta"),
                SuplierTable(2,"Polystar","Jakarta"),
                SuplierTable(3,"Busa Yerry","Surabaya"),
                SuplierTable(4,"Vision","Jakarta"),
                SuplierTable(5,"PT. SIMNU","Surabaya"),
                SuplierTable(6,"New Lancar","Makassar"),
                SuplierTable(7,"Sentral Logam","Makassar"),
                SuplierTable(8,"Toko Utama","Makassar"),
                SuplierTable(9,"Bali Jaya","Makassar"),
                SuplierTable(10,"Cahaya Indah","Makassar"),
                SuplierTable(11,"Trijaya Tunggal","Makassar"),
                SuplierTable(12,"Sun Auto","Surabaya"),
                SuplierTable(13,"Nasional Lestari","Surabaya"),
            )

            // Update all entries with the new date format
            suplierDao.insert(suplierDummy)
        }
    }
    /////////////////////////////////InventoryPurchase////////////////////////////////////
    suspend fun getInventoryPurchaseList(id:Int): List<InventoryPurchase>{
        return withContext(Dispatchers.IO){inventoryPurchaseDao.selectPurchaseList(id)}
    }


    suspend fun getAllInventoryPurchaseTable(): List<InventoryPurchase> {
        return withContext(Dispatchers.IO) {
            inventoryPurchaseDao.selectAllInventoryPurchaseTable()
        }
    }
    suspend fun getPurchaseById():InventoryPurchase?{
        return withContext(Dispatchers.IO){
            inventoryPurchaseDao.getPurchaseById(0)
        }
    }
    suspend fun insertPurchases(expenses: Expenses, list:List<InventoryPurchase>){
        withContext(Dispatchers.IO){
            inventoryPurchaseDao.insertPurchaseAndExpense(expenses,list)
        }
    }

    suspend fun insertPurchase(inventoryPurchase: InventoryPurchase,subName:String?): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO){
                inventoryPurchase.sPCloudId=subProductDao.getSubIdBySubName(subName!!)
                inventoryPurchaseDao.upsertPurchaseAndUpdateExpense(inventoryPurchase)
            }
    }
    suspend fun deletePurchase(inventoryPurchase: InventoryPurchase): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO){
                inventoryPurchaseDao.deletePurchaseAndUpdateExpense(inventoryPurchase)
            }
        }

    ///////////////////////////////PurchaseDiscount///////////////////////////////////////
    suspend fun insertPurchaseDiscount(pDiscount: PurchaseDiscount): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO){
                inventoryPurchaseDao.insertPDiscountAndUpdateExpense(pDiscount)
            }
    }

    suspend fun deletePurchaseDiscount(pDiscount: PaymentModel): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO){

                inventoryPurchaseDao.deletePDiscountAndUpdateExpense(pDiscount)
            }
        }
    fun getPDiscountByExpense(expenseId:Int): LiveData<List<PaymentModel>> {
        return pDiscountDao.getPDiscountByExpenseId(expenseId)
    }
}