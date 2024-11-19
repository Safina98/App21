package com.example.app21try6.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.Date


data class SuplierTable(
    var id:Int=0,
    var suplierName:String="",
    var suplierLocation:String=""
)
data class suplierAndProduct(
    var id:Int,
    var suplierId:Int,
    var productId:Int
)

data class InventoryPurchase(
    var id: Int=0,
    var subProductId:Int=0,//Foreign key
    var suplierId:Int=0,//Foreign key
    var expensesId:Int=0,//Foreign key
    var supName:String="",//done
    var name:String="",//done
    var purchaseDate:Date= Date(),//done
    var batchCount:Int=0,//done
    var netQty:Double=0.0,//done
    var price:Int=0,//done
    var totalPrice:Double=0.0,//done
    var status:String="",
    var ref:String=""
)
//tambah di sub product saja
data class DetailWarnaTable(
    var detailId:Int=0,
    var subId:Int=0,
    var batchCount:Int=0,
    var netCount:Double=0.0,
    var ket:String="",
    var ref: String=""
)
data class InventoryLog(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // merk_table refMerk
    @ColumnInfo(name="merkId")
    var merkId: Int =0,
    // warna_table warnaRef
    @ColumnInfo(name="productId")
    var productId: Int = 0,
    @ColumnInfo(name="subProductId")
    var subProductId: Int = 0,
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


val purchaseDummy= mutableListOf<InventoryPurchase>(
    InventoryPurchase(1,3,2,-1,"Polystar","9001", Date(),5,35.0,118000,4130000.0),
            InventoryPurchase(1,5,2,-1,"Polystar","9002",Date(),5,35.0,118000,4130000.0)
)

