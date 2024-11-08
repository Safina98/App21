package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
        @PrimaryKey(autoGenerate = true)
        var category_id:Int = 0,
        @ColumnInfo(name="category_name")
        var category_name:String = "emtpy",
        @Ignore
        var checkBoxBoolean:Boolean = false
)