package com.example.app21try6.database

import androidx.room.*

@Entity(tableName = "product_table",
        foreignKeys = [
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
                //recently added
                ForeignKey(entity = DiscountTable::class,
                        parentColumns = ["discountId"],
                        childColumns = ["discountId"],
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE),

        ]
)
data class Product(
        @PrimaryKey(autoGenerate = true)
        var product_id:Int = 0,
        @ColumnInfo(name="product_name")
        var product_name:String = "emtpy",
        @ColumnInfo(name="product_price")
        var product_price:Int = 0,
        @ColumnInfo(name="product_capital")
        var product_capital:Int = 0,
        @ColumnInfo(name="checkBoxBoolean")
        var checkBoxBoolean: Boolean = false,
        @ColumnInfo(name="best_selling")
        var bestSelling: Boolean = false,
        @ColumnInfo(name="brand_code")
        var brand_code:Int=0,
        @ColumnInfo(name = "cath_code")
        var cath_code:Int = 0,
        //recently added
        @ColumnInfo(name = "discountId")
        var discountId:Int?=null,

)