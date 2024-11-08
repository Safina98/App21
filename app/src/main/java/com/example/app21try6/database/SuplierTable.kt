package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


data class SuplierTable(
    var id:Int=0,
    var suplierName:String="",
    var suplierLocation:String=""
)

data class InventoryPurchase(
    var id: Int=0,
    var subProductId:Int=0,//Foreign key
    var suplierId:Int=0,//Foreign key
    var purchaseDate:Date= Date(),
    var batchCount:Int=0,
    var netQty:Double,
    var price:Int=0,
    var totalPrice:Int=0,
    var status:String="",
    var ref:String=""
)
data class subProductDetail(
    var detailId:Int=0,
    var subId:Int,
    var batchCount:Int,
    var netCount:Double,
)
data class InventoryLog(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // merk_table refMerk
    @ColumnInfo(name="refMerk")
    var refMerk: String = "",
    // warna_table warnaRef
    @ColumnInfo(name="warnaRef")
    var warnaRef: String = "",
    // detail_warna_table,
    @ColumnInfo(name="detailWarnaRef")
    var detailWarnaRef: String? = "",
    @ColumnInfo(name="isi")
    var isi: Double = 0.0,
    // detail_warna_table
    @ColumnInfo(name="pcs")
    var pcs: Int = 0,
    @ColumnInfo(name="barangLogDate")
    var barangLogDate: Date = Date(),
    // log_table
    @ColumnInfo(name="barangLogRef")
    var barangLogRef: String = "",
    //added column
    var barangLogKet: String = ""
)

