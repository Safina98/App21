package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "detail_warna_table",
    foreignKeys = [
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sPCloudId"],
            childColumns = ["sPCloudId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Expenses::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["ref"], unique = true)]   // <-- add this
)
data class DetailWarnaTable(
    @PrimaryKey
    @ColumnInfo(name = "dWCloudId")
    var dWCloudId: Long =0L,
    @ColumnInfo(name="sPCloudId")
    var sPCloudId: Long =0,
    @ColumnInfo(name="batchCount")
    var batchCount:Double=0.0,
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="ket")
    var ket:String="",
    @ColumnInfo(name="ref")
    var ref:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1,
    @ColumnInfo
    var date: Date?=null, //NEW COLUMN
    @ColumnInfo
    var expenseId:Int?=null //NEW COLUMN
)

