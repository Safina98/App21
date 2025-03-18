package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.PaymentDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository


class TransactionDetailViewModelFactory  (private val stockRepos: StockRepositories,
                                          private val bookRepo: BookkeepingRepository,
                                          private val transRepo: TransactionsRepository,
                                          private val discountRepo: DiscountRepository,

    private val application: Application,
                                          private val id:Int): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionDetailViewModel::class.java)) {
            return TransactionDetailViewModel(stockRepos,bookRepo,transRepo,discountRepo,application,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}