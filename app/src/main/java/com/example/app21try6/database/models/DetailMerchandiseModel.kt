package com.example.app21try6.database.models

import java.util.Date

data class DetailMerchandiseModel(
    var id:Int=0,
    var sub_id:Int=0,
    var ref:String="",
    var net:Double=0.0,
    var batchCount:Double?,
    var ket:String?,
    var date: Date?
)