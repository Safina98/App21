package com.example.app21try6.database.models

data class PurchaseOrderModel(
    var subId:Long,// sPCloudId from sub_table
    var productId:Long,//productCloudId from product_table
    var subName:String,//sub_name from sub_table
    var totalNet:Double?,// batchCount*net  from detail_warna_table added with net from merchandise_table
    var defaultNet:Double?,//default_net from product_table
    var totalSells30Days:Double?,//the sum of qty*unit_qty from trans_detail_table in the last 30 days
    var totalSells90Days:Double?,//the sum of qty*unit_qty from trans_detail_table in the last 90 days
    var averagesells:Double?,// if (totalSelss30Days:30 > totalSells90Days:90) totalSelss30Days:30 else totalSells90Days:90
    var purcahseOrder:Int?//averageSells*45/defaultnet round up
)
