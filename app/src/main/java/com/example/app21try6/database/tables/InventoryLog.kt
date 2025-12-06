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
            parentColumns = ["brandCloudId"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productCloudId"],
            childColumns = ["productCloudId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sPCloudId"],
            childColumns = ["sPCloudId"],
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
    var brandId: Long? =null,
    // product table
    @ColumnInfo(name="productCloudId")
    var productCloudId: Long? = null,
    @ColumnInfo(name="sPCloudId")
    var sPCloudId: Long? = null,
    @ColumnInfo(name="detailWarnaRef")
    var detailWarnaRef: String? = null,
    @ColumnInfo(name="isi")
    var isi: Double = 0.0,
    @ColumnInfo(name="pcs")
    var pcs: Int = 0,
    @ColumnInfo(name="barangLogDate")
    var barangLogDate: Date = Date(),
    @ColumnInfo(name="barangLogRef")
    var barangLogRef: String = "",
    @ColumnInfo(name="barangLogKet")
    var barangLogKet: String = "",
    // FIX 1: Add defaultValue='0' to match the migration's DEFAULT 0
    @ColumnInfo(name="is_deleted", defaultValue = "0")
    var isDeleted: Boolean = false,

    // FIX 2: Add defaultValue='0' to match the migration's DEFAULT 0
    @ColumnInfo(name = "iLCloudId", defaultValue = "0")
    var iLCloudId: Long = 0L,

    // FIX 3: Add defaultValue='1' to match the migration's DEFAULT 1
    @ColumnInfo(name="needs_syncs", defaultValue = "1")
    var needsSyncs:Int=1
)
