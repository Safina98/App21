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
    tableName = "inventory_purchase_table",
    foreignKeys = [
        ForeignKey(
            entity = Expenses::class,
            parentColumns = ["id"],
            childColumns = ["expensesId"],
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
            entity = SuplierTable::class,
            parentColumns = ["id"],
            childColumns = ["suplierId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["ref"], unique = true)]
)
@TypeConverters(DateTypeConverter::class)
data class InventoryPurchase(
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    @ColumnInfo(name="subProductId")
    var subProductId:Int?=null,//Foreign key
    @ColumnInfo(name="expensesId")
    var expensesId:Int?=null,//Foreign key
    @ColumnInfo(name="suplierId")
    var suplierId:Int?=null,
    @ColumnInfo(name="suplierName")
    var suplierName:String="",
    @ColumnInfo(name="subProductName")
    var subProductName:String="",
    @ColumnInfo(name="purchaseDate")
    var purchaseDate: Date = Date(),
    @ColumnInfo(name="batchCount")
    var batchCount:Double=0.0,
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="price")
    var price:Int=0,
    @ColumnInfo(name="totalPrice")
    var totalPrice:Double=0.0,
    @ColumnInfo(name="status")
    var status:String="",
    @ColumnInfo(name="ref")
    var ref:String="",
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "iPCloudId")
    var iPCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)