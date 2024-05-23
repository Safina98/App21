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
    var trans_detail_date:Date? = null,
    var unit:String?=null,
    var unit_qty:Double?=null,
    var item_position:Int?=null,
    var sum_id:Int? = null,
    var cust_name:String? = null,
    var total_trans:Double? = null,
    var paid:Int? = null,
    var trans_date: Date? = null,
    var is_taken:Boolean? = null,
    var is_paid_off:Boolean? = null,
    var is_keeped:Boolean?=null,
    var ref:String? = null
)
