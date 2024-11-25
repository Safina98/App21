package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.*

@Entity(tableName = "summary_table",
        foreignKeys = [
                ForeignKey(entity = Product::class,
                parentColumns = ["product_id"],
                childColumns = ["product_id"],
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.SET_NULL),
                ForeignKey(entity = SubProduct::class,
                        parentColumns = ["sub_id"],
                        childColumns = ["sub_id"],
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.SET_NULL)]
        )
@TypeConverters(DateTypeConverter::class)
data class Summary(
        @PrimaryKey(autoGenerate = true)
        var id_m :Int=0,
        @ColumnInfo(name = "year")
        var year: Int= 2030,
        @ColumnInfo(name = "month")
        var month: String = "empty",
        @ColumnInfo(name = "month_number")
        var month_number: Int = 0,
        @ColumnInfo(name = "day")
        var day: Int = 0,
        @ColumnInfo(name = "day_name")
        var day_name: String = "empty",
        @ColumnInfo(name = "date")
        var date:Date?=Date(),
        @ColumnInfo(name = "item_name")
        var item_name:String = "empty",
        @ColumnInfo(name = "item_sold")
        var item_sold:Double = 0.0,
        @ColumnInfo(name = "price")
        var price:Double = 0.0,
        @ColumnInfo(name = "total_income")
        var total_income : Double = 0.0,
        @ColumnInfo(name = "product_id", index = true)
        var product_id: Int? = null,
        @ColumnInfo(name = "sub_id", index = true)
        var sub_id: Int? = null,
        //new added column
        @ColumnInfo(name = "product_capital")
        var product_capital:Int=0,
)