package com.example.app21try6.database.models

import java.util.Date

data class PaymentModel (
    var id:Int?=null,
    var tSCloudId: Long?=null,
    var payment_ammount:Int?=null,
    var payment_date: Date?=null,
    var ref:String?=null,
    var paid:Int?=null,
    var name:String?="Bayar: ",
    var total_trans:Double?=null,
    var discountType:String?=null,
)