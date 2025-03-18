package com.example.app21try6.transaction.transactionactive

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.repositories.TransactionsRepository

class TransactionActiveViewModelFactory(
    private val application: Application,
    private val transRepo:TransactionsRepository
):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionActiveViewModel::class.java)) {
            return TransactionActiveViewModel(application,transRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

