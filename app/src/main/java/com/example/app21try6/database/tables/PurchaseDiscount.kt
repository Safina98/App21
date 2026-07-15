package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "purchase_discount_table",
    foreignKeys = [
        ForeignKey(
            entity = Expenses::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["expenseId"])
    ]
)
data class PurchaseDiscount (
    @PrimaryKey
    var id:Long=0L, //primary key, System.currentTimeMillis()
    @ColumnInfo
    var discountName:String="",
    @ColumnInfo
    var discountValue:Int=0,
    @ColumnInfo
    var expenseId:Int=0,
)