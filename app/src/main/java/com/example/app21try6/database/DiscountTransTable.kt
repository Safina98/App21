package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "discout_transaction_table",
    foreignKeys = [
        ForeignKey(entity = DiscountTable::class,
            parentColumns = ["discountId"],
            childColumns = ["discountId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = TransactionSummary::class,
            parentColumns = ["sum_id"],
            childColumns = ["sum_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)])

data class DiscountTransaction(
    @PrimaryKey(autoGenerate = true)
    var discTransId:Int = 0,//autoIncrement
    @ColumnInfo(name = "discTransRef" )
    var discTransRef:String,
    @ColumnInfo(name = "discountId" )
    var discountId:Int?,//foreign key
    @ColumnInfo(name = "sum_id")
    var sum_id:Int,//foreign key
    @ColumnInfo(name = "discTransDate" )
    var discTransDate: Date= Date(),
    @ColumnInfo(name = "discTransName" )
    var discTransName:String="",
    @ColumnInfo(name = "discountAppliedValue")
    var discountAppliedValue: Double = 0.0,

)