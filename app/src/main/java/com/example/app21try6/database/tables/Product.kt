package com.example.app21try6.database.tables

import androidx.room.*
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.DiscountTable

@Entity(tableName = "product_table",
        foreignKeys = [
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
    @ColumnInfo(name = "default_net")
        var default_net:Double = 0.0,
    @ColumnInfo(name = "alternate_price")
        var alternate_price:Double = 0.0,
    @ColumnInfo(name="brand_code")
        var brand_code: Long=0L,
    @ColumnInfo(name = "cath_code")
        var cath_code: Long = 0L,
    @ColumnInfo(name = "discountId")
        var discountId:Int?=null,
        //new columns
    @ColumnInfo(name="purchasePrice")
        var purchasePrice:Int?=null,
    @ColumnInfo(name="purchaseUnit")
        var puchaseUnit:String?=null,
    @ColumnInfo(name = "alternate_capital") //newly added
        var alternate_capital:Double = 0.0,
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "productCloudId")
    var productCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)