package com.example.app21try6.transaction.transactionselect

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.*
import com.example.app21try6.transaction.transactionedit.TransactionEditViewModel

class TransactionSelectViewModelFactory(
    val sum_id:Int,
    val database1: CategoryDao,
    val database2: ProductDao,
    val database3: BrandDao,
    val database4: SubProductDao,
    val date:Array<String>,
    val database5: TransSumDao,
    val database6:TransDetailDao,
    val application: Application
): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionSelectViewModel::class.java)) {
            return TransactionSelectViewModel(sum_id,database1,database2,database3,database4,date,database5,database6,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}