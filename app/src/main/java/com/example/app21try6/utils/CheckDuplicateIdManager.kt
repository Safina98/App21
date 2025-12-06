package com.example.app21try6.utils

import android.util.Log
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

class CheckDuplicateIdManager (
    private val bookKeepingRepository: BookkeepingRepository,
    private val discountRepository: DiscountRepository,
    private val expensesRepository: ExpensesRepository,
    private val logsRepository: LogsRepository,
    private val stockRepository: StockRepositories,
    private val transactionRepository: TransactionsRepository,


    // add more repositories if needed
){

}