package com.example.app21try6.database.models

data class ExportStockPriceModel(
    var id:Long?,
    var productName:String?,
    var productPrice:Int?,
    var productAlternatePrice: Double?,
    var productNet: Double?,
    var isChecked: Boolean=false,
    var isRollChecked: Boolean=false,
    var isEnceranChecked: Boolean=false,
)
