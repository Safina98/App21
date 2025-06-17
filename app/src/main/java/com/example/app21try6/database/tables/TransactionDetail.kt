package com.example.app21try6.database.tables

import androidx.room.*
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "trans_detail_table",
        foreignKeys = [
                ForeignKey(entity = TransactionSummary::class,
                parentColumns = ["sum_id"],
                childColumns = ["sum_id"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE),
                ForeignKey(entity = SubProduct::class,
                        parentColumns = ["sub_id"],
                        childColumns = ["sub_id"],
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE),
        ])
data class TransactionDetail(
        @PrimaryKey(autoGenerate = true)
        var trans_detail_id: Long = 0,
        @ColumnInfo(name="sum_id")
        var sum_id:Int = 0,
        @ColumnInfo(name = "trans_item_name")
        var trans_item_name:String = "",
        @ColumnInfo(name = "qty")
        var qty:Double = 0.0,
        @ColumnInfo(name = "trans_price")
        var trans_price:Int= 0,
        @ColumnInfo(name = "total_price")
        var total_price:Double = 0.0,
        @ColumnInfo(name = "is_prepared")
        var is_prepared:Boolean = false,
        @ColumnInfo(name = "is_cutted") //newly added colum
        var is_cutted:Boolean = false,//newly added column
        @ColumnInfo(name = "trans_detail_date")
        var trans_detail_date:Date? = null,
        @ColumnInfo(name = "unit")
        var unit:String?=null,
        @ColumnInfo(name = "unit_qty")
        var unit_qty:Double=1.0,
        @ColumnInfo(name = "item_position")
        var item_position:Int=0,
        @ColumnInfo(name = "sub_id")
        var sub_id:Int? = null,
        @ColumnInfo(name = "product_capital")
        var product_capital:Int=0,
        )

