package com.example.app21try6.bookkeeping.vendiblelist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.*
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories


class VendibleViewModelFactory (private val bookRepo: BookkeepingRepository,
                                private val stockRepo: StockRepositories,
                                private val date:Array<String>,
                                private val application: Application):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VendibleViewModel::class.java)) {
            return VendibleViewModel(bookRepo,stockRepo,date,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}