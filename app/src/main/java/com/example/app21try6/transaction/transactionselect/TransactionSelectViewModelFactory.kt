package com.example.app21try6.transaction.transactionselect

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

class TransactionSelectViewModelFactory(
    val stockRepo:StockRepositories,
    val transRepo:TransactionsRepository,
    val sum_id:Int,
    val date:Array<String>,
    val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionSelectViewModel::class.java)) {
            return TransactionSelectViewModel(stockRepo,transRepo,sum_id,date,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}