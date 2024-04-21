package com.example.app21try6.transaction.transactionactive

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao

class TransactionActiveViewModelFactory(
    private val application: Application,
    private val datasource1: TransSumDao,
    private val datasource2: TransDetailDao
):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionActiveViewModel::class.java)) {
            return TransactionActiveViewModel(application,datasource1,datasource2) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

