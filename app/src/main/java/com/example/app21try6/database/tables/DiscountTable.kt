package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

    @Entity(tableName = "discount_table",
            indices = [Index(value = ["discountName"], unique = true)])
    data class DiscountTable (
        @PrimaryKey(autoGenerate = true)
        var discountId:Int=0,//autoIncrement
        @ColumnInfo(name = "discountName" )
        var discountName:String="",//unique
        @ColumnInfo(name = "discountValue")
        var discountValue:Double=0.0,
        @ColumnInfo(name = "discountType")
        var discountType:String="",
        @ColumnInfo(name = "minimumQty")
        var minimumQty:Double?=null,
        @ColumnInfo(name = "minimumTranscation")
        var minimumTranscation:Double?=null,
        @ColumnInfo(name = "vendibleLevel")
        var vendibleLevel:String?=null,
        @ColumnInfo(name = "custLevel")
        var custLevel:String?=null,
        @ColumnInfo(name = "custTag1")
        var custTag1:String?=null,
        @ColumnInfo(name = "custTag2")
        var custTag2:String?=null,
        @ColumnInfo(name = "discountDuration")
        var discountDuration:Date?=null,
        @ColumnInfo(name = "custLocation")
        var custLocation:String?=null,
        @ColumnInfo(name="is_deleted")
        var isDeleted: Boolean = false, //newly added cloumn
        @ColumnInfo(name = "discountCloudId")
        var discountCloudId: Long = 0L,//newly added cloumn
        @ColumnInfo(name="needs_syncs")
        var needsSyncs:Int=1//newly added cloumn
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
     * product table tambah discount id nullabe check
     * cust table tambah disc id nullable nope
     * sub product table tambah discount id nullable? CHECK
     * tambah sub product id di transdetail tablle nullable check
     * tambah customer id di trans sum

    * pas insert discount, update semua merk, product, sub product
    * di transaction detail
      * get customer table
      * check lokasinya apa dapat discount
      * kalau ya hitung cahsback group by disc

    * buat table yang isinya product dan discount


    * */
