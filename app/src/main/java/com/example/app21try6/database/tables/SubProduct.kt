package com.example.app21try6.database.tables

import androidx.room.*

@Entity(tableName = "sub_table",
    foreignKeys = [ForeignKey(entity = Product::class,
        parentColumns = ["productCloudId"],
        childColumns = ["productCloudId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Brand::class,
                parentColumns = ["brandCloudId"],
                childColumns = ["brand_code"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Category::class,
                parentColumns = ["categoryCloudId"],
                childColumns = ["cath_code"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = DiscountTable::class,
            parentColumns = ["discountId"],
            childColumns = ["discountId"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE),

    ]
)
data class SubProduct(
    @PrimaryKey
    @ColumnInfo(name = "sPCloudId")
    var sPCloudId: Long = 0L,
    @ColumnInfo(name="sub_name")
    var sub_name:String = "emtpy",
    @ColumnInfo(name="roll_u")
    var roll_u:Int = 0,
    @ColumnInfo(name="warna")
    var warna:String = "click to add",
    @ColumnInfo(name="ket")
    var ket:String = "click to add",
    @ColumnInfo(name="productCloudId")
    var productCloudId: Long = 0,
    @ColumnInfo(name="brand_code")
    var brand_code:Long = 0L,
    @ColumnInfo(name="cath_code")
    var cath_code:Long = 0L,
    @ColumnInfo(name="is_checked")
    var is_checked:Boolean = false,
    @ColumnInfo(name = "discountId")
    var discountId:Int?=null,
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)