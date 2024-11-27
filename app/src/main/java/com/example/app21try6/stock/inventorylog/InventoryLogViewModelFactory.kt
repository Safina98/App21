package com.example.app21try6.stock.inventorylog

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.stock.brandstock.BrandStockViewModel

class InventoryLogViewModelFactory (
    private val subProductDao: SubProductDao,
    private val inventoryLogDao: InventoryLogDao,
    private val application: Application
                                            ): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryLogViewModel::class.java)) {
            return InventoryLogViewModel(subProductDao,inventoryLogDao,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}