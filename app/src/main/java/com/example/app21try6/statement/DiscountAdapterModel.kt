package com.example.app21try6.statement

import androidx.room.ColumnInfo
import java.util.Date

data class DiscountAdapterModel (
    var id:Int?,  // Change discountId to id
    var discountName:String?=null,//unique
    var discountValue:Double?=null,
    var discountType:String?=null,
    var minimumQty:Double?=null,
    var date: Date?=null,
    var custLocation:String?=null,
    var sum_id:Long? = null,
    var expense_category_name:String?=null,
    var expense_name: String?=null,
    var expense_ammount:Int? = null,
    var expense_ref:String?=null
)