package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

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
data class MerchandiseRetail(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    @ColumnInfo(name="sub_id")
    var sub_id: Int=0,//foreignkey
    @ColumnInfo(name="subProductNet")
    var subProductNet:Double=0.0,
    @ColumnInfo(name="ref")
    var ref:String="",
    @ColumnInfo(name="date")
    var date: Date = Date()
)