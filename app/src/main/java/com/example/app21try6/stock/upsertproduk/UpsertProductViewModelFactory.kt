package com.example.app21try6.stock.upsertproduk

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.stock.brandstock.BrandStockViewModel

class UpsertProductViewModelFactory (
    private val productId:Long?,
    private val repository: StockRepositories,
    private val discountRepository: DiscountRepository,
    private val transRepositories: TransactionsRepository,
    private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpsertProductViewModel::class.java)) {
            return UpsertProductViewModel(productId,repository,discountRepository,transRepositories,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}