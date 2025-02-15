package com.example.app21try6.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.app21try6.database.tables.SubProduct


data class SubWithPriceModel (
    @Embedded val subProduct: SubProduct,
    @ColumnInfo(name = "purchasePrice") val purchasePrice:Int?,
    @ColumnInfo(name = "default_net") val default_net:Double?,
    @ColumnInfo(name = "purchaseUnit") val puchaseUnit:Double?
)