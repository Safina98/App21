package com.example.app21try6.stock.exportprice

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.stock.brandstock.BrandStockViewModel

class ExportStockPriceViewModelFactory (private val repository: StockRepositories,
private val discountRepository: DiscountRepository,
private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExportStockPriceViewModel::class.java)) {
            return ExportStockPriceViewModel(repository,discountRepository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}