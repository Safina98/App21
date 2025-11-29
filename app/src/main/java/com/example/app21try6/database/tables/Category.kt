package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
        @PrimaryKey
        @ColumnInfo(name = "categoryCloudId")
        var categoryCloudId:Long = 0L,
        @ColumnInfo(name="category_name")
        var category_name:String = "emtpy",
        @ColumnInfo(name="needs_syncs")
        var needsSyncs:Int=1,
        @Ignore
        var checkBoxBoolean:Boolean = false
)