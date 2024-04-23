package com.example.app21try6.transaction.transactionselect

import androidx.room.Ignore

data  class TransSelectModel(
    var sub_product_id:Int =0,
    var item_name:String="",
    var item_price:Int=0,
    var trans_detail_id:Int = 0,
    var qty:Double=0.0,
    var is_selected:Boolean=false
)