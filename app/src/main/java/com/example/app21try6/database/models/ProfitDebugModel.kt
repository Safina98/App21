package com.example.app21try6.database.models

import java.util.Date

//TODO DELETE LATER
data class ProfitDebugModel(
    var transDetailId:Long?=0L,
    var transSumId:Long?=0L,
    var itemName:String?="",
    var date: Date?= Date(),
    var qty: Double?=0.0,
    var unitQty: Double?=0.0,
    var unit:String?="",
    var customerName: String?="",
    var totalProfit: Double? =0.0,
    var totalTrans: Double?=0.0

    )