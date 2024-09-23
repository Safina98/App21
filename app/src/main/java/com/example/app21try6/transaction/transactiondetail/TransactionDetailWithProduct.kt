package com.example.app21try6.transaction.transactiondetail

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.app21try6.database.TransactionDetail

data class TransactionDetailWithProduct(
    @Embedded val transactionDetail: TransactionDetail,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "product_name") val productName: String
)