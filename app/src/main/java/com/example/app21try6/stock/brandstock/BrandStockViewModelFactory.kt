package com.example.app21try6.stock.brandstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao

class BrandStockViewModelFactory(
    private val dataSource1: CategoryDao,
    private val dataSource2: BrandDao,
    private val dataSource3: ProductDao,
    private val dataSource4: SubProductDao,
    private val discountDao: DiscountDao,
    private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandStockViewModel::class.java)) {
            return BrandStockViewModel(dataSource1,dataSource2,dataSource3,dataSource4,discountDao,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}