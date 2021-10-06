package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Month
import java.time.Year
import java.util.*

@Entity(tableName = "summary_table")
data class Summary(
        @PrimaryKey(autoGenerate = true)
        var id_m :Int=0,
        @ColumnInfo(name = "year")
        var year: Int= 2030,
        @ColumnInfo(name = "month")
        var month: String = "empty",
        @ColumnInfo(name = "month_number")
        var month_number: Int = 0,
        @ColumnInfo(name = "day")
        var day: Int = 0,
        @ColumnInfo(name = "day_name")
        var day_name: String = "empty",
        @ColumnInfo(name = "item_name")
        var item_name:String = "empty",
        @ColumnInfo(name = "item_sold")
        var item_sold:Double = 0.0,
        @ColumnInfo(name = "price")
        var price:Double = 0.0,
        @ColumnInfo(name = "total_income")
        var total_income : Double = 0.0
)