package com.example.app21try6.transaction.transactionedit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.DiscountTransDao
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao

class TransactionEditViewModelFactory(
    private val application: Application,
    private val datasource1: TransSumDao,
    private val datasource2: TransDetailDao,
    private val discountDao: DiscountDao,
    private val discountTransDao: DiscountTransDao,
    private val customerDao: CustomerDao,
    private val id:Int
):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionEditViewModel::class.java)) {
            return TransactionEditViewModel(application,datasource1,datasource2,discountDao,discountTransDao,customerDao,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}