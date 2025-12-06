package com.example.app21try6.stock.brandstock

data class ExportModel(
        var category:String,
        var brand: String,
        var product:String,
        var price:Int,
        var capital:Int,
        var bestSelling:Boolean,
        var defaultNet:Double=0.0,
        var alternate_price:Double = 0.0,
        var alternate_capital:Double = 0.0,
        var subProduct:String,
        var warna:String, var roll_u:Int,

        //
){
    constructor():this("","","",0,0,false,0.0,0.0,0.0,"","",0,)


}