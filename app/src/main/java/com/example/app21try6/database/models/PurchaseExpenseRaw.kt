package com.example.app21try6.database.models

data class PurchaseExpenseRow(
    val productCloudId: Long,
    val expenseName: String,
    val expenseDate: String?   // stored as "yyyy-MM-dd HH:mm", sorts correctly as text
)
