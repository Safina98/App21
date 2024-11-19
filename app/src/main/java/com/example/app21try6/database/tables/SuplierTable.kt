package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "suplier_table",
)
data class SuplierTable(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    @ColumnInfo(name="suplierName")
    var suplierName:String="",
    @ColumnInfo(name="suplierLocation")
    var suplierLocation:String=""
)