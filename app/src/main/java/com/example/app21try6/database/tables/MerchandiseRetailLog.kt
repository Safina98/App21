package com.example.app21try6.database.tables

import java.util.Date

data class MerchandiseRetailLog(
    var id:Long=0L,
    //foreign key sub_table sub_id
    var subId:Long=0L,
    //foreign key detail_warna_table ref
    //var detailWarnaRef:String="", //not recorded
    var transDetailId:Long?=null,
    //foreign key merchadise_table ref
    var retailId:Int=0,
    var detailWarnaNet:Double=0.0,
    var cutCount:Int=0,
    var previousNet:Double=0.0,
    var substractedWith:Double=0.0,
    var transDetailQty:Double=0.0,
    var ket:String="",
    var customer:String="",
    var date: Date=Date(),
    )


/*
* var id long
* var sub_id int
* var detail_warna_id/ref Int/string
* var merch retail id/ref Int/string
* var detail net double
* var potonganke Int
* net sebelum di potong: double
* net dipotong berapa
*
* */

