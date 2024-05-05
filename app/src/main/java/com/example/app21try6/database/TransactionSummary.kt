package com.example.app21try6.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*
import androidx.room.TypeConverter

class Converters {
        @TypeConverter
        fun toDate(timestamp: Long): Date {
                return Date(timestamp)
        }

        @TypeConverter
        fun toTimestamp(date: Date): Long {
                return date.time
        }
}

@Entity(tableName = "trans_sum_table")
data class TransactionSummary (
        @PrimaryKey(autoGenerate = true)
        var sum_id:Int = 0,
        @ColumnInfo(name = "cust_name")
        var cust_name:String = "",
        @ColumnInfo(name = "total_trans")
        var total_trans:Double=0.0,
        @ColumnInfo(name = "paid")
        var paid:Int=0,
        @ColumnInfo(name = "trans_date")
        var trans_date:String="",
        @ColumnInfo(name = "is_taken")
        var is_taken_:Boolean=false,
        @ColumnInfo(name = "is_paid_off")
        var is_paid_off:Boolean=false,
        @ColumnInfo(name = "ref")
        var ref:String=""
)
