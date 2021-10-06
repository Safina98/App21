package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao


class TransactionDetailViewModelFactory  (private val application: Application,
                                          private val datasource1:TransSumDao,
                                          private val datasource2:TransDetailDao
        ,private val id:Int): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            return TransactionDetailViewModel(application,datasource1,datasource2,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}