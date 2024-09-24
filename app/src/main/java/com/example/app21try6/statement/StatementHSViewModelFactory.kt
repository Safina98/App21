package com.example.app21try6.statement

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.PaymentDao
import com.example.app21try6.database.SubProductDao
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.transaction.transactiondetail.TransactionDetailViewModel

class StatementHSViewModelFactory(private val application: Application,
                                    private val discountDao: DiscountDao,
                                  private val customerDao: CustomerDao

                                          ): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatementHSViewModel::class.java)) {
            return StatementHSViewModel(application,discountDao,customerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}