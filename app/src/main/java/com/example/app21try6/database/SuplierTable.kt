package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.Date



data class suplierAndProduct(
    var id:Int,
    var suplierId:Int,
    var productId:Int
)

