package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "brand_table",
        foreignKeys = [ForeignKey(entity = Category::class,
                parentColumns = ["categoryCloudId"],
                childColumns = ["cath_code"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)])
data class Brand(
    @PrimaryKey
    @ColumnInfo(name = "brandCloudId")
    var brandCloudId:Long = 0L,
    @ColumnInfo(name="brand_name")
    var brand_name:String = "emtpy",
    @ColumnInfo(name="cath_code")
    var cath_code:Long = 0L,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1,
    //@ColumnInfo(name="is_deleted")
    //var isDeleted: Boolean = false, //newly added cloumn
)

