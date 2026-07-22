package com.example.app21try6.statement.report

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.TransactionsRepository


class ReportViewModelFactory(private val application: Application,
                             private val expenseRepo: ExpensesRepository,
    private val transRepo: TransactionsRepository



): ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            return ReportViewModel(application,expenseRepo,transRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}