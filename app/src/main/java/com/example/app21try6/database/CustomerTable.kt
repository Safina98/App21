package com.example.app21try6.database

data class CustomerTable(
    val custId:Int,//Auoincrement
    val custName:String="",//unique
    val custLocation:String="",
    val custLevel:String="",
    val custPoint:Double=0.0
)