package com.example.app21try6.stock.brandstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories

class BrandStockViewModelFactory(
    private val repository: StockRepositories,
    private val discountRepository: DiscountRepository,
    private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandStockViewModel::class.java)) {
            return BrandStockViewModel(repository,discountRepository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}