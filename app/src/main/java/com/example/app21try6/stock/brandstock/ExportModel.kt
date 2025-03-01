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
        var roll_b_t:Int,var roll_s_t:Int,var roll_k_t:Int,
        var roll_b_g:Int,var roll_s_g:Int,var roll_k_g:Int
        //
){
    constructor():this("","","",0,0,false,0.0,0.0,0.0,"","",0,0,0,0,0,0,0)


}