package com.example.app21try6.database

import java.util.Date

data class DiscountTransaction(
    val discTransId:Int,//autoIncrement
    val discTransRef:String,
    val discId:Int,//foreign key
    val sumId:Int,//foreign key
    val discTransDate: Date
)