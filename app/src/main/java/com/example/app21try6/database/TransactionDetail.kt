package com.example.app21try6.database

import androidx.room.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "trans_detail_table",
        foreignKeys = [
                ForeignKey(entity = TransactionSummary::class,
                parentColumns = ["sum_id"],
                childColumns = ["sum_id"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)
        ])
data class TransactionDetail(
        @PrimaryKey(autoGenerate = true)
        var trans_detail_id: Long = 0,
        @ColumnInfo(name="sum_id")
        var sum_id:Int = 0,
        @ColumnInfo(name = "trans_item_name")
        var trans_item_name:String = "",
        @ColumnInfo(name = "qty")
        var qty:Double = 0.0,
        @ColumnInfo(name = "trans_price")
        var trans_price:Int= 0,
        @ColumnInfo(name = "total_price")
        var total_price:Double = 0.0
        )

