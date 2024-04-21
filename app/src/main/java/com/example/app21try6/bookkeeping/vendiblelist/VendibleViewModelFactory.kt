package com.example.app21try6.bookkeeping.vendiblelist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.*


class VendibleViewModelFactory (private val dataSource:SummaryDbDao,
                                private val dataSource1:CategoryDao,
                                private val dataSource2: ProductDao,
                                private val datasource3:BrandDao,
                                private val dataSource4: SubProductDao,
                                private val date:Array<String>,
                                private val application: Application):ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VendibleViewModel::class.java)) {
            return VendibleViewModel(dataSource,dataSource1,dataSource2,datasource3,dataSource4,date,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}