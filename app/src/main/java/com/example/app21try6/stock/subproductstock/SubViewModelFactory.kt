package com.example.app21try6.stock.subproductstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

class SubViewModelFactory(private val stockRepo: StockRepositories,
                          private val transactionsRepo: TransactionsRepository,
                          private val product_id:Int,
                          private val brandId:Long,
                          private val ctgId:Long,
                          private val tSId:Long,
                          private val sPId:Int,
                          private val sum_id:Long,
                          private val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubViewModel::class.java)) {
            return SubViewModel(stockRepo,transactionsRepo,product_id,brandId,ctgId,tSId,sPId,sum_id,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}