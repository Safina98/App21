package com.example.app21try6.transaction.transactionedit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

class TransactionEditViewModelFactory(
    private val stockRepo: StockRepositories,
    private val transRepo: TransactionsRepository,
    private val discountRepo: DiscountRepository,
    private val application: Application,
    private val id:Int
):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionEditViewModel::class.java)) {
            return TransactionEditViewModel(stockRepo,transRepo,discountRepo,application,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}