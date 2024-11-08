package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.Date


@Entity(tableName = "paymen_table",
    foreignKeys = [ForeignKey(entity = TransactionSummary::class,
        parentColumns = ["sum_id"],
        childColumns = ["sum_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
@TypeConverters(DateTypeConverter::class)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name="sum_id")
    var sum_id:Int = 0,
    @ColumnInfo(name="payment_ammount")
    var payment_ammount:Int = 0,
    @ColumnInfo(name="payment_date")
    var payment_date: Date?=null,
    @ColumnInfo(name = "ref")
    var payment_ref:String=""
)