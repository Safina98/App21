package com.example.app21try6.stock.productstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.BrandDao
import com.example.app21try6.database.ProductDao
//import com.example.app21try6.database.ProductDao
import com.example.app21try6.stock.brandstock.BrandStockViewModel

class ProductViewModelFactory (
        private val dataSource2: ProductDao,
        private val application: Application,
        private val brand_id:Array<Int>
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(dataSource2,application,brand_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}