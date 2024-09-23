package com.example.app21try6.database

import androidx.room.Entity
import java.util.Date

@Entity
data class DiscountTable (
    var discId:Int=0,//autoIncrement
    //val custId:Int?=null,//customer id
    var discName:String="",//unique
    var discValue:Double=0.0,
    var discType:String="",
    var minimumQty:Double?=null,
    var minimumTranscation:Double?=null,
    var vendibleLevel:String?=null,
    var custLevel:String?=null,
    var custTag1:String?=null,
    var custTag2:String?=null,
    var discDuration:Date?=null,
    var custLocation:String?=null
)

/***************************************Cashback & Discount******************************************/
// 1. mbtech camaro, fiesta & carrera discount 2000 untuk cust local dan pembelian diatas 5 m
    //*disctype:cashback mbtch
    /*program flow:
        *di detail fragment on start buat fungsi untuk hitung discount
            *fungsi:
            * cari product dari semua item list
            * cari disc id di product
            * kalau jenis cashback mb
            * check kalau harga mb 124.000
            * buat fungsi untuk hitung cashback
            * insert cashback di discTransaction
        *
     2. table to alter
     * product table tambah discount id nullabe
     * cust table tambah disc id nullable
     * sub product table tambah discount id nullable?
     * tambah sub product id di transdetail tablle nullable
     * tambah customer id di trans sum

    * pas insert discount, update semua merk, product, sub product
    * di transaction detail
      * get customer table
      * check lokasinya apa dapat discount
      * kalau ya hitung cahsback group by disc

    * buat table yang isinya product dan discount


    * */
