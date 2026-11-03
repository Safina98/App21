package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "suplier_table",
)
data class SuplierTable(
    @PrimaryKey
    var id:Long=0L,
    @ColumnInfo(name="suplierName")
    var suplierName:String="",
    @ColumnInfo(name="suplierLocation")
    var suplierLocation:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1
)