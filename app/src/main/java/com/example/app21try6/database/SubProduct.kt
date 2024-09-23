package com.example.app21try6.database

import androidx.room.*

@Entity(tableName = "sub_table",
    foreignKeys = [ForeignKey(entity = Product::class,
        parentColumns = ["product_id"],
        childColumns = ["product_code"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Brand::class,
                parentColumns = ["brand_id"],
                childColumns = ["brand_code"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = Category::class,
                parentColumns = ["category_id"],
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
    @PrimaryKey(autoGenerate = true)
    var sub_id:Int = 0,
    @ColumnInfo(name="sub_name")
    var sub_name:String = "emtpy",
    @ColumnInfo(name="roll_u")
    var roll_u:Int = 0,
    @ColumnInfo(name="roll_b_t")
    var roll_bt:Int = 0,
    @ColumnInfo(name="roll_s_t")
    var roll_st:Int = 0,
    @ColumnInfo(name="roll_k_t")
    var roll_kt:Int = 0,
    @ColumnInfo(name="roll_b_g")
    var roll_bg:Int = 0,
    @ColumnInfo(name="roll_s_g")
    var roll_sg:Int = 0,
    @ColumnInfo(name="roll_k_g")
    var roll_kg:Int = 0,
    @ColumnInfo(name="warna")
    var warna:String = "click to add",
    @ColumnInfo(name="ket")
    var ket:String = "click to add",
    @ColumnInfo(name="product_code")
    var product_code:Int = 0,
    @ColumnInfo(name="brand_code")
    var brand_code:Int = 0,
    @ColumnInfo(name="cath_code")
    var cath_code:Int = 0,
    @ColumnInfo(name="is_checked")
    var is_checked:Boolean = false,
    @ColumnInfo(name = "discountId")
    var discountId:Int?=null,
)