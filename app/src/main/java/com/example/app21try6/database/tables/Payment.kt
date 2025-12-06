package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.Date

//was paymen_table
@Entity(tableName = "payment_table",
    foreignKeys = [ForeignKey(entity = TransactionSummary::class,
        parentColumns = ["tSCloudId"],
        childColumns = ["tSCloudId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
@TypeConverters(DateTypeConverter::class)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name="tSCloudId")
    var tSCloudId: Long = 0,
    @ColumnInfo(name="payment_ammount")
    var payment_ammount:Int = 0,
    @ColumnInfo(name="payment_date")
    var payment_date: Date?=null,
    @ColumnInfo(name = "ref")
    var payment_ref:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "paymentCloudId")
    var paymentCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)