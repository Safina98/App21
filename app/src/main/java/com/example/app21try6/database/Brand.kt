package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "brand_table",
        foreignKeys = [ForeignKey(entity = Category::class,
                parentColumns = ["category_id"],
                childColumns = ["cath_code"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)])
data class Brand(
    @PrimaryKey(autoGenerate = true)
    var brand_id:Int = 0,
    @ColumnInfo(name="brand_name")
    var brand_name:String = "emtpy",
    @ColumnInfo(name="cath_code")
    var cath_code:Int = 0
)