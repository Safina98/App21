package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.Date
@Entity(
    tableName = "inventory_purchase_table",
    foreignKeys = [
        ForeignKey(
            entity = Expenses::class,
            parentColumns = ["id"],
            childColumns = ["expensesId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sPCloudId"],
            childColumns = ["sPCloudId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
    ],
    indices = [
        Index(value = ["expensesId"]),
        Index(value = ["sPCloudId"])
    ]

)
@TypeConverters(DateTypeConverter::class)
data class InventoryPurchase(
    @PrimaryKey
    var id: Long=0L,
    @ColumnInfo(name="sPCloudId")
    var sPCloudId: Long? =null,//Foreign key,
    @ColumnInfo(name="expensesId")
    var expensesId:Int=0,//Foreign key,
    @ColumnInfo(name="subProductName")
    var subProductName:String="",

    @ColumnInfo(name="batchCount")
    var batchCount:Double=0.0,
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="price")
    var price:Int=0,
    @ColumnInfo(name="totalPrice")
    var totalPrice:Double=0.0,
    @ColumnInfo(name="status")
    var status:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1
)