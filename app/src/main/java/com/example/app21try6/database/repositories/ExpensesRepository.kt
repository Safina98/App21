package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.example.app21try6.database.DateTypeConverter
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.models.PurchaseOrderModel

import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.PurchaseDiscount
import com.example.app21try6.database.tables.SuplierTable
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.math.ceil

data class SupplierCleanupResult(
    val updatedCount: Int,
    val unmatchedNames: List<String>
)
class ExpensesRepository(application: Application) {
    private val expenseDao = VendibleDatabase.getInstance(application).expenseDao
    private val db = VendibleDatabase.getInstance(application)
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

    suspend fun getcExpenseByTipe(tipe:String, startDate: Date?, endDate:Date?): List<BarChartModel>{
        return withContext(Dispatchers.IO){
            expenseDao.getHPPExpense(tipe,startDate,endDate)
        }
    }
    suspend fun getTotalcExpenseByTipe(tipe:String, startDate: Date?, endDate:Date?): Double?{
        return withContext(Dispatchers.IO){
            expenseDao.getTotalHPP(tipe,startDate,endDate)
        }
    }
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



    suspend fun getAllSuplierTable(): List<SuplierTable> {
        return withContext(Dispatchers.IO) {
            suplierDao.selectAllSuplierTable()
        }
    }
    suspend fun getPurchaseOrders(suplierName:String): List<PurchaseOrderModel> =
        withContext(Dispatchers.IO) {
            val now = Calendar.getInstance()
            val since30 = DateTypeConverter.fromDate(
                (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -30) }.time
            )!!
            val since90 = DateTypeConverter.fromDate(
                (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -90) }.time
            )!!

            val suplierId=suplierDao.getSuplierIdByName(suplierName)

            suplierDao.getPurchaseOrderRaw(suplierId, since30, since90).map { raw ->
                val daily30 = raw.totalSells30Days / 30.0
                val daily90 = raw.totalSells90Days / 90.0
                val averageSells = if (daily30 > daily90) daily30 else daily90

                val net = raw.defaultNet
                val order = if (net != null && net > 0.0) {
                   val toOrder= ((averageSells * 45)-raw.totalNet) / net
                    if (toOrder>0)
                    ceil(toOrder).toInt()
                    else 0
                } else null

                PurchaseOrderModel(
                    subId = raw.subId,
                    productId = raw.productId,
                    subName = raw.subName,
                    totalNet = raw.totalNet,
                    defaultNet = raw.defaultNet,
                    totalSells30Days = raw.totalSells30Days,
                    totalSells90Days = raw.totalSells90Days,
                    averagesells = averageSells,
                    purcahseOrder = order
                )
            }
        }
    suspend fun deleteAllSuplierTable(){
        withContext(Dispatchers.IO) {
            suplierDao.delete()
        }
    }

    fun getAllSuplier() : LiveData<List<String>>{
        return suplierDao.getAllSuplier()
    }
    suspend fun insertSupliers(){
        withContext(Dispatchers.IO){
            val suplierDummy= listOf<SuplierTable>(
                SuplierTable(1L,"Mitra","Jakarta"),
                SuplierTable(2L,"MBTECH","Jakarta"),
                SuplierTable(3L,"Busa Yerry","Surabaya"),
                SuplierTable(4L,"Vision","Jakarta"),
                SuplierTable(5L,"SIMNU","Surabaya"),
                SuplierTable(6L,"Lancar","Makassar"),
                SuplierTable(7L,"Sentral Logam","Makassar"),
                SuplierTable(8L,"Toko Utama","Makassar"),
                SuplierTable(9L,"Bali Jaya","Makassar"),
                SuplierTable(10L,"Cahaya Indah","Makassar"),
                SuplierTable(11L,"Trijaya","Makassar"),
                SuplierTable(12L,"Sun Auto","Surabaya"),
                SuplierTable(13L,"Nasional Lestari","Surabaya"),
            )

            // Update all entries with the new date format
            suplierDao.insert(suplierDummy)
        }
    }
    suspend fun getSuplierNameById(id:Long?):String?{
        return withContext(Dispatchers.IO){
            suplierDao.getSuplierNameById(id)
        }
    }
    ///////////////////////to delete later///////////////////////////////////////

    suspend fun updateProductSuppliersFromPurchases(): SupplierCleanupResult {
        insertSupliers()
        val supplierIdByName = suplierDao.getAllSuppliers()
            .associate { normalize(it.suplierName) to it.id }

        val latestPurchaseByProduct = suplierDao.getPurchaseExpenseRows()
            .groupBy { it.productCloudId }
            .mapNotNull { (productId, rows) ->
                rows.maxByOrNull { it.expenseDate.orEmpty() }?.let { productId to it }
            }

        var updatedCount = 0
        val unmatchedNames = mutableSetOf<String>()

        db.withTransaction {
            for ((productId, row) in latestPurchaseByProduct) {
                val cleanedName = cleanExpenseName(row.expenseName)
                val supplierId = supplierIdByName[cleanedName]
                if (supplierId == null) {
                    unmatchedNames += cleanedName
                    continue
                }
                suplierDao.updateProductSupplier(productId, supplierId)
                updatedCount++
            }
        }
        return SupplierCleanupResult(updatedCount, unmatchedNames.toList())
    }

    private fun cleanExpenseName(name: String): String =
        normalize(
            name.replace(NOISE_REGEX, " ")
                .trim { !it.isLetterOrDigit() }
        )

    private fun normalize(name: String): String =
        name.replace(SPACE_REGEX, " ").trim().lowercase()

    private companion object {
        val NOISE_REGEX = Regex("\\b(bayar|nota)\\b", RegexOption.IGNORE_CASE)
        val SPACE_REGEX = Regex("\\s+")
    }
    ///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////InventoryPurchase////////////////////////////////////
    suspend fun getInventoryPurchaseListOld(id:Int): List<InventoryPurchase>{
        return withContext(Dispatchers.IO){inventoryPurchaseDao.selectPurchaseListOld(id)}
    }

    fun getInventoryPurchaseList(id:Int): LiveData<List<InventoryPurchase>>{
        return inventoryPurchaseDao.selectPurchaseList(id)
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