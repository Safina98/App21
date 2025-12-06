package com.example.app21try6.grafik

data class StockModel(
    var year:Int,
    var month:String,
    var productCloudId: Long?,
    var sub_id:Int?,
    var sub_name:String?,
    var item_name:String,
    var price:Double?,
    var total_income : Double?,
    var itemCount:Double,
    var unit_qty:Double?,
    var category_name:String,
    var product_name:String?=null,
    var product_capital:Int?,
    var productNet:Double?,
)

