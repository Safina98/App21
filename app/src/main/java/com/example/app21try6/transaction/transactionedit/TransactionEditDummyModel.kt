package com.example.app21try6.transaction.transactionedit

data class TransactionEditDummyModel(var id:Int,
                                     var item_name:String,
                                     var qty:Double,
                                     var item_price:Int,
                                     var total_price:Double,
                                     var date:String,
                                     var cost_name:String){
    constructor():this(0,"",0.0,0,0.0,"","")
}