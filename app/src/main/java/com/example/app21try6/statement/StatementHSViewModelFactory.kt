package com.example.app21try6.statement

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao

class StatementHSViewModelFactory(private val application: Application,
                                  private val discountDao: DiscountDao,
                                  private val customerDao: CustomerDao,
                                  private val expenseDao: ExpenseDao,
                                  private val expenseCategoryDao: ExpenseCategoryDao,
                                  val transDetailDao: TransDetailDao,
                                  val transSumDao: TransSumDao


                                          ): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatementHSViewModel::class.java)) {
            return StatementHSViewModel(application,discountDao,customerDao,expenseDao,expenseCategoryDao,transDetailDao,transSumDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}