package com.example.app21try6.database.models

import java.util.Date

data class WorkerModel(
    var productCapital:Int=0,
    var productId:Long=0L,
    var fromDate: Date= Date()
)