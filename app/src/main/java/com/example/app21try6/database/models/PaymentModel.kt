package com.example.app21try6.database.models

import androidx.room.ColumnInfo
import java.util.Date

data class PaymentModel (
    var id:Int?,
    var sum_id:Int?,
    var payment_ammount:Int?,
    var payment_date: Date?,
    var ref:String?,
    var paid:Int?=null,
    var name:String?="Bayar: ",
    var total_trans:Double?,
    var discountType:String?=null
)