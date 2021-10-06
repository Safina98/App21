package com.example.app21try6.bookkeeping.editdetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.ProductDao
import com.example.app21try6.database.SummaryDbDao


class BookkeepingViewModelFactory(
        private val dataSource: SummaryDbDao,
        private val dataSource2: ProductDao,
        private val application: Application,
        private val date:Array<String>) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookkeepingViewModel::class.java)) {
            return BookkeepingViewModel(dataSource,dataSource2,application,date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}