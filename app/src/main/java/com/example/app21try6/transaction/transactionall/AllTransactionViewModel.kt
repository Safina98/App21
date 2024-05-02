package com.example.app21try6.transaction.transactionall

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel

class AllTransactionViewModel(application: Application,dataSource1:TransSumDao):AndroidViewModel(application) {
    val allTransactionSummary = dataSource1.getAllTransSum()

    companion object {
        @JvmStatic
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val dataSource1 = VendibleDatabase.getInstance(application).transSumDao

                return AllTransactionViewModel(
                    application,dataSource1
                ) as T
            }
        }
    }

}