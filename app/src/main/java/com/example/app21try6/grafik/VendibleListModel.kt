package com.example.app21try6.grafik

import java.util.Date

data class VendibleListModel (
    var date: Date?,
    var sub_name:String,
    var price:Double?,
    var totalQty:Double,
    var category_name:String,
    var product_name:String?=null,
    var product_capital:Int?,
)


