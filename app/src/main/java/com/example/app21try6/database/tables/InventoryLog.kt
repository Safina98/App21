package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.Date

@Entity(
    tableName = "inventory_log_table",
    foreignKeys = [
        ForeignKey(
            entity = Brand::class,
            parentColumns = ["brand_id"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sub_id"],
            childColumns = ["subProductId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = DetailWarnaTable::class,
            parentColumns = ["ref"],
            childColumns = ["detailWarnaRef"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["barangLogRef"], unique = true)]
)
@TypeConverters(DateTypeConverter::class)
data class InventoryLog(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    // merk_table refMerk
    @ColumnInfo(name="brandId")
    var brandId: Int? =null,
    // warna_table warnaRef
    @ColumnInfo(name="productId")
    var productId: Int? = null,
    @ColumnInfo(name="subProductId")
    var subProductId: Int? = null,
    // detail_warna_table,
    @ColumnInfo(name="detailWarnaRef")
    var detailWarnaRef: String? = null,
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
