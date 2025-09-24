package com.example.app21try6.database.models

import java.util.Date

data class TracketailWarnaModel (
    var trans_detail_id:Long,
    var sum_id:Int,
    var trans_item_name:String,
    var qty:Double,
    var tans_detail_date: Date?,
    val sub_id:Int,
    var unit_qty:Double,
    var cust_name:String,
    var sum_note:String?=null,
    var is_cutted: Boolean?
)