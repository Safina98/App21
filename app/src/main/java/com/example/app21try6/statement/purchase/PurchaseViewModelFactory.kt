package com.example.app21try6.statement.purchase

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.statement.StatementHSViewModel

class PurchaseViewModelFactory(private val application: Application,
                               private val id:Int,
                               private val expenseDao: ExpenseDao,
                               private val expenseCategoryDao: ExpenseCategoryDao,
                               val subProductDao: SubProductDao,
                               val productDao: ProductDao


): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
            return PurchaseViewModel(application,id,expenseDao,expenseCategoryDao,subProductDao,productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}