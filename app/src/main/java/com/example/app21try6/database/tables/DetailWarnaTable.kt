package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "detail_warna_table",
    foreignKeys = [
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sub_id"],
            childColumns = ["subId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ref"], unique = true)] // Add this index
)
data class DetailWarnaTable(
    @PrimaryKey(autoGenerate = true)
    var detailId:Int=0,
    @ColumnInfo(name="subId")
    var subId:Int=0,
    @ColumnInfo(name="batchCount")
    var batchCount:Double=0.0,
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="ket")
    var ket:String="",
    @ColumnInfo(name="ref")
    var ref: String=""
)