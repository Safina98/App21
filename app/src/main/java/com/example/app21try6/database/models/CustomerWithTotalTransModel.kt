package com.example.app21try6.database.models

data class CustomerWithTotalTransModel(
    val customerName: String,
    val customerBussinessName: String,
    val month: Int,
    val year: Int,
    val totalAfterDiscount: Double
)