package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app21try6.Constants
import java.util.Date


@Entity(tableName = "expense_category_table")
data class  ExpenseCategory (
    @PrimaryKey(autoGenerate = true)
    var id : Int=0,
    @ColumnInfo(name="expense_category_name")
    var expense_category_name:String = "",
    @ColumnInfo
    var categoryType:String = Constants.TIPEEXPENSECATOGORY.BEBAN,//new_column
    @ColumnInfo(name="is_periodic")
    var is_periodic:Boolean = false,
    @ColumnInfo(name="repeat_period")
    var repeat_period:String?=null,
    @ColumnInfo(name="repeat_date")
    var repeat_date:Date?=null,
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false,
    @ColumnInfo(name = "eCCloudId")
    var eCCloudId: Long = 0L,
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1
)
