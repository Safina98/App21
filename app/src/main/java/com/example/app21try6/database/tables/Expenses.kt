package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app21try6.database.DateTypeConverter
import java.util.Date


@Entity(
    tableName = "expenses_table",
    foreignKeys = [
        ForeignKey(
            entity = TransactionSummary::class,
            parentColumns = ["tSCloudId"],
            childColumns = ["tSCloudId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExpenseCategory::class,
            parentColumns = ["id"],
            childColumns = ["expense_category_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(DateTypeConverter::class)
data class  Expenses (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    @ColumnInfo(name="tSCloudId")
    var tSCloudId:Long? = null,
    @ColumnInfo(name="expense_category_id")
    var expense_category_id:Int = 0,
    @ColumnInfo(name="expense_name")
    var expense_name: String="",
    @ColumnInfo(name="expense_ammount")
    var expense_ammount:Int? = null,
    @ColumnInfo(name="expense_date")
    var expense_date:Date? = null,
    @ColumnInfo(name = "expense_ref")
    var expense_ref:String="",
    @ColumnInfo(name = "is_keeped")
    var is_keeped:Boolean=false,
    @ColumnInfo(name="is_deleted")
    var isDeleted: Boolean = false, //newly added cloumn
    @ColumnInfo(name = "expenseCloudId")
    var expenseCloudId: Long = 0L,//newly added cloumn
    @ColumnInfo(name="needs_syncs")
    var needsSyncs:Int=1//newly added cloumn
)





