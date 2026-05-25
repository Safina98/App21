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
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.TransactionsRepository

class StatementHSViewModelFactory(private val application: Application,

                                  private val discountRepository: DiscountRepository


                                          ): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatementHSViewModel::class.java)) {
            return StatementHSViewModel(application,
                discountRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}