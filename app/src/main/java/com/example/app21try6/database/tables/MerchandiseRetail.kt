package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "merchandise_table",
    foreignKeys = [
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sPCloudId"],
            childColumns = ["sPCloudId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MerchandiseRetail(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    @ColumnInfo(name="sPCloudId")
    var sPCloudId: Long =0,//foreignkey
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="ref")
    var ref:String="",
    @ColumnInfo(name="date")
    var date: Date = Date(),
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false,
    @ColumnInfo(name = "mRCloudId")
    var mRCloudId: Long = 0L,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1,
    //@ColumnInfo(name = "initial_net")
    @Ignore
    var initialNet:Double=0.0,//new column
    @Ignore
    var cutCount:Int=0
)