package com.example.app21try6.stock.brandstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

//todo delete bookkeeping repositories and transaction repository
class BrandStockViewModelFactory(
    private val repository: StockRepositories,
    private val discountRepository: DiscountRepository,
    private val bookKeepingRepository: BookkeepingRepository,
    private val transactionsRepository: TransactionsRepository,
    private val expensesRepository: ExpensesRepository,
    private val logsRepository: LogsRepository,
    private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandStockViewModel::class.java)) {
            return BrandStockViewModel(repository,discountRepository,  bookKeepingRepository,transactionsRepository,expensesRepository,logsRepository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}