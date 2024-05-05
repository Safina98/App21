package com.example.app21try6.transaction.transactionactive

import androidx.room.ColumnInfo
import java.util.Date

data class TransExportModel (
    var trans_detail_id: Long? = null,
    var trans_item_name:String? = null,
    var qty:Double? = null,
    var trans_price:Int? = null,
    var total_price:Double? = null,
    var is_prepared:Boolean? = null,
    var sum_id:Int? = null,
    var cust_name:String? = null,
    var total_trans:Double? = null,
    var paid:Int? = null,
    var trans_date: Date? = null,
    var is_taken:Boolean? = null,
    var is_paid_off:Boolean? = null,
    var ref:String? = null
)

/*
data class TransExportModel (
    var trans_detail_id: Long = 0,
    var trans_item_name:String = "",
    var qty:Double = 0.0,
    var trans_price:Int= 0,
    var total_price:Double = 0.0,
    var is_prepared:Boolean = false,
    var sum_id:Int = 0,
    var cust_name:String = "",
    var total_trans:Double=0.0,
    var paid:Int=0,
    var trans_date:String="",
    var is_taken:Boolean=false,
    var is_paid_off:Boolean=false,
    var ref:String=""

)

 */