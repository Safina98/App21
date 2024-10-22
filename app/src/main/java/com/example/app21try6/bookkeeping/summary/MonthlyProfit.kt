package com.example.app21try6.bookkeeping.summary

data class MonthlyProfit(
    val year: Int?,
    val month: String,
    val monthly_profit: Double
)
data class ProductProfit(
    val product_id: Int,
    val product_name: String,
    val profit_by_product: Double
)