package com.example.app21try6.stock.subdetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.SubProductDao

class DetailViewModelFactory (private val dataSource2: SubProductDao,
                              private val application: Application,
                              private val product_id:Array<Int>
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(dataSource2, application, product_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}