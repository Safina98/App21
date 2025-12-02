package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "customer_table")
data class CustomerTable(
    @PrimaryKey(autoGenerate = true)
    var custId:Int=0,//Auoincrement
    @ColumnInfo(name = "customerName")
    var customerName:String="",
    @ColumnInfo(name = "customerBussinessName")
    var customerBussinessName:String="",//unique
    @ColumnInfo(name = "customerLocation")
    var customerLocation:String?=null,
    @ColumnInfo(name = "customerAddress")
    var customerAddress:String="",
    @ColumnInfo(name = "customerLevel")
    var customerLevel:String?=null,
    @ColumnInfo(name = "customerTag1")
    var customerTag1:String?=null,
    @ColumnInfo(name = "customerTag2")
    var customerTag2:String?=null,
    @ColumnInfo(name = "customerPoint")
    var customerPoint:Double=0.0,
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "customerCloudId")
    var customerCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)