package com.example.app21try6.database.models

import androidx.room.ColumnInfo
import java.util.Date

data class TransSumModel(var sum_id:Int = 0,

                         var cust_name:String = "",

                         var total_trans:Double=0.0,

                         var paid:Int=0,

                         var trans_date: Date? =null,

                         var is_taken:Boolean=false,

                         var is_paid_off:Boolean=false,

                         var is_keeped:Boolean=false,

                         var ref:String="",

                         var sum_note:String?=null,

                         var custId:Int?=null)
