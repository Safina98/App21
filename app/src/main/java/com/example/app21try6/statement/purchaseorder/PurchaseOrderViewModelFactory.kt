package com.example.app21try6.statement.purchaseorder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.stock.exportprice.ExportStockPriceViewModel

class PurchaseOrderViewModelFactory (private val repository: StockRepositories,
                                     private val expensesRepository: ExpensesRepository,
                                     private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseOrderViewModel::class.java)) {
            return PurchaseOrderViewModel(repository,expensesRepository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}