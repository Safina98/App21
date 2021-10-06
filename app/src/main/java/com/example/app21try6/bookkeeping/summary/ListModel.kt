package com.example.app21try6.bookkeeping.summary

data class ListModel(var year_n:Int,var month_n:String, var month_nbr:Int,var day_n: Int,var nama:String,var day_name:String, var total:Double){
    constructor():this(0,"",0,0,"","",0.0)

}