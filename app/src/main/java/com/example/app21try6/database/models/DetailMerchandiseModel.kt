package com.example.app21try6.database.models

import java.util.Date

data class DetailMerchandiseModel(
    var id:Int=0,
    var sPCloudId: Long =0L,
    var ref:String="",
    var net:Double=0.0,
    var batchCount:Double?,
    var ket:String?,
    var date: Date?,
    var selectedQty:Int=0
)