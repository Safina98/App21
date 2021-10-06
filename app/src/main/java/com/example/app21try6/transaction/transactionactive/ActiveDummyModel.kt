package com.example.app21try6.transaction.transactionactive

data class ActiveDummyModel(val id:Int,val cust_name:String,val total_trans:Int,val date:String){
    constructor():this(0,"",0,"")
}