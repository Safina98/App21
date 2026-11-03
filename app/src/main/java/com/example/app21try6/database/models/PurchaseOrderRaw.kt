package com.example.app21try6.database.models

data class PurchaseOrderRaw(
    val subId: Long,
    val productId: Long,
    val subName: String,
    val totalNet: Double,
    val defaultNet: Double?,
    val totalSells30Days: Double,
    val totalSells90Days: Double
)
