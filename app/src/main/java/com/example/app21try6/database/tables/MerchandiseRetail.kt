package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
/*
@Entity(
    tableName = "merchandise_table",
    foreignKeys = [
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sub_id"],
            childColumns = ["sub_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)

 */
data class MerchandiseRetail(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name="sub_id")
    val sub_id: Int,//foreignkey
    @ColumnInfo(name="subProductNet")
    var subProductNet:Double,
    @ColumnInfo(name="ref")
    var ref:String="",
    @ColumnInfo(name="date")
    var date: Date = Date()
)