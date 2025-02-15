package com.example.app21try6.transaction.transactiondetail

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.app21try6.database.tables.TransactionDetail

data class TransactionDetailWithProduct(
    @Embedded val transactionDetail: TransactionDetail,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "discount_id") val discountId: Int?,
    @ColumnInfo(name = "product_price") val productPrice:Int?
)