package com.example.app21try6.stock.subproductstock

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.SubProductDao
import com.example.app21try6.database.TransDetailDao

class SubViewModelFactory(private val dataSource2: SubProductDao,
                          private val application: Application,
                          private val product_id:Array<Int>,
                          private val datasource3:TransDetailDao,
                          private val sum_id:Int
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubViewModel::class.java)) {
            return SubViewModel(dataSource2,application,product_id,datasource3,sum_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}