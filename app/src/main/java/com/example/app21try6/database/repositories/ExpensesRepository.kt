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

}