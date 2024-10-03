package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.DiscountTransDao
import com.example.app21try6.database.PaymentDao
import com.example.app21try6.database.ProductDao
import com.example.app21try6.database.SubProductDao
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao


class TransactionDetailViewModelFactory  (private val application: Application,
                                          private val datasource1:TransSumDao,
                                          private val datasource2:TransDetailDao,
                                          private val datasource3:SummaryDbDao,
                                          private val datasource4:PaymentDao,
                                          private val datasource5:SubProductDao,
                                          private val discountDao: DiscountDao,
                                          private val discountTransDao: DiscountTransDao,
                                          private val customerDao: CustomerDao,
                                          private val id:Int): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            return TransactionDetailViewModel(application,datasource1,datasource2,datasource3,datasource4,datasource5,discountDao,discountTransDao,customerDao,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}