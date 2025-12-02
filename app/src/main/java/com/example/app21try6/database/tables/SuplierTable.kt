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
    var suplierLocation:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "suplierCloudId")
    var suplierCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)