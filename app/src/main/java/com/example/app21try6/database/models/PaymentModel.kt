package com.example.app21try6.database.models

import java.util.Date

data class PaymentModel (
    var id:Int?,
    var tSCloudId: Long?,
    var payment_ammount:Int?,
    var payment_date: Date?,
    var ref:String?,
    var paid:Int?=null,
    var name:String?="Bayar: ",
    var total_trans:Double?,
    var discountType:String?=null,
)