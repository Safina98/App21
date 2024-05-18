package com.example.app21try6.transaction.transactiondetail

import androidx.room.ColumnInfo
import java.util.Date

data class PaymentModel (
    var id:Int?,
    var sum_id:Int?,
    var payment_ammount:Int?,
    var payment_date: Date?,
    var ref:String?,
    var paid:Int?,
    var total_trans:Double?
)