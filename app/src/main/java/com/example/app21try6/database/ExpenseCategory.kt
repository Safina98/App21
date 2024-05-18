package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "expense_category_table")
data class  ExpenseCategory (
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    @ColumnInfo(name="expense_category_name")
    var expense_category_name:String = "",
    @ColumnInfo(name="is_periodic")
    var is_periodic:Boolean = false,
)

