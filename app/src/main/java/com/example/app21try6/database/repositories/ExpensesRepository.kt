package com.example.app21try6.database.repositories

import android.app.Application
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.StringDateModel
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.SuplierTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class ExpensesRepository(application: Application) {
    private val expenseDao = VendibleDatabase.getInstance(application).expenseDao
    private val expenseCategoryDao = VendibleDatabase.getInstance(application).expenseCategoryDao
    private val suplierDao= VendibleDatabase.getInstance(application).suplierDao
    private val inventoryPurchaseDao= VendibleDatabase.getInstance(application).inventoryPurchaseDao

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

    suspend fun assignCloudIdToInventoryPurchaseTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            inventoryPurchaseDao.assignInventoryPurchaseCloudID(cloudId, id)
        }
    }

    suspend fun getAllInventoryPurchaseTable(): List<InventoryPurchase> {
        return withContext(Dispatchers.IO) {
            inventoryPurchaseDao.selectAllInventoryPurchaseTable()
        }
    }
}