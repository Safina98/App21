package com.example.app21try6.database

data class DiscountTable (
    val discId:Int,//autoIncrement
    val custId:Int?=null,//customer id
    val discValue:Int=0,
    val discType:String=""
)
/***************************************Cashback & Discount******************************************/
// 1. mbtech camaro, fiesta & carrera discount 2000 untuk cust local dan pembelian diatas 5 m
    //*disctype:cashback mbtch
    /*program flow:
        *di detail fragment on start buat fungsi untuk hitung discount
            *fungsi:
            * cari product dari semua item list
            * cari disc id di product
            * kalau jenis

            * te
        *
    * */
