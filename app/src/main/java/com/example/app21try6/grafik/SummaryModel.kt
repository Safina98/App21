package com.example.app21try6.grafik

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class SummaryModel(
    var year: Int?,
    var month: String?,
    var month_number: Int?,
    var day: Int?,
    var item_name:String,
    var item_sold:Double,
    var price:Double,
    var total_income : Double,
    var product_id:Int?,
    var productCapital:Int?,
    var productNet:Int
)